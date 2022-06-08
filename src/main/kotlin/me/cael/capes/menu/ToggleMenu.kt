package me.cael.capes.menu

import me.cael.capes.CapeType
import me.cael.capes.Capes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

class ToggleMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    override fun init() {
        super.init()

        val config = Capes.CONFIG

        addDrawableChild(ButtonWidget(width / 2 - 155, height / 6 + 24, 150, 20, CapeType.OPTIFINE.getToggleText(config.enableOptifine)) { buttonWidget: ButtonWidget ->
            config.enableOptifine = !config.enableOptifine
            config.save()
            buttonWidget.message = CapeType.OPTIFINE.getToggleText(config.enableOptifine)
        })

        addDrawableChild(ButtonWidget(width / 2 - 155 + 160, height / 6 + 24, 150, 20, CapeType.LABYMOD.getToggleText(config.enableLabyMod)) { buttonWidget: ButtonWidget ->
            config.enableLabyMod = !config.enableLabyMod
            config.save()
            buttonWidget.message = CapeType.LABYMOD.getToggleText(config.enableLabyMod)
        })

        addDrawableChild(ButtonWidget(width / 2 - 155, height / 6 + 2 * 24, 150, 20, CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)) { buttonWidget: ButtonWidget ->
            config.enableMinecraftCapesMod = !config.enableMinecraftCapesMod
            config.save()
            buttonWidget.message = CapeType.MINECRAFTCAPES.getToggleText(config.enableMinecraftCapesMod)
        })

        addDrawableChild(ButtonWidget(width / 2 - 155 + 160, height / 6 + 2 * 24, 150, 20, CapeType.WYNNTILS.getToggleText(config.enableWynntils)) { buttonWidget: ButtonWidget ->
            config.enableWynntils = !config.enableWynntils
            config.save()
            buttonWidget.message = CapeType.WYNNTILS.getToggleText(config.enableWynntils)
        })

        addDrawableChild(ButtonWidget(width / 2 - 155, height / 6 + 3 * 24, 150, 20, CapeType.COSMETICA.getToggleText(config.enableCosmetica)) { buttonWidget: ButtonWidget ->
            config.enableCosmetica = !config.enableCosmetica
            config.save()
            buttonWidget.message = CapeType.COSMETICA.getToggleText(config.enableCosmetica)
        })

        addDrawableChild(ButtonWidget(width / 2 - 155 + 160, height / 6 + 3 * 24, 150, 20, CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)) { buttonWidget: ButtonWidget ->
            config.enableCloaksPlus = !config.enableCloaksPlus
            config.save()
            buttonWidget.message = CapeType.CLOAKSPLUS.getToggleText(config.enableCloaksPlus)
        })

        addDrawableChild(ButtonWidget((width/2) - (200 / 2), height / 6 + 4 * 24, 200, 20, elytraMessage(config.enableElytraTexture)) { buttonWidget: ButtonWidget ->
            config.enableElytraTexture = !config.enableElytraTexture
            config.save()
            buttonWidget.message = elytraMessage(config.enableElytraTexture)
        }).active = !FabricLoader.getInstance().getModContainer("capetweaks").isPresent

    }

    private fun elytraMessage(enabled: Boolean) = ScreenTexts.composeToggleText(Text.translatable("options.capes.elytra"), enabled)

}