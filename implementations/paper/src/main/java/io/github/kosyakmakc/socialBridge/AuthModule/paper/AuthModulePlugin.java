package io.github.kosyakmakc.socialBridge.AuthModule.paper;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IModuleLoader;
import io.github.kosyakmakc.socialBridge.Utils.Version;
import io.github.kosyakmakc.socialBridge.SocialBridge;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthModulePlugin extends JavaPlugin implements IModuleLoader {
    private AuthModule module;

    @Override
    public void onEnable() {
        module = new AuthModule(this, new Version(getPluginMeta().getVersion()));
        SocialBridge.INSTANCE.connectModule(module).join();
    }

    @Override
    public void onDisable() {
        SocialBridge.INSTANCE.disconnectModule(module).join();
    }
 }
