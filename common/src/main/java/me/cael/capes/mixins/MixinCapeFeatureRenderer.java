package me.cael.capes.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public class MixinCapeFeatureRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer fixCapeTransparency(Identifier texture) {
        return RenderLayer.getArmorCutoutNoCull(texture);
    }

    // Fixes https://bugs.mojang.com/browse/MC-127749
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private float fixCapeInterpolation(float bodyRotation, @Local(argsOnly = true) AbstractClientPlayerEntity playerEntity, @Local(ordinal = 2, argsOnly = true) float partialTicks) {
        return playerEntity.prevBodyYaw + (playerEntity.bodyYaw - playerEntity.prevBodyYaw) * partialTicks;
    }
}
