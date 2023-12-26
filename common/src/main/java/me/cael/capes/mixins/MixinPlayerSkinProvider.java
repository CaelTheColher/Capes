package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import me.cael.capes.ListEntryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinProvider$1")
public class MixinPlayerSkinProvider {
    @Inject(method = "load(Ljava/lang/Object;)Ljava/lang/Object;", at = @At("TAIL"))
    private void getTextureFuture(Object key, CallbackInfoReturnable<Object> cir) {
        // One it's done loading, refresh our skin providers
        GameProfile profile = ((KeyAccessor) key).getProfile();
        CompletableFuture<SkinTextures> result = (CompletableFuture<SkinTextures>) cir.getReturnValue();

        result.whenCompleteAsync((textures, error) -> {
            if (textures == null) return;

            MinecraftClient client = MinecraftClient.getInstance();
            client.submit(() -> {
                var network = client.getNetworkHandler();
                if (network == null) return;
                var entry = (ListEntryAccessor) network.getPlayerListEntry(profile.getId());
                if (entry == null) return;
                entry.capesRefresh(false);
            });
        });
    }
}
