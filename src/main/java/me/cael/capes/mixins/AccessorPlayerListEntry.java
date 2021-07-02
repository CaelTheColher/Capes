package me.cael.capes.mixins;

import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListEntry.class)
public interface AccessorPlayerListEntry {
    @Accessor void setTexturesLoaded(boolean texturesLoaded);
}
