package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<Type, Identifier> textures;
    @Shadow private boolean texturesLoaded;

    @Inject(method = "loadTextures", at = @At("HEAD"))
    private void loadTextures(CallbackInfo ci) {
        if (!texturesLoaded) {
            PlayerHandler.Companion.onLoadTexture(profile);
        }
        Identifier cape = PlayerHandler.Companion.fromProfile(profile).getCapeTexture();
        this.textures.put(Type.CAPE, cape);
    }

}
