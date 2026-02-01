package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import java.util.HashMap;
import java.util.List;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialAttachmentReply;

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
        var placeholders = new HashMap<String, String>();
        placeholders.put("social-user-name", ctx.getSender().getName());

        var message = ctx.getSocialMessage();
        var replyMessage = message
                            .getAttachments()
                            .stream()
                            .filter(attachment -> attachment instanceof ISocialAttachmentReply)
                            .map(attachment -> (ISocialAttachmentReply) attachment)
                            .findFirst()
                            .orElse(null);

        if (replyMessage == null) {
            getBridge()
                .getLocalizationService()
                .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NO_REPLY, null)
                .thenCompose(msgTemplate -> message.sendReply(msgTemplate, placeholders));

            return;
        }

        var replyAuthor = replyMessage.getReply().getAuthor();
        module
            .tryGetMinecraftUser(replyAuthor, null)
            .thenCompose(replyMinecraftPlayer -> {
                if (replyMinecraftPlayer == null) {
                    return getBridge()
                            .getLocalizationService()
                            .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NOT_FOUND, null)
                            .thenCompose(msgTemplate -> message.sendReply(msgTemplate, placeholders));
                }

                placeholders.put("reply-minecraft-user-name", replyMinecraftPlayer.getName());

                return getBridge()
                        .getLocalizationService()
                        .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.SOCIAL_USER_INFO_COMMAND_SUCCESS, null)
                        .thenCompose(msgTemplate -> message.sendReply(msgTemplate, placeholders));
            });
    }

}
