package io.github.kosyakmakc.socialBridge.AuthSocial;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands.LoginCommand;
import io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands.StatusCommand;
import io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands.CommitLoginCommand;
import io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands.LogoutLoginCommand;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.Association;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.AssociationByUUID;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.AssociationByInteger;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.AssociationByLong;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.AssociationByString;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.Session;
import io.github.kosyakmakc.socialBridge.AuthSocial.Translations.English;
import io.github.kosyakmakc.socialBridge.AuthSocial.Translations.Russian;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.LoginState;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IModuleLoader;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Modules.SocialModule;
import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.Identifier;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.IdentifierType;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthModule extends SocialModule implements IAuthModule {
    public static final UUID ID = UUID.fromString("11752e9b-8968-42ca-8513-6ce3e52a27b4");
    public static final Version SocialBridge_CompabilityVersion = new Version("0.9.1");
    public static final String NAME = "authsocial";
    private Logger logger;

    public final AuthEvents events = new AuthEvents();

    public AuthModule(IModuleLoader loader) {
        super(loader, SocialBridge_CompabilityVersion, ID, NAME);

        addMinecraftCommand(new LoginCommand(this));
        addMinecraftCommand(new StatusCommand(this));

        addSocialCommand(new CommitLoginCommand(this));
        addSocialCommand(new LogoutLoginCommand(this));

        addTranslationSource(new English());
        addTranslationSource(new Russian());
    }

    @Override
    public CompletableFuture<LoginState> authorize(SocialUser socialUser, UUID minecraftId, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> authorizeInternal(socialUser, minecraftId, transaction2))
            : authorizeInternal(socialUser, minecraftId, transaction);
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<LoginState> authorizeInternal(SocialUser socialUser, UUID minecraftId, ITransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var databaseContext = transaction.getDatabaseContext();

                var associationType = getClassByType(socialUser.getId().type());
                if (associationType == null) {
                    return LoginState.NotSupportedPlatform;
                }

                var dao = databaseContext.getDaoTable(associationType);
                var existedRows = dao
                    .queryBuilder()
                        .where()
                        .eq(Association.SOCIAL_PLATFORM_ID_FIELD_NAME, socialUser.getPlatform().getId())
                        .and()
                        .eq(Association.MINECRAFT_ID_FIELD_NAME, minecraftId)
                        .and()
                        .eq(Association.IS_DELETED_FIELD_NAME, false)
                        .countOf();
                    
                if (existedRows > 0) {
                    return LoginState.DuplicationError;
                } else {
                    var association = createAssociation(socialUser, minecraftId);
                    dao.create(association);
                }
                
                return LoginState.Commited;
            }
            catch (SQLException err) {
                err.printStackTrace();
                return LoginState.NotCommited;
            }
        })
        .thenCompose(loginState -> {
            if (loginState == LoginState.Commited) {
                return events.login
                    .invoke(new LoginEvent(socialUser, minecraftId))
                    .thenApply(Void -> loginState);
            }
            
            return CompletableFuture.completedFuture(loginState);
        });
    }

    @Override
    public CompletableFuture<List<SocialUser>> tryGetSocialUsers(UUID minecraftId, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> tryGetSocialUsersInternal(minecraftId, transaction2))
            : tryGetSocialUsersInternal(minecraftId, transaction);
    }

    private CompletableFuture<List<SocialUser>> tryGetSocialUsersInternal(UUID minecraftId, ITransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            var associations = new LinkedList<Association>();
            try {
                for (var type : IdentifierType.values()) {
                    var associationType = getClassByType(type);
                    if (associationType == null) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    var dao = (Dao<Association, Object>) databaseContext.getDaoTable(associationType);
                    var query = dao
                        .queryBuilder()
                        .where()
                        .eq(Association.MINECRAFT_ID_FIELD_NAME, minecraftId)
                        .and()
                        .eq(Association.IS_DELETED_FIELD_NAME, false);

                    try (var cursor = query.iterator()) {
                        while (cursor.hasNext()) {
                            associations.add(cursor.next());
                        }
                    }
                }
                return associations;
            }
            catch (Exception err) {
                err.printStackTrace();
                return associations;
            }
        })
        .thenCompose(associasions -> {
            var tasks = associasions
                .stream()
                .map(association -> {
                    var socialPlatform = getBridge().getSocialPlatform(association.getSocialPlatformId());
                    return socialPlatform.tryGetUser(new Identifier(null, association.getSocialUserId()), transaction);
                })
                .toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(tasks).thenApply(Void -> tasks);
        })
        .thenApply(tasks -> Arrays.stream(tasks).map(x -> { 
            try {
                return x.get() instanceof SocialUser user ? user : null;
            }
            catch (InterruptedException | ExecutionException err) {
                err.printStackTrace();
                return null;
            }
        }).toList());
    }

    @Override
    public CompletableFuture<MinecraftUser> tryGetMinecraftUser(SocialUser socialUser, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> tryGetMinecraftUserInternal(socialUser, transaction2))
            : tryGetMinecraftUserInternal(socialUser, transaction);
    }

    private CompletableFuture<MinecraftUser> tryGetMinecraftUserInternal(SocialUser socialUser, ITransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var associationType = getClassByType(socialUser.getId().type());
                if (associationType == null) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                var dao = (Dao<Association, Object>) databaseContext.getDaoTable(associationType);

                var association = dao
                        .queryBuilder()
                        .where()
                        .eq(Association.SOCIAL_PLATFORM_ID_FIELD_NAME, socialUser.getPlatform().getId())
                        .and()
                        .eq(Association.SOCIAL_USER_ID_FIELD_NAME, socialUser.getId().value())
                        .and()
                        .eq(Association.IS_DELETED_FIELD_NAME, false)
                        .queryForFirst();

                if (association != null) {
                    return association.getMinecraftId();
                }

                return null;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "failed get minecraft user", e);
                return null;
            }
        })
        .thenCompose(uuid -> {
            if (uuid == null) {
                return CompletableFuture.completedStage(null);
            }
            else {
                return getBridge().getMinecraftPlatform().tryGetUser(uuid);
            }
        });
    }

    @Override
    public CompletableFuture<MinecraftUser> logoutUser(SocialUser socialUser, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> logoutUserInternal(socialUser, transaction2))
            : logoutUserInternal(socialUser, transaction);
    }

    private CompletableFuture<MinecraftUser> logoutUserInternal(SocialUser socialUser, ITransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
                var databaseContext = transaction.getDatabaseContext();

                try {
                    var associationType = getClassByType(socialUser.getId().type());
                    if (associationType == null) {
                        return null;
                    }

                    @SuppressWarnings("unchecked")
                    var dao = (Dao<Association, Object>) databaseContext.getDaoTable(associationType);

                    var association = dao
                            .queryBuilder()
                            .where()
                            .eq(Association.SOCIAL_PLATFORM_ID_FIELD_NAME, socialUser.getPlatform().getId())
                            .and()
                            .eq(Association.SOCIAL_USER_ID_FIELD_NAME, socialUser.getId().value())
                            .and()
                            .eq(Association.IS_DELETED_FIELD_NAME, false)
                            .queryForFirst();

                    if (association != null) {
                        association.Delete();
                        dao.update(association);
                        return association.getMinecraftId();
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "failed get minecraft user", e);
                    return null;
                }
            })
            .thenCompose(minecraftId -> getBridge().getMinecraftPlatform().tryGetUser(minecraftId))
            .thenCompose(minecraftUser -> {
                if (minecraftUser != null) {
                    return events.logout
                            .invoke(new LogoutEvent(socialUser, minecraftUser))
                            .thenApply(Void -> minecraftUser);
                }

                return CompletableFuture.completedFuture(minecraftUser);
            });
    }

    @Override
    public CompletableFuture<Void> enumerateAssociations(Consumer<SocialUser> handler, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> enumerateAssociationsInternal(handler, transaction2))
            : enumerateAssociationsInternal(handler, transaction);
    }

    private CompletableFuture<Void> enumerateAssociationsInternal(Consumer<SocialUser> handler, ITransaction transaction) {
        return enumerateAssociationsInternal(socialUser -> {
            handler.accept(socialUser);
            return CompletableFuture.completedFuture(null);
        }, transaction);
    }

    @Override
    public CompletableFuture<Void> enumerateAssociations(Function<SocialUser, CompletableFuture<Void>> handler, ITransaction transaction) {
        return transaction == null
            ? getBridge().doTransaction(transaction2 -> enumerateAssociationsInternal(handler, transaction2))
            : enumerateAssociationsInternal(handler, transaction);
    }

    private CompletableFuture<Void> enumerateAssociationsInternal(Function<SocialUser, CompletableFuture<Void>> handler, ITransaction transaction) {
        var databaseContext = transaction.getDatabaseContext();

        try {
            return iterateCursor(Arrays.stream(IdentifierType.values()).iterator(), type -> {
                var associationType = getClassByType(type);
                if (associationType == null) {
                    return CompletableFuture.completedFuture(null);
                }

                @SuppressWarnings("unchecked")
                var dao = (Dao<Association, Object>) databaseContext.getDaoTable(associationType);

                try {
                    var query = dao
                        .queryBuilder()
                        .where()
                        .eq(Association.IS_DELETED_FIELD_NAME, false);

                    try (var cursor = query.iterator()) {
                        return iterateCursor(cursor, association -> {
                            var platform = getBridge().getSocialPlatform(association.getSocialPlatformId());
                            if (platform != null) {
                                return platform
                                    .tryGetUser(new Identifier(type, association.getSocialUserId()), transaction)
                                    .thenCompose(socialUser -> {
                                        if (socialUser != null) {
                                            return handler.apply(socialUser);
                                        }
                                        return CompletableFuture.completedFuture(null);
                                    });
                            }
                            else {
                                return CompletableFuture.completedFuture(null);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        return CompletableFuture.completedFuture(null);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    return CompletableFuture.completedFuture(null);
                }
            });
        }
        catch (Exception err) {
            err.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

    private <T> CompletableFuture<Void> iterateCursor(Iterator<T> cursor, Function<T, CompletableFuture<Void>> handler) {
        if (cursor.hasNext()) {
            var item = cursor.next();

            return handler
                .apply(item)
                .thenCompose(Void -> iterateCursor(cursor, handler));
        }

        return CompletableFuture.completedFuture(null);
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public CompletableFuture<Boolean> enable(ISocialBridge bridge) {
        super.enable(bridge);
        
        logger = Logger.getLogger(bridge.getLogger().getName() + '.' + NAME);

        return bridge.doTransaction(transaction -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                TableUtils.createTableIfNotExists(databaseContext.getConnectionSource(), Session.class);
                var daoSession = databaseContext.registerTable(Session.class);

                if (daoSession == null) {
                    throw new RuntimeException("Failed to create required database table - " + Session.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(databaseContext.getConnectionSource(), AssociationByInteger.class);
                var daoAssociationByInteger = databaseContext.registerTable(AssociationByInteger.class);
                if (daoAssociationByInteger == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByInteger.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(databaseContext.getConnectionSource(), AssociationByLong.class);
                var daoAssociationByLong = databaseContext.registerTable(AssociationByLong.class);
                if (daoAssociationByLong == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByLong.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(databaseContext.getConnectionSource(), AssociationByUUID.class);
                var daoAssociationByGuid = databaseContext.registerTable(AssociationByUUID.class);
                if (daoAssociationByGuid == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByUUID.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(databaseContext.getConnectionSource(), AssociationByString.class);
                var daoAssociationByString = databaseContext.registerTable(AssociationByString.class);
                if (daoAssociationByString == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByString.class.getSimpleName());
                }

                return CompletableFuture.completedFuture(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })
        .exceptionally(err -> {
            err.printStackTrace();
            return false;
        });
    }

    @SuppressWarnings("rawtypes")
    private Class getClassByType(IdentifierType type) {
        switch (type) {
            case IdentifierType.Integer:
                return AssociationByInteger.class;
            case IdentifierType.Long:
                return AssociationByLong.class;
            case IdentifierType.UUID:
                return AssociationByUUID.class;
            case IdentifierType.String:
                return AssociationByString.class;

            default:
                return null;
        }
    }

    private Association createAssociation(SocialUser socialUser, UUID minecraftId) {
        var socialPlatformId = socialUser.getPlatform().getId();
        var identifier = socialUser.getId();
        var type = identifier.type();
        switch (type) {
            case IdentifierType.Integer:
                return new AssociationByInteger(minecraftId, socialPlatformId, (Integer) identifier.value());
            case IdentifierType.Long:
                return new AssociationByLong(minecraftId, socialPlatformId, (Long) identifier.value());
            case IdentifierType.UUID:
                return new AssociationByUUID(minecraftId, socialPlatformId, (UUID) identifier.value());
            case IdentifierType.String:
                return new AssociationByString(minecraftId, socialPlatformId, identifier.value().toString());

            default:
                return null;
        }
    }
}