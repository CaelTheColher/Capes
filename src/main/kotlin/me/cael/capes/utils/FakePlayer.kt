package me.cael.capes.utils

import com.google.common.collect.Maps
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.render.entity.PlayerModelPart
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkSide
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionTypes
import java.util.*

object FakePlayer : ClientPlayerEntity(MinecraftClient.getInstance(),
    FakeWorld, FakeNetworkHandler, null, null, false, true) {

    private val textures: EnumMap<MinecraftProfileTexture.Type, Identifier> = Maps.newEnumMap(MinecraftProfileTexture.Type::class.java)
    private var model: String? = null
    var capeLoaded = false
    var showElytra = false

    init {
        MinecraftClient.getInstance().skinProvider.loadSkin(gameProfile,
            { type: MinecraftProfileTexture.Type, identifier: Identifier, texture: MinecraftProfileTexture ->
                this.textures[type] = identifier
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    model = texture.getMetadata("model")
                    if (model == null) {
                        model = "default"
                    }
                }
            },
            true
        )
    }

    override fun getCapeTexture(): Identifier? {
        if (!capeLoaded) {
            capeLoaded = true
            PlayerHandler.onLoadTexture(gameProfile)
        }
        updateCapeAngles()
        val handler = PlayerHandler.fromProfile(gameProfile)
        return if (handler.hasCape) handler.getCape() else textures[MinecraftProfileTexture.Type.CAPE]
    }

    override fun canRenderCapeTexture(): Boolean = true

    fun updateCapeAngles() {
        prevCapeX = capeX
        prevCapeY = capeY
        prevCapeZ = capeZ
        val d = this.x - capeX
        val e = this.y - capeY
        val f = this.z - capeZ
        if (d > 10.0) {
            capeX = this.x
            prevCapeX = capeX
        }
        if (f > 10.0) {
            capeZ = this.z
            prevCapeZ = capeZ
        }
        if (e > 10.0) {
            capeY = this.y
            prevCapeY = capeY
        }
        if (d < -10.0) {
            capeX = this.x
            prevCapeX = capeX
        }
        if (f < -10.0) {
            capeZ = this.z
            prevCapeZ = capeZ
        }
        if (e < -10.0) {
            capeY = this.y
            prevCapeY = capeY
        }
        capeX += d * 0.25
        capeZ += f * 0.25
        capeY += e * 0.25
    }

    override fun getSkinTexture(): Identifier = textures.getOrDefault(MinecraftProfileTexture.Type.SKIN, DefaultSkinHelper.getTexture(this.getUuid()))
    override fun hasSkinTexture(): Boolean = true
    override fun getModel(): String? = model
    override fun isSpectator(): Boolean = false
    override fun getPlayerListEntry(): PlayerListEntry? = null
    override fun isPartVisible(modelPart: PlayerModelPart): Boolean = when (modelPart) {
        PlayerModelPart.CAPE -> true
        else -> MinecraftClient.getInstance().options.isPlayerModelPartEnabled(modelPart)
    }
    override fun getEquippedStack(slot: EquipmentSlot?): ItemStack? {
        return if (slot == EquipmentSlot.CHEST && showElytra) Items.ELYTRA.defaultStack
        else ItemStack.EMPTY
    }

    object FakeNetworkHandler : ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, ClientConnection(NetworkSide.CLIENTBOUND), MinecraftClient.getInstance().session.profile, MinecraftClient.getInstance().createTelemetrySender())
    object FakeWorld : ClientWorld(FakeNetworkHandler, Properties(Difficulty.EASY, false, true), RegistryKey.of(Registry.WORLD_KEY, Capes.identifier("fakeworld")), BuiltinRegistries.DIMENSION_TYPE.entryOf(DimensionTypes.OVERWORLD), 0, 0, null, null, false, 0L)
}
