package me.cael.capes.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.model.ElytraEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerModelPart
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis

class DisplayPlayerEntityRenderer(val ctx: EntityRendererFactory.Context, slim: Boolean) :
    LivingEntityRenderer<LivingEntity, PlayerEntityRenderState, PlayerEntityModel> (
        ctx,
        PlayerEntityModel(
            ctx.getPart(if (slim) EntityModelLayers.PLAYER_SLIM else EntityModelLayers.PLAYER),
            slim
        ),
        0.5f
    ) {

    val elytra = ElytraEntityModel(ctx.modelLoader.getModelPart(EntityModelLayers.ELYTRA))

    fun render(livingEntity : PlaceholderEntity, tickDelta: Float, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, light: Int) {
        setModelPose()
        matrixStack.push()

        matrixStack.scale(0.9375f, 0.9375f, 0.9375f)
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - livingEntity.yaw))
        matrixStack.scale(-1.0f, -1.0f, 1.0f)
        matrixStack.translate(0.0f, -1.501f, 0.0f)

        var limbDistance = MathHelper.lerp(tickDelta, livingEntity.lastLimbDistance, livingEntity.limbDistance)
        val limbAngle = livingEntity.limbAngle - livingEntity.limbDistance * (1.0f - tickDelta)

        if (limbDistance > 1.0f) {
            limbDistance = 1.0f
        }

        setAngles(limbAngle, limbDistance)

        if (livingEntity.showBody) {
            val renderLayer = this.model.getLayer(PlaceholderEntity.getSkinTexture())
            val vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer)
            val overlay = OverlayTexture.packUv(OverlayTexture.getU(0f), OverlayTexture.getV(false))
            model.render(matrixStack, vertexConsumer, light, overlay)
        }

        if (!PlaceholderEntity.showElytra) {
            if (PlaceholderEntity.getCapeTexture() == null) return
            matrixStack.push()

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f))
            val vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(PlaceholderEntity.getCapeTexture()))
            ctx.getPart(EntityModelLayers.PLAYER_CAPE).getChild("body").getChild("cape")
                .render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV)
            matrixStack.pop()
        } else {
            val identifier = PlaceholderEntity.getElytraTexture()
            matrixStack.push()
            matrixStack.translate(0.0f, 0.0f, 0.125f)

            val vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(identifier), false)
            this.elytra.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV)
            matrixStack.pop()
        }


        matrixStack.pop()
    }

    fun setAngles(f: Float, g: Float) {
        model.body.yaw = 0.0f
        model.rightArm.pivotZ = 0.0f
        model.rightArm.pivotX = -5.0f
        model.leftArm.pivotZ = 0.0f
        model.leftArm.pivotX = 5.0f

        model.rightArm.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 2.0f * g * 0.5f
        model.leftArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f
        model.rightArm.roll = 0.0f
        model.leftArm.roll = 0.0f
        model.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g
        model.leftLeg.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 1.4f * g
        model.rightLeg.yaw = 0.0f
        model.leftLeg.yaw = 0.0f
        model.rightLeg.roll = 0.0f
        model.leftLeg.roll = 0.0f

        model.rightArm.yaw = 0.0f
        model.leftArm.yaw = 0.0f

        model.body.pitch = 0.0f
        model.rightLeg.pivotZ = 0.1f
        model.leftLeg.pivotZ = 0.1f
        model.rightLeg.pivotY = 12.0f
        model.leftLeg.pivotY = 12.0f
        model.head.pivotY = 0.0f
        model.body.pivotY = 0.0f
        model.leftArm.pivotY = 2.0f
        model.rightArm.pivotY = 2.0f
    }
    
    private fun setModelPose() {
        val options = MinecraftClient.getInstance().options
        val playerEntityModel = this.getModel()
        playerEntityModel.setVisible(true)
        playerEntityModel.hat.visible = options.isPlayerModelPartEnabled(PlayerModelPart.HAT)
        playerEntityModel.jacket.visible = options.isPlayerModelPartEnabled(PlayerModelPart.JACKET)
        playerEntityModel.leftPants.visible = options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG)
        playerEntityModel.rightPants.visible = options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG)
        playerEntityModel.leftSleeve.visible = options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE)
        playerEntityModel.rightSleeve.visible = options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE)
    }

    override fun getTexture(state: PlayerEntityRenderState?): Identifier = DefaultSkinHelper.getTexture()

    override fun createRenderState(): PlayerEntityRenderState = PlayerEntityRenderState()
}