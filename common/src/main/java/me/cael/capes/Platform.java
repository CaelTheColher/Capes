package me.cael.capes;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class Platform {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
}
