package me.cael.capes.menu

import com.mojang.blaze3d.systems.RenderSystem
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import me.cael.capes.mixins.AccessorPlayerEntityModel
import me.cael.capes.mixins.AccessorPlayerListEntry
import me.cael.capes.utils.FakePlayer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3f


class SelectorMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    var yaw = 0f
    var showBody = true
    var lastTime = 0L

    override fun init() {
        super.init()

        var buttonW = 200
        val config = Capes.CONFIG

        addDrawableChild(ButtonWidget((width/2) - (buttonW / 2), 60, buttonW, 20, config.clientCapeType.getText()) { buttonWidget: ButtonWidget ->
            config.clientCapeType = config.clientCapeType.cycle()
            config.save()
            buttonWidget.message = config.clientCapeType.getText()
            FakePlayer.capeLoaded = false
            if (this.client?.player != null) {
                val playerListEntry = this.client!!.networkHandler!!.getPlayerListEntry(this.client!!.player!!.uuid) as AccessorPlayerListEntry
                playerListEntry.setTexturesLoaded(false)
            }
        })

        buttonW = 100

        addDrawableChild(ButtonWidget((width/4) - (buttonW / 2), 120, buttonW, 20, Text.translatable("options.capes.selector.elytra")) { buttonWidget: ButtonWidget ->
            FakePlayer.showElytra = !FakePlayer.showElytra
        })

        addDrawableChild(ButtonWidget((width/4) - (buttonW / 2), 145, buttonW, 20, Text.translatable("options.capes.selector.player")) { buttonWidget: ButtonWidget ->
            showBody = !showBody
        })
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val playerX : Int = width/2
        val playerY = 215

        val time = System.currentTimeMillis()

        if (time > lastTime + (1000 / 60)) {
            lastTime = time
            FakePlayer.prevX = FakePlayer.x + 0.025
            FakePlayer.updateLimbs(FakePlayer, false)
        }
        drawPlayer(playerX, playerY, 70, FakePlayer)
    }

    fun drawPlayer(x: Int, y: Int, size: Int, entity: ClientPlayerEntity) {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.translate(x.toDouble(), y.toDouble(), 1050.0)
        matrixStack.scale(1.0f, 1.0f, -1.0f)
        RenderSystem.applyModelViewMatrix()
        val matrixStack2 = MatrixStack()
        matrixStack2.translate(0.0, 0.0, 1000.0)
        matrixStack2.scale(size.toFloat(), size.toFloat(), size.toFloat())
        val quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f)
        matrixStack2.multiply(quaternion)
        val h = entity.bodyYaw
        val i = entity.yaw
        val j = entity.pitch
        val k = entity.prevHeadYaw
        val l = entity.headYaw
        entity.bodyYaw = yaw
        entity.yaw = yaw
        entity.pitch = 0f
        entity.headYaw = entity.yaw
        entity.prevHeadYaw = entity.yaw
        DiffuseLighting.method_34742()
        val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
        entityRenderDispatcher.setRenderShadows(false)
        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        RenderSystem.runAsFancy {
            val playerEntityRenderer = entityRenderDispatcher.getRenderer(entity) as PlayerEntityRenderer
            playerEntityRenderer.model.setVisible(showBody)
            (playerEntityRenderer.model as AccessorPlayerEntityModel).cloak.visible = true
            playerEntityRenderer.render(entity, 0.0f, 1.0f, matrixStack2, immediate, 0xF000F0)
        }
        immediate.draw()
        entityRenderDispatcher.setRenderShadows(true)
        entity.bodyYaw = h
        entity.yaw = i
        entity.pitch = j
        entity.prevHeadYaw = k
        entity.headYaw = l
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        this.yaw = yaw - deltaX.toFloat()
        return true
    }

    private fun elytraMessage(enabled: Boolean) = ScreenTexts.composeToggleText(Text.translatable("options.capes.elytra"), enabled)

}