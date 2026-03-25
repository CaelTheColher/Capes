package me.cael.capes.handler

import com.google.gson.Gson
import com.mojang.authlib.GameProfile
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import me.cael.capes.CapeType
import me.cael.capes.Capes
import me.cael.capes.Capes.identifier
import me.cael.capes.handler.data.CosmeticaData
import me.cael.capes.handler.data.MCMData
import net.minecraft.client.Minecraft
import com.mojang.blaze3d.platform.NativeImage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.core.ClientAsset
import net.minecraft.core.UUIDUtil
import org.apache.commons.codec.binary.Base64
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import java.util.concurrent.Executors

class PlayerHandler(var profile: GameProfile) {
    val uuid: UUID = profile.id
    var lastFrame = 0
    var maxFrames = 0
    var lastFrameTime = 0L
    var hasCape: Boolean = false
    var hasElytraTexture: Boolean = true
    var hasAnimatedCape: Boolean = false
    var capeType: CapeType? = null
    var hasLoadedTextures = false
    init {
        instances[uuid] = this
    }

    companion object {
        val instances = HashMap<UUID, PlayerHandler>()
        val capeExecutor = Executors.newCachedThreadPool()

        fun fromProfile(profile: GameProfile) = instances[profile.id] ?: PlayerHandler(profile)

        fun onLoadTexture(profile: GameProfile) {
            val playerHandler = fromProfile(profile)
            if (profile == Minecraft.getInstance().gameProfile) {
                playerHandler.hasCape = false
                playerHandler.hasAnimatedCape = false
                val config = Capes.CONFIG
                capeExecutor.submit {
                    playerHandler.setCape(config.clientCapeType)
                }
            } else if (!playerHandler.hasLoadedTextures) {
                capeExecutor.submit {
                    for (capeType in CapeType.values()) {
                        if (playerHandler.setCape(capeType)) break
                    }
                }
                playerHandler.hasLoadedTextures = true
            }
        }

        fun connection(url: String): HttpURLConnection {
            val connection = URI(url).toURL().openConnection(Minecraft.getInstance().proxy) as HttpURLConnection
            connection.addRequestProperty("User-Agent", "Mozilla/5.0")
            connection.doInput = true
            connection.doOutput = false
            return connection
        }
    }

    fun getCape(): ClientAsset.Texture {
        if (!hasAnimatedCape) return ClientAsset.ResourceTexture(identifier(uuid.toString()), identifier(uuid.toString()))
        val time = System.currentTimeMillis()
        return if (time > this.lastFrameTime + 100L) {
            val thisFrame = (this.lastFrame + 1) % this.maxFrames
            this.lastFrame = thisFrame
            this.lastFrameTime = time
            ClientAsset.ResourceTexture(identifier("$uuid/$thisFrame"), identifier("$uuid/$thisFrame"))
        } else {
            ClientAsset.ResourceTexture(identifier("$uuid/${this.lastFrame}"), identifier("$uuid/${this.lastFrame}"))
        }
    }

    fun setCape(capeType: CapeType): Boolean {
        val capeURL = capeType.getURL(profile) ?: return false
        val connection = connection(capeURL)

        return when(capeType) {
            CapeType.LABYMOD -> setStandardCape(connection, true)
            CapeType.COSMETICA -> setCosmeticaCape(connection)
            CapeType.MINECRAFTCAPES -> setMCMCape(connection)
            else -> setStandardCape(connection)
        }.also { if (it) this.capeType = capeType}
    }

    fun setStandardCape(connection: HttpURLConnection, labymod: Boolean = false): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            return setCapeTexture(connection.inputStream, labymod = labymod)
        }
        return false
    }

    fun setCosmeticaCape(connection: HttpURLConnection): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            val reader: Reader = InputStreamReader(connection.inputStream, "UTF-8")
            val result = Gson().fromJson(reader, CosmeticaData::class.java)
            return result.cape?.origin == "Cosmetica"
                    && setCapeTextureFromBase64(result.cape.image.substring(22), result.cape.isAnimated())
        }
        return false
    }

    fun setMCMCape(connection: HttpURLConnection): Boolean {
        connection.connect()
        if (connection.responseCode / 100 == 2) {
            val reader: Reader = InputStreamReader(connection.inputStream, "UTF-8")
            val profile = Gson().fromJson(reader, MCMData::class.java)

            val result = connection(profile.cape_url)
            result.connect();
            if(result.responseCode / 100 == 2) {
                return setCapeTexture(result.inputStream, profile.animated_cape_url != null, false)
            }
        }
        return false
    }

    fun setCapeTextureFromBase64(base64Texture: String?, animated: Boolean = false): Boolean {
        if(base64Texture == null) return false
        val bytes = Base64.decodeBase64(base64Texture)
        return setCapeTexture(ByteArrayInputStream(bytes), animated)
    }

    fun setCapeTexture(image: InputStream, animated: Boolean = false, labymod: Boolean = false): Boolean {
        return try {
            val cape = NativeImage.read(image)
            if (labymod && UUIDUtil.uuidFromIntArray(cape.pixels).toString() == "ff305f81-ff30-5f90-ff30-5f90ff305f90") {
                return false
            }
            Minecraft.getInstance().submit {
                if (animated) {
                    val animatedCapeFrames = parseAnimatedCape(cape)
                    animatedCapeFrames.forEach { (frame, texture) ->
                        Minecraft.getInstance().textureManager.register(
                            identifier("$uuid/$frame"), DynamicTexture({"$uuid/$frame"}, texture)
                        )
                    }
                    this.maxFrames = animatedCapeFrames.size
                    this.hasCape = true
                    this.hasAnimatedCape = true
                } else {
                    this.hasElytraTexture = cape.width.floorDiv(cape.height) == 2
                    Minecraft.getInstance().textureManager.register(
                        identifier(uuid.toString()), DynamicTexture({uuid.toString()}, parseCape(cape))
                    )
                    this.hasCape = true
                }
            }
            true
        } catch (exception: Exception) {
            exception.printStackTrace()
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
                imgNew.setPixel(x, y, img.getPixel(x, y))
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
                    frame.setPixel(x, y, img.getPixel(x, y + (currentFrame * (img.width / 2))))
                }
            }
            animatedCape[currentFrame] = frame
        }
        return animatedCape
    }

}