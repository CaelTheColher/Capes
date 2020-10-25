package me.cael.capes.render

import me.cael.capes.compatibility.TrinketsCompatibility
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.PlayerModelPart
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.ElytraEntityModel
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Identifier

class ElytraRender<T : LivingEntity?, M : EntityModel<T>>(featureRendererContext: FeatureRendererContext<T, M>) : FeatureRenderer<T, M>(featureRendererContext) {
    private val SKIN = Identifier("textures/entity/elytra.png")
    private val elytra: ElytraEntityModel<T> = ElytraEntityModel()
    override fun render(matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, i: Int, entity: T, f: Float, g: Float, h: Float, j: Float, k: Float, l: Float) {
        val itemStack = entity!!.getEquippedStack(EquipmentSlot.CHEST)
        if (itemStack.item === Items.ELYTRA || TrinketsCompatibility.displayElytra(entity as PlayerEntity)) {
            val identifier4: Identifier = (if (entity is AbstractClientPlayerEntity && entity.isPartVisible(PlayerModelPart.CAPE)) {
                val playerHandler = PlayerHandler.fromPlayer(entity)
                when(entity) {
                    MinecraftClient.getInstance().player -> playerHandler.capeTexture ?: entity.capeTexture
                    else -> entity.capeTexture ?: playerHandler.capeTexture
                }
            } else SKIN) ?: SKIN
            matrixStack.push()
            matrixStack.translate(0.0, 0.0, 0.125)
            this.contextModel!!.copyStateTo(elytra)
            elytra.setAngles(entity, f, g, j, k, l)
            val vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(identifier4), false, itemStack.hasGlint())
            elytra.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
            matrixStack.pop()
        }
    }
}