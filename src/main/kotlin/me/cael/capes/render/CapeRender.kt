package me.cael.capes.render

import me.cael.capes.compatibility.TrinketsCompatibility
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.PlayerModelPart
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper

class CapeRender(context: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(context) {
    override fun render(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, entity: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float, animationProgress: Float, headYaw: Float, headPitch: Float
    ) {
        val playerHandler = PlayerHandler.fromPlayer(entity)
        val capeTexture = when(entity) {
            MinecraftClient.getInstance().player -> playerHandler.capeTexture ?: entity.capeTexture
            else -> entity.capeTexture ?: playerHandler.capeTexture
        }
        if (entity.canRenderCapeTexture() && !entity.isInvisible && entity.isPartVisible(PlayerModelPart.CAPE) && capeTexture != null) {
            val itemStack: ItemStack = entity.getEquippedStack(EquipmentSlot.CHEST)
            if (itemStack.item !== Items.ELYTRA && !TrinketsCompatibility.displayElytra(entity)) {
                matrices.push()
                matrices.translate(0.0, 0.0, 0.125)
                val d = MathHelper.lerp(tickDelta.toDouble(), entity.prevCapeX,entity.capeX) - MathHelper.lerp(tickDelta.toDouble(), entity.prevX, entity.x)
                val e = MathHelper.lerp(tickDelta.toDouble(), entity.prevCapeY,entity.capeY) - MathHelper.lerp(tickDelta.toDouble(), entity.prevY, entity.y)
                val m = MathHelper.lerp(tickDelta.toDouble(), entity.prevCapeZ,entity.capeZ) - MathHelper.lerp(tickDelta.toDouble(), entity.prevZ, entity.z)
                val n = entity.prevBodyYaw + (entity.bodyYaw - entity.prevBodyYaw)
                val o = MathHelper.sin(n * 0.017453292f).toDouble()
                val p = (-MathHelper.cos(n * 0.017453292f)).toDouble()
                var q = e.toFloat() * 10.0f
                q = MathHelper.clamp(q, -6.0f, 32.0f)
                var r = (d * o + m * p).toFloat() * 100.0f
                r = MathHelper.clamp(r, 0.0f, 150.0f)
                var s = (d * p - m * o).toFloat() * 100.0f
                s = MathHelper.clamp(s, -20.0f, 20.0f)
                if (r < 0.0f) {
                    r = 0.0f
                }
                val t = MathHelper.lerp(tickDelta, entity.prevStrideDistance, entity.strideDistance)
                q += MathHelper.sin((MathHelper.lerp(tickDelta, entity.prevHorizontalSpeed, entity.horizontalSpeed) * 6.0f)) * 32.0f * t
                if (entity.isInSneakingPose) {
                    q += 25.0f
                }
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(6.0f + r / 2.0f + q))
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(s / 2.0f))
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - s / 2.0f))
                val vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(capeTexture), false, playerHandler.glint)
                (this.contextModel as PlayerEntityModel<*>).renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV)
                matrices.pop()
            }
        }
    }

}