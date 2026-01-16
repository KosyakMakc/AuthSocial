package io.github.kosyakmakc.socialBridge.AuthSocial;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.LoginState;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;

public interface IAuthModule {
    CompletableFuture<LoginState> authorize(SocialUser socialUser, UUID minecraftId);
    CompletableFuture<LoginState> authorize(SocialUser socialUser, UUID minecraftId, ITransaction transaction);
    CompletableFuture<List<SocialUser>> tryGetSocialUsers(UUID minecraftId);
    CompletableFuture<List<SocialUser>> tryGetSocialUsers(UUID minecraftId, ITransaction transaction);
    CompletableFuture<MinecraftUser> tryGetMinecraftUser(SocialUser socialUser);
    CompletableFuture<MinecraftUser> tryGetMinecraftUser(SocialUser socialUser, ITransaction transaction);
    CompletableFuture<MinecraftUser> logoutUser(SocialUser socialUser);
    CompletableFuture<MinecraftUser> logoutUser(SocialUser socialUser, ITransaction transaction);
    CompletableFuture<Void> enumerateAssociations(Consumer<SocialUser> handler);
    CompletableFuture<Void> enumerateAssociations(Consumer<SocialUser> handler, ITransaction transaction);
    CompletableFuture<Void> enumerateAssociations(Function<SocialUser, CompletableFuture<Void>> handler);
    CompletableFuture<Void> enumerateAssociations(Function<SocialUser, CompletableFuture<Void>> handler, ITransaction transaction);
}