package me.cael.capes.menu

import net.minecraft.client.Options
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.OptionsSubScreen
import net.minecraft.network.chat.Component

open class MainMenu(parent: Screen, gameOptions: Options) : OptionsSubScreen(parent, gameOptions, Component.translatable("options.capes.title")) {

    override fun init() {
        val buttonW = 100
        val offset = (buttonW / 2) + 5

        addRenderableWidget(Button.builder(Component.translatable("options.capes.selector")) {
            minecraft.gui.setScreen(SelectorMenu(lastScreen, options))
        }.pos((width/2) - (buttonW / 2), 35).size(buttonW, 20).build())
            .active = this !is SelectorMenu

        addRenderableWidget(Button.builder(Component.translatable("options.capes.toggle")) {
            minecraft.gui.setScreen(ToggleMenu(lastScreen, options))
        }.pos((width/2) - (buttonW + offset), 35).size(buttonW, 20).build())
            .active = this !is ToggleMenu

        addRenderableWidget(Button.builder(Component.translatable("options.capes.other")) {
            minecraft.gui.setScreen(OtherMenu(lastScreen, options))
        }.pos((width/2) + offset, 35).size(buttonW, 20).build())
            .active = this !is OtherMenu
    }

    override fun addOptions() {
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, delta)
        graphics.centeredText(font, title, width / 2, 20, 16777215)
    }
}