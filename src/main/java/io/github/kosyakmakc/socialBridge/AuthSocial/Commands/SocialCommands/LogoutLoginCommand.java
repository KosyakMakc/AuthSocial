package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;

import java.util.HashMap;
import java.util.List;

public class LogoutLoginCommand extends SocialCommandBase {
    private final AuthModule module;

    public LogoutLoginCommand(AuthModule module) {
        super("logout", AuthMessageKey.LOGOUT_DESCRIPTION);
        this.module = module;
    }

    @Override
    public void execute(SocialCommandExecutionContext context, List<Object> args) {
        var logger = module.getLogger();

        var message = context.getSocialMessage();

        context
            .getSender()
            .thenCompose(sender -> {
                var platformName = sender.getPlatform().getPlatformName();
                var socialName = sender.getName();

                var placeholders = new HashMap<String, String>();
                placeholders.put("social-platform-name", sender.getPlatform().getPlatformName());
                placeholders.put("social-user-name", socialName);

                return module
                    .tryGetMinecraftUser(sender, null)
                    .thenCompose(player -> {
                        if (player == null) {
                            logger.info("social(" + sender.getName() + ") failed to logout - not authenticated.");
                            return message.sendReply(AuthMessageKey.LOGOUT_FAILED, sender.getLocale(), placeholders, null);
                        }
                        var minecraftName = player.getName();
                        return module
                            .logoutUser(sender, null)
                            .thenCompose(minecraftId -> {
                                if (minecraftId != null) {
                                    placeholders.put("minecraft-user-name", minecraftName);
                                    
                                    logger.info("minecraft(" + minecraftName + ") is logout from " + platformName + " platform.");
                                    
                                    player.sendMessage(AuthMessageKey.LOGOUT_SUCCESS_MINECRAFT, sender.getLocale(), placeholders, null);
                                    
                                    return message.sendReply(AuthMessageKey.LOGOUT_SUCCESS, sender.getLocale(), placeholders, null);
                                }
                                else {
                                    logger.info("social(" + sender.getName() + ") failed to logout - not authenticated.");
                                    return message.sendReply(AuthMessageKey.LOGOUT_FAILED, sender.getLocale(), placeholders, null);
                                }
                            });
                    });
            });
    }
}
