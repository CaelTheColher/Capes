package me.cael.capes.forge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
