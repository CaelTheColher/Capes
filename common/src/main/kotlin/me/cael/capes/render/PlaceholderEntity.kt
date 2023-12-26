package me.cael.capes.render

import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.client.util.SkinTextures
import net.minecraft.util.Identifier
import kotlin.math.sqrt

object PlaceholderEntity {
    val gameProfile = MinecraftClient.getInstance().gameProfile

    var skin: SkinTextures = DefaultSkinHelper.getTexture(gameProfile)

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

    init {
        MinecraftClient.getInstance().skinProvider.fetchSkinTextures(gameProfile).thenAccept {
            skin = it
            slim = skin.model == SkinTextures.Model.SLIM
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

    fun getCapeTexture(): Identifier? {
        if (!capeLoaded) {
            capeLoaded = true
            PlayerHandler.downloadTextures(gameProfile)
        }
        val handler = PlayerHandler.fromProfile(gameProfile)
        return if (handler.hasCape) handler.getCape() else skin.capeTexture
    }

    fun getElytraTexture(): Identifier {
        val handler = PlayerHandler.fromProfile(gameProfile)
        val capeTexture = getCapeTexture()
        return if (handler.hasElytraTexture && Capes.CONFIG.enableElytraTexture && capeTexture != null) capeTexture else Identifier("textures/entity/elytra.png")
    }

    fun getSkinTexture(): Identifier = skin.texture
}
