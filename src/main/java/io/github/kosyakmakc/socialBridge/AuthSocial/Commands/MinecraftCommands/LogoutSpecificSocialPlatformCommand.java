// package io.github.kosyakmakc.socialBridge.AuthSocial.Commands.MinecraftCommands;

// import java.util.HashMap;
// import java.util.List;
// import java.util.concurrent.CompletableFuture;

// import javax.security.auth.AuthPermission;

// import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
// import io.github.kosyakmakc.socialBridge.AuthSocial.IAuthModule;
// import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
// import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthPermissions;
// import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
// import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
// import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;
// import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

// public class LogoutSpecificSocialPlatformCommand extends MinecraftCommandBase {
//     private final AuthModule module;

//     public LogoutSpecificSocialPlatformCommand(AuthModule module) {
//         super(
//             "logout",
//             AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_DESCRIPTION,
//             AuthPermissions.CAN_LOGOUT,
//             List.of(
//                 CommandArgument.ofGreedyString("social platform", () -> CompletableFuture.completedFuture(getBridge().getSocialPlatforms().stream().map(platform -> platform.getPlatformName()).toArray(String[]::new)))
//             )
//         );
//         this.module = module;
//     }

//     @Override
//     public void execute(MinecraftCommandExecutionContext ctx, List<Object> args) {
//         var socialPlatformName = (String) args.get(0);
//         var placeholders = new HashMap<String, String>();
//         placeholders.put("minecraft-user-name", ctx.getSender().getName());

//         var socialPlatform = getBridge()
//                                 .getSocialPlatforms()
//                                 .stream()
//                                 .filter(x -> x.getPlatformName().equalsIgnoreCase(socialPlatformName))
//                                 .findFirst()
//                                 .orElse(null);
//         if (socialPlatform == null) {
//             getBridge()
//                 .getLocalizationService()
//                 .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_PLATFORM, null)
//                 .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));

//             return;
//         }

//         placeholders.put("social-platform-name", ctx.getSender().getName());

//         module
//             .tryGetSocialUsers(ctx.getSender().getId(), null)
//             .thenCompose(SocialUsers -> {
//                 var socialUser = SocialUsers
//                                         .stream()
//                                         .filter(x -> x.getPlatform() == socialPlatform)
//                                         .findFirst()
//                                         .orElse(null);

//                 if (socialUser == null) {
//                     return getBridge()
//                         .getLocalizationService()
//                         .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_SOCIAL_USER, null)
//                         .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));
//                 }

//                 placeholders.put("social-user-name", socialUser.getName());

//                 return module.logoutUser(socialUser, null)
//                     .thenCompose(minecraftPlayer -> {
//                         if (minecraftPlayer == null) {
//                             return getBridge()
//                                 .getLocalizationService()
//                                 .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_FAILED, null)
//                                 .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));
//                         }

//                         return getBridge()
//                             .getLocalizationService()
//                             .getMessage(module, ctx.getSender().getLocale(), AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_SUCCESS, null)
//                             .thenAccept(msgTemplate -> ctx.getSender().sendMessage(msgTemplate, placeholders));
//                     })
//                     .thenRun(() -> { });
//             });
//         }
//     }
