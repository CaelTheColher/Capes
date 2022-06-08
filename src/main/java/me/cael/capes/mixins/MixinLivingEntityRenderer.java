package me.cael.capes.mixins;

import me.cael.capes.menu.SelectorMenu;
import me.cael.capes.utils.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity> {

    @Inject(method = "hasLabel*", at = @At(value = "HEAD"), cancellable = true)
    private void hasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof FakePlayer || MinecraftClient.getInstance().currentScreen instanceof SelectorMenu) cir.setReturnValue(false);
    }
}