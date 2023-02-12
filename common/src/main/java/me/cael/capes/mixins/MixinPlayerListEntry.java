package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;
    @Shadow private boolean texturesLoaded;

    @Inject(method = "loadTextures", at = @At("HEAD"))
    private void loadTextures(CallbackInfo ci) {
        if (!texturesLoaded) {
            PlayerHandler.Companion.onLoadTexture(profile);
        }
    }

    @Inject(method = "getCapeTexture", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(profile);
        if (handler.getHasCape()) {
            cir.setReturnValue(handler.getCape());
        }
    }


}
