package me.cael.capes.handler

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.cael.capes.CapeConfig
import me.cael.capes.CapeType
import me.cael.capes.handler.data.MCMData
import me.cael.capes.handler.data.WynntilsData
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.apache.commons.codec.binary.Base64
import java.io.*
import java.util.concurrent.ForkJoinPool

class PlayerHandler(var player: PlayerEntity) {
    val uuid: UUID = player.uuid
    var capeTexture: Identifier? = null
    var glint: Boolean = false
    init {
        instances[uuid] = this
    }

    companion object {
        val instances = HashMap<UUID, PlayerHandler>()

        fun fromPlayer(player: PlayerEntity) = instances[player.uuid] ?: PlayerHandler(player)

        fun onPlayerJoin(player: PlayerEntity) {
            val playerHandler = fromPlayer(player)
            if (player == MinecraftClient.getInstance().player) {
                val config = AutoConfig.getConfigHolder(CapeConfig::class.java).config
                ForkJoinPool.commonPool().submit {
                    playerHandler.setCape(config.clientCapeType, config.glint)
                }
            } else {
                if (player.uuidAsString == "5f91fdfd-ea97-473c-bb77-c8a2a0ed3af9") { playerHandler.setStandardCape(connection("https://athena.wynntils.com/capes/user/${player.uuidAsString}"), true); return }
                ForkJoinPool.commonPool().submit {
                    for (capeType in CapeType.values()) {
                        if (playerHandler.setCape(capeType)) break
                    }
                }
            }
        }

        fun connection(url: String): HttpURLConnection {
            val connection = URL(url).openConnection(MinecraftClient.getInstance().networkProxy) as HttpURLConnection
            connection.addRequestProperty("User-Agent", "Mozilla/4.0")
            connection.doInput = true
            connection.doOutput = false
            return connection
        }
    }

    fun setCape(capeType: CapeType, glint: Boolean = false): Boolean {
        val capeURL = capeType.getURL(player) ?: return false
        val connection = connection(capeURL)
        return when(capeType) {
            CapeType.WYNNTILS -> setWynntilsCape(connection)
            CapeType.MINECRAFTCAPES -> setMCMCape(connection)
            else -> setStandardCape(connection, glint)
        }
    }

    fun setStandardCape(connection: HttpURLConnection, glint: Boolean = false): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            this.glint = glint
            return setCapeTexture(connection.inputStream)
        }
        this.capeTexture = null
        return false
    }

    fun setWynntilsCape(connection: HttpURLConnection): Boolean {
        val body = JsonObject()
        body.addProperty("uuid", uuid.toString())
        val data = body.toString().toByteArray()
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("Content-Length", data.size.toString())
        val o = connection.outputStream
        o.write(data)
        o.flush()
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            val reader: Reader = InputStreamReader(connection.inputStream, "UTF-8")
            val cosmetics = JsonParser().parse(reader).asJsonObject["user"].asJsonObject["cosmetics"]
            val result = Gson().fromJson(cosmetics, WynntilsData::class.java)
            return this.setCapeTextureFromBase64(result.texture)
        }
        this.capeTexture = null
        return false
    }

    fun setMCMCape(connection: HttpURLConnection): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            val reader: Reader = InputStreamReader(connection.inputStream, "UTF-8")
            val result = Gson().fromJson(reader, MCMData::class.java)
            this.glint = result.capeGlint
            return setCapeTextureFromBase64(result.textures["cape"])
        }
        this.capeTexture = null
        return false
    }

    fun setCapeTextureFromBase64(base64Texture: String?): Boolean {
        if(base64Texture == null) return false
        val bytes = Base64.decodeBase64(base64Texture)
        return setCapeTexture(ByteArrayInputStream(bytes))
    }

    fun setCapeTexture(image: InputStream): Boolean {
        return try {
            val cape = NativeImage.read(image)
            MinecraftClient.getInstance().submit {
                this.capeTexture = MinecraftClient.getInstance().textureManager.registerDynamicTexture(uuid.toString().replace("-", ""), NativeImageBackedTexture(parseCape(cape)))
            }
            true
        } catch (ioException: IOException) {
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