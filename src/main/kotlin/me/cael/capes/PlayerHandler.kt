package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class PlayerHandler(player: PlayerEntity) {
    val uuid: UUID = player.uuid
    var capeTexture: Identifier? = null
    var glint: Boolean = false
    init {
        instances[uuid] = this
    }

    companion object {
        val instances = HashMap<UUID, PlayerHandler>()

        fun fromPlayer(player: PlayerEntity): PlayerHandler {
            return instances[player.uuid] ?: PlayerHandler(player)
        }

        fun onPlayerJoin(player: PlayerEntity) {
            val playerHandler = fromPlayer(player)
            if (player.uuidAsString == "5f91fdfd-ea97-473c-bb77-c8a2a0ed3af9") { playerHandler.setCapeFromURL("https://athena.wynntils.com/capes/user/${player.uuidAsString}", true); return }
            if (player == MinecraftClient.getInstance().player) {
                val config = AutoConfig.getConfigHolder(CapeConfig::class.java).config
                val capeURL = config.clientCapeType.getURL(player) ?: return
                thread(start = true) {
                    playerHandler.setCapeFromURL(capeURL, config.glint)
                }
            } else {
                thread(start=true) {
                    for (capeType in CapeType.values()) {
                        if (playerHandler.setCapeFromURL(capeType.getURL(player))) break
                    }
                }
            }
        }
    }

    fun setCapeFromURL(capeURL: String?, glint: Boolean = false): Boolean {
        if (capeURL == null) return false
        val connection =
            URL(capeURL).openConnection(MinecraftClient.getInstance().networkProxy) as HttpURLConnection
        connection.addRequestProperty("User-Agent", "Mozilla/4.0")
        connection.doInput = true
        connection.doOutput = false
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            this.glint = glint
            return setCape(connection.inputStream)
        }
        capeTexture = null
        return false
    }

    fun setCape(image: InputStream): Boolean {
        return try {
            val cape = NativeImage.read(image)
            capeTexture = MinecraftClient.getInstance().textureManager.registerDynamicTexture(uuid.toString().replace("-", ""), NativeImageBackedTexture(parseCape(cape)))
            true
        } catch(ioException: IOException) {
            false
        }
    }

    private fun parseCape(img: NativeImage): NativeImage {
        var imageWidth = 64
        var imageHeight = 32
        val srcWidth = img.width
        val srcHeight= img.height
        while (imageWidth < srcWidth || imageHeight < srcHeight) {
            imageWidth *= 2
            imageHeight *= 2
        }
        val imgNew = NativeImage(imageWidth, imageHeight, true)
        for (x in 0 until srcWidth) {
            for (y in 0 until srcHeight) {
                imgNew.setPixelColor(x, y, img.getPixelColor(x, y))
            }
        }
        img.close()
        return imgNew
    }
}