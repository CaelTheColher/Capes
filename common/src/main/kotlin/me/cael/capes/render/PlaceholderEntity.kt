package me.cael.capes.render

import com.google.common.collect.Maps
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.util.Identifier
import java.util.*
import kotlin.math.sqrt

object PlaceholderEntity {
    private val textures: EnumMap<MinecraftProfileTexture.Type, Identifier> = Maps.newEnumMap(MinecraftProfileTexture.Type::class.java)

    val gameProfile = MinecraftClient.getInstance().session.profile

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
        MinecraftClient.getInstance().skinProvider.loadSkin(gameProfile,
            { type: MinecraftProfileTexture.Type, identifier: Identifier, texture: MinecraftProfileTexture ->
                this.textures[type] = identifier
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    slim = texture.getMetadata("model") == "slim"
                }
            },
            true
        )
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
            PlayerHandler.onLoadTexture(gameProfile)
        }
        val handler = PlayerHandler.fromProfile(gameProfile)
        return if (handler.hasCape) handler.getCape() else textures[MinecraftProfileTexture.Type.CAPE]
    }

    fun getElytraTexture(): Identifier {
        val handler = PlayerHandler.fromProfile(gameProfile)
        val capeTexture = getCapeTexture()
        return if (handler.hasElytraTexture && Capes.CONFIG.enableElytraTexture && capeTexture != null) capeTexture else Identifier("textures/entity/elytra.png")
    }

    fun getSkinTexture(): Identifier = textures.getOrDefault(MinecraftProfileTexture.Type.SKIN, DefaultSkinHelper.getTexture())
}