package me.cael.capes.menu

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.text.Text

open class MainMenu(parent: Screen, gameOptions: GameOptions) : GameOptionsScreen(parent, gameOptions, Text.translatable("options.capes.title")) {

    override fun init() {

        val buttonW = 100
        val offset = (buttonW / 2) + 5

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.selector")) {
            client!!.setScreen(SelectorMenu(parent, gameOptions))
        }.position((width/2) - (buttonW / 2), 35).size(buttonW, 20).build())
            .active = this !is SelectorMenu

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.toggle")) {
            client!!.setScreen(ToggleMenu(parent, gameOptions))
        }.position((width/2) - (buttonW + offset), 35).size(buttonW, 20).build())
            .active = this !is ToggleMenu

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.other")) {
            client!!.setScreen(OtherMenu(parent, gameOptions))
        }.position((width/2) + offset, 35).size(buttonW, 20).build())
            .active = this !is OtherMenu

    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 16777215)
        super.render(context, mouseX, mouseY, delta)
    }
}