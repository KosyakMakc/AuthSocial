package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.AuthSocial.IAuthModule;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;

public class LogoutSpecificSocialPlatformCommand extends MinecraftCommandBase {
    private final IAuthModule module;
    private static CompletableFuture<String[]> socialNames = CompletableFuture.completedFuture(new String[] { "telegram" }); // to do calculate social in runtime

    public LogoutSpecificSocialPlatformCommand(IAuthModule module) {
        super(
            "logout",
            AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_DESCRIPTION,
            AuthPermissions.CAN_LOGOUT,
            List.of(
                // need ctx for computing suggestions
                CommandArgument.ofGreedyString("social platform", () -> socialNames)
            )
        );
        this.module = module;

        // CompletableFuture.completedFuture(getBridge().getSocialPlatforms().stream().map(platform -> platform.getPlatformName()).toArray(String[]::new))
    }

    @Override
    public void execute(MinecraftCommandExecutionContext ctx, List<Object> args) {
        var socialPlatformName = (String) args.get(0);
        var placeholders = new HashMap<String, String>();
        placeholders.put("minecraft-user-name", ctx.getSender().getName());

        var socialPlatform = getBridge()
                                .getSocialPlatforms()
                                .stream()
                                .filter(x -> x.getPlatformName().equalsIgnoreCase(socialPlatformName))
                                .findFirst()
                                .orElse(null);
        if (socialPlatform == null) {
            ctx.getSender().sendMessage(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_PLATFORM, ctx.getSender().getLocale(), placeholders, null);
            return;
        }

        placeholders.put("social-platform-name", socialPlatform.getPlatformName());

        module
            .tryGetSocialUsers(ctx.getSender().getId(), null)
            .thenCompose(SocialUsers -> {
                var socialUser = SocialUsers
                                        .stream()
                                        .filter(x -> x.getPlatform() == socialPlatform)
                                        .findFirst()
                                        .orElse(null);

                if (socialUser == null) {
                    return ctx.getSender().sendMessage(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_SOCIAL_USER, ctx.getSender().getLocale(), placeholders, null);
                }

                placeholders.put("social-user-name", socialUser.getName());

                return module
                    .logoutUser(socialUser, null)
                    .thenCompose(minecraftPlayer -> {
                        if (minecraftPlayer == null) {
                            return ctx.getSender().sendMessage(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_FAILED, ctx.getSender().getLocale(), placeholders, null);
                        }

                        return ctx.getSender().sendMessage(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_SUCCESS, ctx.getSender().getLocale(), placeholders, null);
                    });
            });
        }
    }
