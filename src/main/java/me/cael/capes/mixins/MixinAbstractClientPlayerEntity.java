package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import me.cael.capes.PlayerHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {

    @Shadow
    public abstract Identifier getCapeTexture();

    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ConstructorAbstractClientPlayerEntity(ClientWorld world, GameProfile profile, CallbackInfo info) {
//        PlayerHandler.Companion.onPlayerJoin(this, getCapeTexture());
        PlayerHandler.Companion.onPlayerJoin(this, null);
    }
}