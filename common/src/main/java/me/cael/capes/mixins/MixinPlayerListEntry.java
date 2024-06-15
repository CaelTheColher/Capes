package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import me.cael.capes.CapeConfig;
import me.cael.capes.Capes;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;

    @Inject(method = "texturesSupplier", at = @At("HEAD"))
    private static void loadTextures(GameProfile profile, CallbackInfoReturnable<Supplier<SkinTextures>> cir) {
        PlayerHandler.Companion.onLoadTexture(profile);
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(profile);
        if (handler.getHasCape()) {
            CapeConfig config = Capes.INSTANCE.getCONFIG();
            SkinTextures oldTextures = cir.getReturnValue();
            Identifier capeTexture = handler.getCape();
            Identifier elytraTexture = handler.getHasElytraTexture() && config.getEnableElytraTexture() ? capeTexture : Identifier.of("textures/entity/elytra.png");
            SkinTextures newTextures = new SkinTextures(
                    oldTextures.texture(), oldTextures.textureUrl(),
                    capeTexture, elytraTexture,
                    oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(newTextures);
        }
    }



}
