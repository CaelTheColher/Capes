package me.cael.capes.menu

import com.mojang.blaze3d.systems.RenderSystem
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import me.cael.capes.render.DisplayPlayerEntityRenderer
import me.cael.capes.render.PlaceholderEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.math.RotationAxis

class SelectorMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    var lastTime = 0L

    override fun init() {
        super.init()

        var buttonW = 200
        val config = Capes.CONFIG

        addDrawableChild(ButtonWidget.builder(config.clientCapeType.getText()) {
            config.clientCapeType = config.clientCapeType.cycle()
            config.save()
            it.message = config.clientCapeType.getText()
            PlaceholderEntity.capeLoaded = false
            (client!!.player?: return@builder).uuid.let(PlayerHandler.Companion::refreshListEntry)
        }.position((width / 2) - (buttonW / 2), 60).size(buttonW, 20).build())

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) {
            client!!.setScreen(parent)
        }.position((width / 2) - (buttonW / 2), 220).size(buttonW, 20).build())

        buttonW = 100

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.selector.elytra")) {
            PlaceholderEntity.showElytra = !PlaceholderEntity.showElytra
        }.position((width / 4) - (buttonW / 2), 120).size(buttonW, 20).build())

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.selector.player")) {
            PlaceholderEntity.showBody = !PlaceholderEntity.showBody
        }.position((width / 4) - (buttonW / 2), 145).size(buttonW, 20).build())

    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val playerX : Int = width/2
        val playerY = 215

        val time = System.currentTimeMillis()

        val entity = PlaceholderEntity

        if (time > lastTime + (1000 / 60)) {
            lastTime = time
            entity.prevX = entity.x + 0.025
            entity.updateLimbs()
        }
        drawPlayer(playerX, playerY, 70, entity)
    }


    fun drawPlayer(x: Int, y: Int, size: Int, entity: PlaceholderEntity) {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.translate(x.toDouble(), y.toDouble(), 1050.0)
        matrixStack.scale(1.0f, 1.0f, -1.0f)
        RenderSystem.applyModelViewMatrix()
        val matrixStack2 = MatrixStack()
        matrixStack2.translate(0.0, 0.0, 1000.0)
        matrixStack2.scale(size.toFloat(), size.toFloat(), size.toFloat())

        val quaternion = RotationAxis.POSITIVE_Z.rotationDegrees(180.0f)
        matrixStack2.multiply(quaternion)

        DiffuseLighting.method_34742()
        val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
        entityRenderDispatcher.setRenderShadows(false)
        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        RenderSystem.runAsFancy {
            val ctx = EntityRendererFactory.Context(
                MinecraftClient.getInstance().entityRenderDispatcher,
                MinecraftClient.getInstance().itemRenderer,
                MinecraftClient.getInstance().blockRenderManager,
                MinecraftClient.getInstance().entityRenderDispatcher.heldItemRenderer,
                MinecraftClient.getInstance().resourceManager,
                MinecraftClient.getInstance().entityModelLoader,
                MinecraftClient.getInstance().textRenderer
            )
            val displayPlayerEntityRenderer = DisplayPlayerEntityRenderer(ctx, entity.slim)
            displayPlayerEntityRenderer.render(entity, 1.0f, matrixStack2, immediate, 0xF000F0)
        }
        immediate.draw()
        entityRenderDispatcher.setRenderShadows(true)
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        PlaceholderEntity.prevYaw = PlaceholderEntity.yaw
        PlaceholderEntity.yaw -= deltaX.toFloat()
        return true
    }

}
