package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinProvider$Key")
public interface KeyAccessor {
    @Accessor
    GameProfile getProfile();
}
