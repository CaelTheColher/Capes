package me.cael.capes.mixins;

import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import me.cael.capes.CapeConfig;
import me.cael.capes.CapeType;
import me.cael.capes.Capes;
import me.cael.capes.ListEntryAccessor;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry implements ListEntryAccessor {
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Supplier<SkinTextures> texturesSupplier;

    @Unique private PlayerHandler handler;

    @Unique private int lastFrame = 0;
    @Unique private int maxFrames = 0;
    @Unique private long lastFrameTime = 0;

    @Unique private Supplier<SkinTextures> moddedTextureSupplier;

    @Override
    public void capesRefresh(boolean tryDownload) {
        CapeType type = handler.getCapeType();

        if (!handler.getHasCape() || type == null) {
            moddedTextureSupplier = null;

            if (tryDownload) {
                PlayerHandler.Companion.downloadTextures(profile);
            }

            return;
        }

        if (!type.isEnabled(profile)) {
            moddedTextureSupplier = null;
            return;
        }

        if (!handler.getHasAnimatedCape()) {
            moddedTextureSupplier = Suppliers.memoize(() -> {
                return makeTextures(handler, handler.getCape());
            });
        } else {
            maxFrames = handler.getMaxFrames();
            SkinTextures[] animationParts = new SkinTextures[maxFrames];

            for (int i = 0; i < maxFrames; i++) {
                animationParts[i] = makeTextures(handler, handler.getCape(i));
            }

            moddedTextureSupplier = () -> {
                var time = System.currentTimeMillis();
                if (time > this.lastFrameTime + 100L) {
                    var thisFrame = (this.lastFrame + 1) % this.maxFrames;
                    this.lastFrame = thisFrame;
                    this.lastFrameTime = time;
                    return animationParts[thisFrame];
                } else {
                    return animationParts[lastFrame];
                }
            };
        }
    }

    @Unique
    private SkinTextures makeTextures(PlayerHandler handler, Identifier capeTexture) {
        CapeConfig config = Capes.INSTANCE.getCONFIG();
        SkinTextures oldTextures = texturesSupplier.get();

        Identifier elytraTexture = handler.getHasElytraTexture() && config.getEnableElytraTexture() ? capeTexture : new Identifier("textures/entity/elytra.png");
        return new SkinTextures(
                oldTextures.texture(), oldTextures.textureUrl(),
                capeTexture, elytraTexture,
                oldTextures.model(), oldTextures.secure());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setupModifiedSupplier(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
        handler = PlayerHandler.Companion.fromProfile(profile);
        capesRefresh(true);
    }

    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        if (moddedTextureSupplier != null) {
            cir.setReturnValue(moddedTextureSupplier.get());
        }
    }
}
