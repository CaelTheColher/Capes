package me.cael.capes.mixins;

import me.cael.capes.utils.FakePlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

    @Inject(method = "setModelPose", at = @At(value = "HEAD"), cancellable = true)
    private void hasLabel(AbstractClientPlayerEntity player, CallbackInfo ci) {
        if (player instanceof FakePlayer) {
            ci.cancel();
        }
    }
}
