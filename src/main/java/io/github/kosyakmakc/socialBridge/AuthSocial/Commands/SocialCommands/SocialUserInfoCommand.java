package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialAttachmentReply;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialMessage;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;

public class SocialUserInfoCommand extends AuthSocialBaseCommand {
    private final AuthModule module;

    public SocialUserInfoCommand(AuthModule module) {
        super(
            "info",
            AuthMessageKey.SOCIAL_USER_INFO_COMMAND_DESCRIPTION,
            AuthPermissions.CAN_CHECK_USER_INFO
        );

        this.module = module;
    }

    @Override
    public void execute(SocialCommandExecutionContext ctx, MinecraftUser minecraftPlayer, List<Object> args) {
        ctx.getSender().thenCompose(sender -> internalExecute(sender, ctx.getSocialMessage()));
    }

    private CompletableFuture<Boolean> internalExecute(SocialUser sender, ISocialMessage message) {
        var placeholders = new HashMap<String, String>();
            placeholders.put("social-user-name", sender.getName());

            var replyMessage = message
                                .getAttachments()
                                .stream()
                                .filter(attachment -> attachment instanceof ISocialAttachmentReply)
                                .map(attachment -> (ISocialAttachmentReply) attachment)
                                .findFirst()
                                .orElse(null);

            if (replyMessage == null) {
                return message.sendReply(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NO_REPLY, sender.getLocale(), placeholders, null);
            }

            return replyMessage.getReply()
                .getAuthor()
                .thenCompose(replyAuthor -> {
                    return module
                        .tryGetMinecraftUser(replyAuthor, null)
                        .thenCompose(replyMinecraftPlayer -> {
                            if (replyMinecraftPlayer == null) {
                                return message.sendReply(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NOT_FOUND, sender.getLocale(), placeholders, null);
                            }
                            
                            placeholders.put("reply-minecraft-user-name", replyMinecraftPlayer.getName());
                            
                            return message.sendReply(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_SUCCESS, sender.getLocale(), placeholders, null);
                        });
                });
    }

}
