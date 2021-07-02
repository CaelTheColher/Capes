package me.cael.capes.mixins;

import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraFeatureRenderer.class)
public class MixinElytraFeatureRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;canRenderCapeTexture()Z"))
    private boolean fixLabyModBeingWeird(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(abstractClientPlayerEntity.getGameProfile());
        return handler.getHasElytraTexture();
    }

}
