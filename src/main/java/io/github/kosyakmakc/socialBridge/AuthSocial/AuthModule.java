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
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.IMinecraftCommand;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.ISocialCommand;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.ITranslationSource;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IModuleLoader;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.ISocialModule;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.Identifier;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.IdentifierType;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthModule implements ISocialModule, IAuthModule {
    public static final UUID ID = UUID.fromString("11752e9b-8968-42ca-8513-6ce3e52a27b4");
    public static final Version SocialBridge_CompabilityVersion = new Version("0.6.0");
    public static final String NAME = "authsocial";
    private Logger logger;
    private ISocialBridge bridge;

    private final IModuleLoader loader;
    public final AuthEvents events = new AuthEvents();

    public final List<ISocialCommand> socialCommands = List.of(
            new CommitLoginCommand(this),
            new LogoutLoginCommand(this)
    );

    public final List<IMinecraftCommand> minecraftCommands = List.of(
            new LoginCommand(this),
            new StatusCommand(this)
    );

    public final List<ITranslationSource> translationSources = List.of(
            new English(),
            new Russian()
    );

    public AuthModule(IModuleLoader loader) {
        this.loader = loader;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public IModuleLoader getLoader() {
        return loader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<LoginState> authorize(SocialUser socialUser, UUID minecraftId) {
        return bridge
            .queryDatabase(databaseContext -> {
                try {
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
    public CompletableFuture<List<SocialUser>> tryGetSocialUsers(UUID minecraftId) {
        return bridge.queryDatabase(databaseContext -> {
            @SuppressWarnings("rawtypes")
            var associations = new LinkedList<Association>();
            try {
                for (var type : IdentifierType.values()) {
                    var associationType = getClassByType(type);
                    if (associationType == null) {
                        continue;
                    }
                    
                    @SuppressWarnings({ "rawtypes", "unchecked" })
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
                    var socialPlatform = bridge.getSocialPlatform(association.getSocialPlatformId());
                    return socialPlatform.tryGetUser(new Identifier(null, association.getSocialUserId()));
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
    public CompletableFuture<MinecraftUser> tryGetMinecraftUser(SocialUser socialUser) {
        return bridge.queryDatabase(databaseContext -> {
            try {
                var associationType = getClassByType(socialUser.getId().type());
                if (associationType == null) {
                    return null;
                }

                @SuppressWarnings({ "rawtypes", "unchecked" })
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
                return bridge.getMinecraftPlatform().tryGetUser(uuid);
            }
        });
    }

    @Override
    public CompletableFuture<MinecraftUser> logoutUser(SocialUser socialUser) {
        return bridge
            .queryDatabase(databaseContext -> {
                try {
                    var associationType = getClassByType(socialUser.getId().type());
                    if (associationType == null) {
                        return null;
                    }

                    @SuppressWarnings({ "rawtypes", "unchecked" })
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
            .thenCompose(minecraftId -> bridge.getMinecraftPlatform().tryGetUser(minecraftId))
            .thenCompose(minecraftUser -> {
                if (minecraftUser != null) {
                    return events.logout
                            .invoke(new LogoutEvent(socialUser, minecraftUser))
                            .thenApply(Void -> minecraftUser);
                }

                return CompletableFuture.completedFuture(minecraftUser);
            });
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public CompletableFuture<Boolean> enable(ISocialBridge bridge) {
        logger = Logger.getLogger(bridge.getLogger().getName() + '.' + NAME);
        this.bridge = bridge;

        return bridge.queryDatabase(ctx -> {
            try {
                TableUtils.createTableIfNotExists(ctx.getConnectionSource(), Session.class);
                var daoSession = ctx.registerTable(Session.class);

                if (daoSession == null) {
                    throw new RuntimeException("Failed to create required database table - " + Session.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(ctx.getConnectionSource(), AssociationByInteger.class);
                var daoAssociationByInteger = ctx.registerTable(AssociationByInteger.class);
                if (daoAssociationByInteger == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByInteger.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(ctx.getConnectionSource(), AssociationByLong.class);
                var daoAssociationByLong = ctx.registerTable(AssociationByLong.class);
                if (daoAssociationByLong == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByLong.class.getSimpleName());
                }

                TableUtils.createTableIfNotExists(ctx.getConnectionSource(), AssociationByUUID.class);
                var daoAssociationByGuid = ctx.registerTable(AssociationByUUID.class);
                if (daoAssociationByGuid == null) {
                    throw new RuntimeException("Failed to create required database table - " + AssociationByUUID.class.getSimpleName());
                }

                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })
        .exceptionally(err -> {
            err.printStackTrace();
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> disable() {
        this.bridge = null;
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public ISocialBridge getBridge() {
        return bridge;
    }

    @Override
    public Version getCompabilityVersion() {
        return SocialBridge_CompabilityVersion;
    }

    @Override
    public List<ISocialCommand> getSocialCommands() {
        return socialCommands;
    }

    @Override
    public List<IMinecraftCommand> getMinecraftCommands() {
        return minecraftCommands;
    }

    @Override
    public List<ITranslationSource> getTranslations() {
        return translationSources;
    }

    @Override
    public String getName() {
        return NAME;
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

    @SuppressWarnings("rawtypes")
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