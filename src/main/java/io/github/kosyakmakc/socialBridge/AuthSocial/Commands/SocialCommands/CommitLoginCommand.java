package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables.Session;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.LoginState;
import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CommitLoginCommand extends SocialCommandBase {
    private final AuthModule module;

    public CommitLoginCommand(AuthModule module) {
        super(
            "login",
            AuthMessageKey.COMMIT_LOGIN_DESCRIPTION,
            List.of(
                CommandArgument.ofInteger("auth-code")));
        this.module = module;
    }

    @Override
    public void execute(SocialCommandExecutionContext context, List<Object> args) {
        var bridge = getBridge();
        var logger = module.getLogger();

        var message = context.getSocialMessage();

        var authCode = (int) args.getFirst();
        var placeholders = new HashMap<String, String>();

        context.getSender().thenCompose(sender -> {
            return bridge.doTransaction(transaction -> {
                var databaseContext = transaction.getDatabaseContext();

                try {
                    var availableSessions = databaseContext.getDaoTable(Session.class).queryBuilder()
                    .orderBy(Session.EXPIRED_AT_FIELD_NAME, true)
                    .where()
                    .eq(Session.AUTH_CODE_FIELD_NAME, authCode)
                    .and()
                    .eq(Session.IS_SPENT_FIELD_NAME, false)
                    .and()
                    .gt(Session.EXPIRED_AT_FIELD_NAME, Date.from(Instant.now()))
                    .query();

                    if (availableSessions.isEmpty()) {
                        return CompletableFuture.completedFuture(null);
                    }

                    var session = availableSessions.getFirst();

                    session.spend();
                    databaseContext.getDaoTable(Session.class).update(session);

                    return CompletableFuture.completedFuture(session.getMinecraftId());
                }
                catch (SQLException e) {
                    logger.log(Level.SEVERE, "failed to commit login", e);
                    return CompletableFuture.completedFuture(null);
                }
            })
            .thenCompose(minecraftId -> {
                if (minecraftId != null) {
                    return module.authorize(sender, minecraftId, null);
                }
                else {
                    return CompletableFuture.completedFuture(LoginState.NotCommited);
                }
            })
            .thenAccept(loginState -> {
                placeholders.put("social-platform-name", sender.getPlatform().getPlatformName());
                placeholders.put("social-user-name", sender.getName());
                switch (loginState) {
                    case Commited -> {
                        logger.info(sender.getName() + " success commited login to " + sender.getPlatform().getPlatformName() + " platform");
                        
                        module
                        .tryGetMinecraftUser(sender, null)
                        .thenAccept(minecraftUser -> {
                            minecraftUser.sendMessage(AuthMessageKey.COMMIT_LOGIN_MINECRAFT_SUCCESS, sender.getLocale(), placeholders, null);
                            message.sendReply(AuthMessageKey.COMMIT_LOGIN_SOCIAL_SUCCESS, sender.getLocale(), placeholders, null);
                        });
                        
                    }
                    case NotCommited -> {
                        logger.info(sender.getName() + " failed to commit login");
                        message.sendReply(AuthMessageKey.COMMIT_LOGIN_FAILED, sender.getLocale(), placeholders, null);
                    }
                    case DuplicationError -> {
                        logger.info(sender.getName() + " duplicating his logins to " + sender.getPlatform().getPlatformName() + ", ignoring it...");
                        message.sendReply(AuthMessageKey.COMMIT_LOGIN_ALREADY_LOGGED, sender.getLocale(), placeholders, null);
                    }
                    case NotSupportedPlatform -> {
                        logger.info(sender.getName() + " trying to commit on not supported platform " + sender.getPlatform().getPlatformName() + ", ignoring it...");
                        message.sendReply(AuthMessageKey.UNSUPPORTED_PLATFORM, sender.getLocale(), placeholders, null);
                    }
                    default -> throw new IllegalArgumentException("Unexpected value: " + loginState);
                }
            });
        });
    }
}
