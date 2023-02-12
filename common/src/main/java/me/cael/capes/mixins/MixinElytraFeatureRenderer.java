package me.cael.capes.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.cael.capes.CapeConfig;
import me.cael.capes.Capes;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraFeatureRenderer.class)
public class MixinElytraFeatureRenderer {

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;canRenderCapeTexture()Z"))
    private boolean toggleCustomElytra(boolean canRenderCapeTexture, @Local AbstractClientPlayerEntity abstractClientPlayerEntity) {
        CapeConfig config = Capes.INSTANCE.getCONFIG();
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(abstractClientPlayerEntity.getGameProfile());
        return canRenderCapeTexture && handler.getHasElytraTexture() && config.getEnableElytraTexture();
    }

}
