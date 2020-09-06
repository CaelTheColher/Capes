package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class PlayerHandler(player: PlayerEntity) {
    val uuid: UUID = player.uuid
    var capeTexture: Identifier? = null

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
            if (player.uuidAsString == "5f91fdfd-ea97-473c-bb77-c8a2a0ed3af9") { playerHandler.setCapeFromURL("https://i.imgur.com/DlyUFYB.png"); return}
            if (player == MinecraftClient.getInstance().player) {
                playerHandler.capeTexture = (player as AbstractClientPlayerEntity).capeTexture
                val config = AutoConfig.getConfigHolder(CapeConfig::class.java).config
                val capeURL = when (config.clientCapeType) {
                    CapeType.OPTIFINE -> "http://s.optifine.net/capes/${player.entityName}.png"
                    CapeType.MINECRAFTCAPES -> "https://minecraftcapes.net/profile/${player.uuidAsString.replace("-", "")}/cape"
                    CapeType.MINECRAFT -> return
                }
                thread(start = true) {
                    playerHandler.setCapeFromURL(capeURL)
                }
            } else {
                val mcCape = (player as AbstractClientPlayerEntity).capeTexture
                if (mcCape == null) {
                    val capeURLs = listOf(
                        "http://s.optifine.net/capes/${player.entityName}.png",
                        "https://minecraftcapes.net/profile/${player.uuidAsString.replace("-", "")}/cape"
                    )
                    thread(start = true) {
                        for (capeURL in capeURLs) {
                            if (playerHandler.setCapeFromURL(capeURL)) break
                        }
                    }
                } else {
                    playerHandler.capeTexture = mcCape
                }
            }
        }
    }

    fun setCapeFromURL(capeURL: String): Boolean {
        val connection =
            URL(capeURL).openConnection(MinecraftClient.getInstance().networkProxy) as HttpURLConnection
        connection.addRequestProperty("User-Agent", "Mozilla/4.0")
        connection.doInput = true
        connection.doOutput = false
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            setCape(connection.inputStream)
            return true
        }
        capeTexture = null
        return false
    }

    fun setCape(image: InputStream) {
        val cape = NativeImage.read(image)
        capeTexture = MinecraftClient.getInstance().textureManager.registerDynamicTexture(uuid.toString().replace("-",""), NativeImageBackedTexture(parseCape(cape)))
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