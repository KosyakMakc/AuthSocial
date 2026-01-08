package io.github.kosyakmakc.socialBridge.AuthSocial;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.LoginState;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;

public interface IAuthModule {
    CompletableFuture<LoginState> authorize(SocialUser socialUser, UUID minecraftId);
    CompletableFuture<List<SocialUser>> tryGetSocialUsers(UUID minecraftId);
    CompletableFuture<MinecraftUser> tryGetMinecraftUser(SocialUser socialUser);
    CompletableFuture<MinecraftUser> logoutUser(SocialUser socialUser);
}