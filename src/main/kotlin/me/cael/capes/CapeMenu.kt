package me.cael.capes

import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.gui.screen.options.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.options.GameOptions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class CapeMenu(parent: Screen, gameOptions: GameOptions) : GameOptionsScreen(parent, gameOptions, Text.of("Cape Options")) {

    override fun init() {
        addButton(ButtonWidget(width / 2 - 75, height / 6, 150, 20, Text.of("Cape Type: ${PlayerHandler.capeType.name}")
        ) { buttonWidget: ButtonWidget ->
            PlayerHandler.capeType = PlayerHandler.capeType.cycle()
            buttonWidget.message = Text.of("Cape Type: ${PlayerHandler.capeType.name}")
            if (this.client?.player != null) PlayerHandler.onPlayerJoin(this.client!!.player as AbstractClientPlayerEntity)
        })
        addButton(ButtonWidget(width / 2 - 100, height / 6 + 24, 200, 20, ScreenTexts.DONE) { buttonWidget: ButtonWidget ->
            client!!.openScreen(parent)
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        DrawableHelper.drawCenteredText(matrices, textRenderer, title,width / 2,20, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
    }
}