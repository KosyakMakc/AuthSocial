package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import io.github.kosyakmakc.socialBridge.Utils.Permissions;

@SuppressWarnings("rawtypes")
public abstract class AuthSocialBaseCommand extends SocialCommandBase {
    private final String permission;

    public AuthSocialBaseCommand(String literal, MessageKey description) {
        this(literal, description, Permissions.NO_PERMISSION);
    }

    @SuppressWarnings("unchecked")
    public AuthSocialBaseCommand(String literal, MessageKey description, String permission) {
        this(literal, description, permission, new ArrayList());
    }

    public AuthSocialBaseCommand(String literal, MessageKey description, List<CommandArgument> argumentDefinition) {
        this(literal, description, Permissions.NO_PERMISSION, argumentDefinition);
    }

    public AuthSocialBaseCommand(String literal, MessageKey description, String permission, List<CommandArgument> argumentDefinition) {
        super(literal, description, argumentDefinition);
        this.permission = permission;
   }

   public abstract void execute (SocialCommandExecutionContext ctx, MinecraftUser minecraftPlayer, List<Object> args);

    @Override
    public void execute(SocialCommandExecutionContext context, List<Object> args) {
        var placeholders = new HashMap<String, String>();
        var module = getBridge().getModule(AuthModule.class);
        AtomicReference<SocialUser> senderReference = new AtomicReference<>();
        var message = context.getSocialMessage();
        
        context
            .getSender()
            .thenCompose(socialUser -> {
                senderReference.set(socialUser);
                return module.tryGetMinecraftUser(socialUser, null);
            })
            .thenAccept(minecraftUser -> {
                if (minecraftUser == null) {
                    message.sendReply(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_LOGIN, senderReference.get().getLocale(), placeholders, null);
                }
                minecraftUser
                    .hasPermission(permission)
                    .thenAccept(isHavePermission -> {
                        if (!isHavePermission) {
                            message.sendReply(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_PERMISSION, senderReference.get().getLocale(), placeholders, null);
                            return;
                        }

                        execute(context, minecraftUser, args);
                    });
            });
    }

}
