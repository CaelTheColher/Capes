package me.cael.capes.render

import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import me.cael.capes.mixin.client.AccessorEntityRenderDispatcher
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.PlayerModelType
import net.minecraft.world.entity.player.PlayerSkin
import kotlin.math.sqrt

object PlaceholderEntity {
    val gameProfile = Minecraft.getInstance().gameProfile

    private var skin: PlayerSkin = DefaultPlayerSkin.get(gameProfile)

    var slim = false

    var showBody = true
    var showElytra = false
    var capeLoaded = false
    var limbDistance = 0f
    var lastLimbDistance = 0f
    var limbAngle = 0f
    var yaw = 0f
    var prevYaw = 0f
    var x = 0.0
    var prevX = 0.0
    var renderer: PlaceholderEntityRenderer

    init {
        val ctx = EntityRendererProvider.Context(
            Minecraft.getInstance().entityRenderDispatcher,
            (Minecraft.getInstance().entityRenderDispatcher as AccessorEntityRenderDispatcher).blockModelResolver,
            Minecraft.getInstance().itemModelResolver,
            Minecraft.getInstance().mapRenderer,
            Minecraft.getInstance().resourceManager,
            Minecraft.getInstance().entityModels,
            (Minecraft.getInstance().entityRenderDispatcher as AccessorEntityRenderDispatcher).equipmentAssets,
            Minecraft.getInstance().atlasManager,
            Minecraft.getInstance().font,
            Minecraft.getInstance().playerSkinRenderCache()
        )
        renderer = PlaceholderEntityRenderer(ctx, slim)
        Minecraft .getInstance().skinManager.get(gameProfile).thenAccept {
            skin = it.get()
            slim = skin.model == PlayerModelType.SLIM
            renderer = PlaceholderEntityRenderer(ctx, slim)
        }
    }

    fun updateLimbs() {
        this.lastLimbDistance = this.limbDistance
        val d = this.x - this.prevX
        var g = sqrt(d * d).toFloat() * 4.0f
        if (g > 1.0f) {
            g = 1.0f
        }
        this.limbDistance += (g - this.limbDistance) * 0.4f
        this.limbAngle += this.limbDistance
    }

    fun getCapeTexture(): ClientAsset.Texture? {
        if (!capeLoaded) {
            capeLoaded = true
            PlayerHandler.onLoadTexture(gameProfile)
        }
        val handler = PlayerHandler.fromProfile(gameProfile)
        return if (handler.hasCape) handler.getCape() else skin.cape
    }

    fun getElytraTexture(): ClientAsset.Texture? {
        val handler = PlayerHandler.fromProfile(gameProfile)
        val capeTexture = getCapeTexture()
        return if (handler.hasElytraTexture && Capes.CONFIG.enableElytraTexture && capeTexture != null) capeTexture
        else ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("entity/equipment/wings/elytra"))
    }

    fun getSkinTextures() : PlayerSkin {
        return PlayerSkin(skin.body, getCapeTexture(), getElytraTexture(), skin.model, skin.secure)
    }
}