package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.SocialCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.SocialCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
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
    public void execute(SocialCommandExecutionContext ctx, List<Object> args) {
        var placeholders = new HashMap<String, String>();
        var module = getBridge().getModule(AuthModule.class);
        var socialUser = ctx.getSender();

        module
            .tryGetMinecraftUser(socialUser, null)
            .thenAccept(minecraftUser -> {
                if (minecraftUser == null) {
                    getBridge()
                        .getLocalizationService()
                        .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_LOGIN, null)
                        .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));
                }
                minecraftUser
                    .hasPermission(permission)
                    .thenAccept(isHavePermission -> {
                        if (!isHavePermission) {
                            getBridge()
                                .getLocalizationService()
                                .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_PERMISSION, null)
                                .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));
                        }

                        execute(ctx, minecraftUser, args);
                    });
            });
    }

}
