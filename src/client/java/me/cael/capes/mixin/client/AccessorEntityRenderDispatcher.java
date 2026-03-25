package me.cael.capes.mixin.client;

import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRenderDispatcher {
    @Accessor
    EquipmentAssetManager getEquipmentAssets();

    @Accessor
    BlockModelResolver getBlockModelResolver();
}
