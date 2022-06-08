package me.cael.capes.handler

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import me.cael.capes.CapeType
import me.cael.capes.Capes
import me.cael.capes.handler.data.MCMData
import me.cael.capes.handler.data.WynntilsData
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import org.apache.commons.codec.binary.Base64
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ForkJoinPool

class PlayerHandler(var profile: GameProfile) {
    val uuid: UUID = profile.id
    var lastFrame = 0
    var maxFrames = 0
    var lastFrameTime = 0L
    var hasCape: Boolean = false
    var hasElytraTexture: Boolean = true
    var hasAnimatedCape: Boolean = false
    init {
        instances[uuid] = this
    }

    companion object {
        val instances = HashMap<UUID, PlayerHandler>()

        fun fromProfile(profile: GameProfile) = instances[profile.id] ?: PlayerHandler(profile)

        fun onLoadTexture(profile: GameProfile) {
            val playerHandler = fromProfile(profile)
            if (profile == MinecraftClient.getInstance().session.profile) {
                playerHandler.hasCape = false
                playerHandler.hasAnimatedCape = false
                val config = Capes.CONFIG
                ForkJoinPool.commonPool().submit {
                    playerHandler.setCape(config.clientCapeType)
                }
            } else {
                ForkJoinPool.commonPool().submit {
                    if (profile.id.toString() == "5f91fdfd-ea97-473c-bb77-c8a2a0ed3af9") { playerHandler.setStandardCape(connection("https://athena.wynntils.com/capes/user/${profile.id}")); return@submit }
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

    fun getCape(): Identifier {
        if (!hasAnimatedCape) return identifier(uuid.toString())
        val time = System.currentTimeMillis()
        return if (time > this.lastFrameTime + 100L) {
            val thisFrame = (this.lastFrame + 1) % this.maxFrames
            this.lastFrame = thisFrame
            this.lastFrameTime = time
            identifier("$uuid/$thisFrame")
        } else {
            identifier("$uuid/${this.lastFrame}")
        }
    }

    fun setCape(capeType: CapeType): Boolean {
        val capeURL = capeType.getURL(profile) ?: return false
        val connection = connection(capeURL)
        return when(capeType) {
            CapeType.WYNNTILS -> setWynntilsCape(connection)
            CapeType.MINECRAFTCAPES -> setMCMCape(connection)
            else -> setStandardCape(connection)
        }
    }

    fun setStandardCape(connection: HttpURLConnection): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            return setCapeTexture(connection.inputStream)
        }
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
            val cosmetics = JsonParser.parseReader(reader).asJsonObject["user"].asJsonObject["cosmetics"]
            val result = Gson().fromJson(cosmetics, WynntilsData::class.java)
            return this.setCapeTextureFromBase64(result.texture)
        }
        return false
    }

    fun setMCMCape(connection: HttpURLConnection): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            val reader: Reader = InputStreamReader(connection.inputStream, "UTF-8")
            val result = Gson().fromJson(reader, MCMData::class.java)
            return setCapeTextureFromBase64(result.textures["cape"], result.animatedCape)
        }
        return false
    }

    fun setCapeTextureFromBase64(base64Texture: String?, animated: Boolean = false): Boolean {
        if(base64Texture == null) return false
        val bytes = Base64.decodeBase64(base64Texture)
        return setCapeTexture(ByteArrayInputStream(bytes), animated)
    }

    fun setCapeTexture(image: InputStream, animated: Boolean = false): Boolean {
        return try {
            val cape = NativeImage.read(image)
            MinecraftClient.getInstance().submit {
                if (animated) {
                    val animatedCapeFrames = parseAnimatedCape(cape)
                    animatedCapeFrames.forEach { (frame, texture) ->
                        MinecraftClient.getInstance().textureManager.registerTexture(
                            identifier("$uuid/$frame"), NativeImageBackedTexture(texture)
                        )
                    }
                    this.maxFrames = animatedCapeFrames.size
                    this.hasCape = true
                    this.hasAnimatedCape = true
                } else {
                    this.hasElytraTexture = cape.width.floorDiv(cape.height) == 2
                    MinecraftClient.getInstance().textureManager.registerTexture(
                        identifier(uuid.toString()), NativeImageBackedTexture(parseCape(cape))
                    )
                    this.hasCape = true
                }
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
                imgNew.setColor(x, y, img.getColor(x, y))
            }
        }
        img.close()
        return imgNew
    }

    private fun parseAnimatedCape(img: NativeImage): Int2ObjectOpenHashMap<NativeImage> {
        val animatedCape = Int2ObjectOpenHashMap<NativeImage>()
        val totalFrames = img.height / (img.width / 2)
        for (currentFrame in 0 until totalFrames) {
            val frame = NativeImage(img.width, img.width / 2, true)
            for (x in 0 until frame.width) {
                for (y in 0 until frame.height) {
                    frame.setColor(x, y, img.getColor(x, y + (currentFrame * (img.width / 2))))
                }
            }
            animatedCape[currentFrame] = frame
        }
        return animatedCape
    }

    fun identifier(id: String) = Identifier("capes", id)

}