package me.cael.capes

import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class PlayerHandler(player: PlayerEntity) {
    val uuid: UUID = player.uuid
    val glint: Boolean = true
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
            val capeURL = URL("http://s.optifine.net/capes/${player.name}.png")
//            val capeURL = URL("https://cdn.discordapp.com/attachments/414040382960304139/751400169752494131/glass.png")
            val connection = capeURL.openConnection(MinecraftClient.getInstance().networkProxy) as HttpURLConnection
            connection.addRequestProperty("User-Agent", "Mozilla/4.0")
            connection.doInput = true
            connection.doOutput = false
            connection.connect()
            if (connection.responseCode / 100 == 2) {
                fromPlayer(player).setCape(connection.inputStream)
            }
        }
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
        imgNew.copyFrom(img)
        img.close()
        return imgNew
    }
}