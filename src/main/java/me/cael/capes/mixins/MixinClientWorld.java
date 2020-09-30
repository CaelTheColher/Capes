package me.cael.capes.mixins;

import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void addPlayer(int id, AbstractClientPlayerEntity player, CallbackInfo info) {
        PlayerHandler.Companion.onPlayerJoin(player);
    }
}
