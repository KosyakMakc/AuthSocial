package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;

import java.util.HashMap;
import java.util.List;

public class StatusCommand extends MinecraftCommandBase {
    private final AuthModule module;

    public StatusCommand(AuthModule module) {
        super("status", AuthMessageKey.STATUS_COMMAND_DESCRIPTION, AuthPermissions.CAN_STATUS);
        this.module = module;
    }

    @Override
    public void execute(MinecraftCommandExecutionContext ctx, List<Object> list) {
        var minecraftUser = ctx.getSender();
        var locale = minecraftUser.getLocale();

        module
            .tryGetSocialUsers(minecraftUser.getId(), null)
            .thenAccept(users -> {
                minecraftUser.sendMessage(AuthMessageKey.STATUS_COMMAND_HEADER, locale, new HashMap<>(), null);
                var isAny = !users.isEmpty();

                for (var user : users) {
                    var placeholders = new HashMap<String, String>();
                    placeholders.put("social-platform-name", user.getPlatform().getPlatformName());
                    placeholders.put("social-user-name", user.getName());
                    minecraftUser.sendMessage(AuthMessageKey.STATUS_COMMAND_RECORD, locale, placeholders, null);
                }

                if (!isAny) {
                    minecraftUser.sendMessage(AuthMessageKey.STATUS_COMMAND_EMPTY, locale, new HashMap<>(), null);
                }
            });
    }
}
