package me.cael.capes.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.CapeLayer
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer
import net.minecraft.client.renderer.entity.layers.WingsLayer
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.util.Unit
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable

class PlaceholderEntityRenderer(ctx: EntityRendererProvider.Context, slim: Boolean) :
    LivingEntityRenderer<LivingEntity, AvatarRenderState, PlayerModel>(
        ctx,
        PlayerModel(ctx.bakeLayer(
            // I'm supposed to use EntityModelLayers.PLAYER_SLIM here, but it seems there is some mapping conflict that doesn't let me use it rn
            if (slim) ModelLayerLocation(Identifier.withDefaultNamespace("player_slim"), "main")
            else ModelLayers.PLAYER), slim),
        0.5f
    ) {

    val placeholderState = createRenderState()
    init {
        this.addLayer(CapeLayer(this, ctx.modelSet, ctx.equipmentAssets))
        this.addLayer(
            CustomHeadLayer(
                this, ctx.modelSet, ctx.playerSkinRenderCache
            )
        )
        this.addLayer(
            WingsLayer(
                this,
                ctx.modelSet,
                ctx.equipmentRenderer
            )
        )
    }

    override fun submit(state: AvatarRenderState, poseStack: PoseStack, submitNodeCollector: SubmitNodeCollector, camera: CameraRenderState) {
        this.model.allParts().forEach { it.visible = PlaceholderEntity.showBody }
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun getTextureLocation(playerEntityRenderState: AvatarRenderState): Identifier {
        return playerEntityRenderState.skin.body.texturePath()
    }

    override fun scale(playerEntityRenderState: AvatarRenderState, matrixStack: PoseStack) {
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f)
    }

    override fun createRenderState(): PlaceholderEntityRenderState {
        return PlaceholderEntityRenderState()
    }

    fun getAndUpdatePlaceholderRenderState(entity: PlaceholderEntity): PlaceholderEntityRenderState {
        val entityRenderState = placeholderState
        updateRenderState(entity, entityRenderState)
        return entityRenderState
    }

    fun updateRenderState(placeholderEntity: PlaceholderEntity, playerEntityRenderState: AvatarRenderState) {
        playerEntityRenderState.bodyRot = placeholderEntity.yaw

        playerEntityRenderState.walkAnimationPos = placeholderEntity.limbAngle
        playerEntityRenderState.walkAnimationSpeed = placeholderEntity.limbDistance

        val options = Minecraft.getInstance().options
        playerEntityRenderState.leftArmPose = HumanoidModel.ArmPose.EMPTY
        playerEntityRenderState.rightArmPose = HumanoidModel.ArmPose.EMPTY
        playerEntityRenderState.skin = placeholderEntity.getSkinTextures()
        playerEntityRenderState.showHat = options.isModelPartEnabled(PlayerModelPart.HAT)
        playerEntityRenderState.showJacket = options.isModelPartEnabled(PlayerModelPart.JACKET)
        playerEntityRenderState.showLeftPants = options.isModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG)
        playerEntityRenderState.showRightPants = options.isModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG)
        playerEntityRenderState.showLeftSleeve = options.isModelPartEnabled(PlayerModelPart.LEFT_SLEEVE)
        playerEntityRenderState.showRightSleeve = options.isModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE)
        playerEntityRenderState.showCape = true

        val componentMap = DataComponentMap.builder()
            .set(DataComponents.GLIDER, Unit.INSTANCE)
            .set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).setAsset(EquipmentAssets.ELYTRA).build())
            .build()

        val elytra = ItemStack(Holder.Direct(Items.ELYTRA, componentMap));

        playerEntityRenderState.chestEquipment = if (placeholderEntity.showElytra) elytra else ItemStack.EMPTY
        playerEntityRenderState.elytraRotZ = -(Math.PI / 12).toFloat()
        playerEntityRenderState.elytraRotX = (Math.PI / 12).toFloat()

//        playerEntityRenderState.name = placeholderEntity.gameProfile.name
    }
}
