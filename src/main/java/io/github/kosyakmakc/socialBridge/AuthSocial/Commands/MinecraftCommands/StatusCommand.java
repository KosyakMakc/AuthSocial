package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        var getHeaderTemplateTask = getBridge().getLocalizationService().getMessage(module, locale, AuthMessageKey.STATUS_COMMAND_HEADER, null);
        var getRecordTemplateTask = getBridge().getLocalizationService().getMessage(module, locale, AuthMessageKey.STATUS_COMMAND_RECORD, null);
        var getEmptyTemplateTask = getBridge().getLocalizationService().getMessage(module, locale, AuthMessageKey.STATUS_COMMAND_EMPTY, null);
        var socialUsersTask = module.tryGetSocialUsers(minecraftUser.getId(), null);

        CompletableFuture.allOf(new CompletableFuture[] {
            socialUsersTask,
            getHeaderTemplateTask,
            getRecordTemplateTask,
            getEmptyTemplateTask
        })
        .thenRun(() -> {
            try {
                minecraftUser.sendMessage(getHeaderTemplateTask.get(), new HashMap<>());
                var users = socialUsersTask.get();
                var isAny = !users.isEmpty();

                for (var user : users) {
                    var placeholders = new HashMap<String, String>();
                    placeholders.put("social-platform-name", user.getPlatform().getPlatformName());
                    placeholders.put("social-user-name", user.getName());
                    minecraftUser.sendMessage(getRecordTemplateTask.get(), placeholders);
                }

                if (!isAny) {
                    minecraftUser.sendMessage(getEmptyTemplateTask.get(), new HashMap<>());
                }
            }
            catch (InterruptedException | ExecutionException err) {
                getBridge().getLocalizationService().getMessage(module, locale, MessageKey.INTERNAL_SERVER_ERROR, null)
                .thenAccept(msgTemplate -> {
                    minecraftUser.sendMessage(msgTemplate, new HashMap<>());
                });
            }
        });
    }
}
