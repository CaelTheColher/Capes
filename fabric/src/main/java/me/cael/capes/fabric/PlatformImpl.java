package me.cael.capes.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
