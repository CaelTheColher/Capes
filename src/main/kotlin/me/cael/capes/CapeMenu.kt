package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.gui.screen.options.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.options.GameOptions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class CapeMenu(parent: Screen, gameOptions: GameOptions) : GameOptionsScreen(parent, gameOptions, TranslatableText("options.capes.title")) {

    override fun init() {
        val configManager = AutoConfig.getConfigHolder(CapeConfig::class.java) as ConfigManager
        val config = configManager.config
        addButton(ButtonWidget(width / 2 - 155, height / 6, 150, 20, config.clientCapeType.getText()) { buttonWidget: ButtonWidget ->
            config.clientCapeType = config.clientCapeType.cycle()
            configManager.save()
            buttonWidget.message = config.clientCapeType.getText()
            if (this.client?.player != null) PlayerHandler.onPlayerJoin(this.client!!.player as AbstractClientPlayerEntity)
        })
        addButton(ButtonWidget(width / 2 - 155 + 160, height / 6, 150, 20, glintMessage(config.glint)) { buttonWidget: ButtonWidget ->
            config.glint = !config.glint
            configManager.save()
            buttonWidget.message = glintMessage(config.glint)
        })
        addButton(ButtonWidget(width / 2 - 100, height / 6 + 24, 200, 20, ScreenTexts.DONE) { buttonWidget: ButtonWidget ->
            client!!.openScreen(parent)
        })
    }

    private fun glintMessage(glint: Boolean): Text {
        return ScreenTexts.composeToggleText(TranslatableText("options.capes.glint"), glint)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        DrawableHelper.drawCenteredText(matrices, textRenderer, title, width / 2, 20, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
    }
}