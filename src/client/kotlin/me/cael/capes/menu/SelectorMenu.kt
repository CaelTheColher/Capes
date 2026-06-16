package me.cael.capes.menu

import me.cael.capes.Capes
import me.cael.capes.render.PlaceholderEntity
import me.cael.capes.render.PlaceholderEntityRenderState
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.Options
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.joml.Quaternionf
import org.joml.Vector3f

class SelectorMenu(parent: Screen, gameOptions: Options) : MainMenu(parent, gameOptions) {

    var lastTime = 0L

    override fun init() {
        super.init()

        var buttonW = 200
        val config = Capes.CONFIG

        addRenderableWidget(Button.builder(config.clientCapeType.getText()) {
            config.clientCapeType = config.clientCapeType.cycle()
            config.save()
            it.message = config.clientCapeType.getText()
            PlaceholderEntity.capeLoaded = false
        }.pos((width / 2) - (buttonW / 2), 60).size(buttonW, 20).build())

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) {
            minecraft!!.gui.setScreen(lastScreen)
        }.pos((width / 2) - (buttonW / 2), 220).size(buttonW, 20).build())

        buttonW = 100

        addRenderableWidget(Button.builder(Component.translatable("options.capes.selector.player")) {
            PlaceholderEntity.showBody = !PlaceholderEntity.showBody
        }.pos((width / 4) - (buttonW / 2), 145).size(buttonW, 20).build())

        addRenderableWidget(Button.builder(Component.translatable("options.capes.selector.elytra")) {
            PlaceholderEntity.showElytra = !PlaceholderEntity.showElytra
        }.pos((width / 4) - (buttonW / 2), 120).size(buttonW, 20).build())

        addRenderableWidget(Button.builder(Component.literal("DO NOT ASK WHY THIS EXISTS")) {
        }.size(0, 0).build())

    }


    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, delta)

        val playerX = (width/2) - 50
        val playerY = 65

        val time = System.currentTimeMillis()

        val entity = PlaceholderEntity

        if (time > lastTime + (1000 / 60)) {
            lastTime = time
            entity.prevX = entity.x + 0.025
            entity.updateLimbs()
        }

        drawEntity(graphics, playerX, playerY, playerX + 100, playerY + 300, PlaceholderEntity);
    }

    fun drawEntity(context: GuiGraphicsExtractor, x1: Int, y1: Int, x2: Int, y2: Int, entity: PlaceholderEntity) {
        context.enableScissor(x1, y1, x2, y2)

        val entityRenderer = PlaceholderEntity.renderer
        val entityRenderState: PlaceholderEntityRenderState = entityRenderer.getAndUpdatePlaceholderRenderState(entity)

        context.entity(entityRenderState, 69f, Vector3f(0.0f, 0.0f, 0.0f), Quaternionf().rotateZ(Math.PI.toFloat()), null, x1, y1, x2, y2)
        context.disableScissor()
    }

    override fun mouseDragged(click: MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean {
        super.mouseDragged(click, offsetX, offsetY)
        PlaceholderEntity.prevYaw = PlaceholderEntity.yaw
        PlaceholderEntity.yaw -= offsetX.toFloat()
        return true
    }

}