package me.cael.capes.menu

import me.cael.capes.CapeType
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

class ToggleMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    override fun init() {
        super.init()

        val config = Capes.CONFIG

        addDrawableChild(ButtonWidget.builder(CapeType.OPTIFINE.getToggleText(config.enableOptifine)) {
            config.enableOptifine = !config.enableOptifine
            config.save()
            it.message = CapeType.OPTIFINE.getToggleText(config.enableOptifine)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155, height / 7 + 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(CapeType.LABYMOD.getToggleText(config.enableLabyMod)) {
            config.enableLabyMod = !config.enableLabyMod
            config.save()
            it.message = CapeType.LABYMOD.getToggleText(config.enableLabyMod)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155 + 160, height / 7 + 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)) {
            config.enableMinecraftCapesMod = !config.enableMinecraftCapesMod
            config.save()
            it.message = CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155, height / 7 + 2 * 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(CapeType.WYNNTILS.getToggleText(config.enableWynntils)) {
            config.enableWynntils = !config.enableWynntils
            config.save()
            it.message = CapeType.WYNNTILS.getToggleText(config.enableWynntils)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155 + 160, height / 7 + 2 * 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(CapeType.COSMETICA.getToggleText(config.enableCosmetica)) {
            config.enableCosmetica = !config.enableCosmetica
            config.save()
            it.message = CapeType.COSMETICA.getToggleText(config.enableCosmetica)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155, height / 7 + 3 * 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)) {
            config.enableCloaksPlus = !config.enableCloaksPlus
            config.save()
            it.message = CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)
            PlayerHandler.refreshListEntries()
        }.position(width / 2 - 155 + 160, height / 7 + 3 * 24).size(150, 20).build())

        addDrawableChild(ButtonWidget.builder(elytraMessage(config.enableElytraTexture)) {
            config.enableElytraTexture = !config.enableElytraTexture
            config.save()
            it.message = elytraMessage(config.enableElytraTexture)
            PlayerHandler.refreshListEntries()
        }.position((width / 2) - (200 / 2), height / 7 + 4 * 24).size(200, 20).build())

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) {
            client!!.setScreen(parent)
        }.position((width / 2) - (200 / 2), height / 7 + 5 * 24).size(200, 20).build())
    }

    private fun elytraMessage(enabled: Boolean) = ScreenTexts.composeToggleText(Text.translatable("options.capes.elytra"), enabled)
}
