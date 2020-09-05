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
import kotlin.concurrent.thread

class PlayerHandler(player: PlayerEntity, var capeTexture: Identifier? = null) {
    val uuid: UUID = player.uuid
    val glint: Boolean = true

    init {
        instances[uuid] = this
    }

    companion object {
        val instances = HashMap<UUID, PlayerHandler>()
        var capeType: CapeType = CapeType.DEBUG

        fun fromPlayer(player: PlayerEntity, capeTexture: Identifier? = null): PlayerHandler {
            return instances[player.uuid] ?: PlayerHandler(player, capeTexture)
        }

        fun onPlayerJoin(player: PlayerEntity, capeTexture: Identifier? = null) {
            val playerManager = fromPlayer(player, capeTexture)
            lateinit var capeURL: URL
            capeURL = URL(when(capeType) {
                CapeType.OPTIFINE -> "http://s.optifine.net/capes/${player.name}.png"
//                CapeType.OPTIFINE -> "http://s.optifine.net/capes/AlexSa1000.png"
                CapeType.MINECRAFTCAPES -> "https://minecraftcapes.net/profile/${player.uuidAsString.replace("-","")}/cape"
//                CapeType.MINECRAFTCAPES -> "https://minecraftcapes.net/profile/b3d7b646ec3c44a2b933efc1461711bb/cape"
                CapeType.DEBUG -> "https://cdn.discordapp.com/attachments/414040382960304139/751400169752494131/glass.png"
                CapeType.MINECRAFT -> return
            })
            println(capeType.name)
            thread(start = true) {
                val connection = capeURL.openConnection(MinecraftClient.getInstance().networkProxy) as HttpURLConnection
                connection.addRequestProperty("User-Agent", "Mozilla/4.0")
                connection.doInput = true
                connection.doOutput = false
                connection.connect()
                if (connection.responseCode / 100 == 2) {
                    playerManager.setCape(connection.inputStream)
                }
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