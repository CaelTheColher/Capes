package me.cael.capes.mixins;

import me.cael.capes.menu.SelectorMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity<T extends Entity> {

    @Inject(method = "squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", at = @At(value = "HEAD"), cancellable = true)
    private void squareDistanceTo(T entity, CallbackInfoReturnable<Double> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof SelectorMenu) cir.setReturnValue(0.0);
    }

    @Inject(method = "distanceTo", at = @At(value = "HEAD"), cancellable = true)
    private void distanceTo(T entity, CallbackInfoReturnable<Float> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof SelectorMenu) cir.setReturnValue(0f);
    }

}