package me.cael.capes.menu

import me.cael.capes.CapeType
import me.cael.capes.Capes
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.Options
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

class ToggleMenu(parent: Screen, gameOptions: Options) : MainMenu(parent, gameOptions) {

    override fun init() {
        super.init()

        val config = Capes.CONFIG

        addRenderableWidget(Button.builder(CapeType.OPTIFINE.getToggleText(config.enableOptifine)) {
            config.enableOptifine = !config.enableOptifine
            config.save()
            it.message = CapeType.OPTIFINE.getToggleText(config.enableOptifine)
        }.pos(width / 2 - 155, height / 7 + 24).size(150, 20).build())

        addRenderableWidget(Button.builder(CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)) {
            config.enableMinecraftCapesMod = !config.enableMinecraftCapesMod
            config.save()
            it.message = CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)
        }.pos(width / 2 - 155 + 160, height / 7 + 24).size(150, 20).build())

        addRenderableWidget(Button.builder(CapeType.LABYMOD.getToggleText(config.enableLabyMod)) {
            config.enableLabyMod = !config.enableLabyMod
            config.save()
            it.message = CapeType.LABYMOD.getToggleText(config.enableLabyMod)
        }.pos(width / 2 - 155, height / 7 + 2 * 24).size(150, 20).build())

        addRenderableWidget(Button.builder(CapeType.COSMETICA.getToggleText(config.enableCosmetica)) {
            config.enableCosmetica = !config.enableCosmetica
            config.save()
            it.message = CapeType.COSMETICA.getToggleText(config.enableCosmetica)
        }.pos(width / 2 - 155 + 160, height / 7 + 2 * 24).size(150, 20).build())

        addRenderableWidget(Button.builder(CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)) {
            config.enableCloaksPlus = !config.enableCloaksPlus
            config.save()
            it.message = CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)
        }.pos(width / 2 - 155, height / 7 + 3 * 24).size(150, 20).build())

//        addDrawableChild(ButtonWidget.builder(CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)) {
//            config.enableCloaksPlus = !config.enableCloaksPlus
//            config.save()
//            it.message = CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)
//        }.position(width / 2 - 155 + 160, height / 7 + 3 * 24).size(150, 20).build())

        addRenderableWidget(Button.builder(elytraMessage(config.enableElytraTexture)) {
            config.enableElytraTexture = !config.enableElytraTexture
            config.save()
            it.message = elytraMessage(config.enableElytraTexture)
        }.pos((width/2) - (200 / 2), height / 7 + 4 * 24).size(200, 20).build())

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) {
            minecraft!!.setScreen(lastScreen)
        }.pos((width/2) - (200 / 2), height / 7 + 5 * 24).size(200, 20).build())

    }

    private fun elytraMessage(enabled: Boolean) = CommonComponents.optionStatus(Component.translatable("options.capes.elytra"), enabled)

}