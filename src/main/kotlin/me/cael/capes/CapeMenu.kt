package me.cael.capes

import me.cael.capes.mixins.AccessorPlayerListEntry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.Util
import java.math.BigInteger
import java.util.*

class CapeMenu(parent: Screen, gameOptions: GameOptions) : GameOptionsScreen(parent, gameOptions, MutableText.of(TranslatableTextContent("options.capes.title"))) {

    override fun init() {
        val config = Capes.CONFIG
        addDrawableChild(ButtonWidget(width / 2 - 155, height / 6, 150, 20, config.clientCapeType.getText()) { buttonWidget: ButtonWidget ->
            config.clientCapeType = config.clientCapeType.cycle()
            config.save()
            buttonWidget.message = config.clientCapeType.getText()
            if (this.client?.player != null) {
                val playerListEntry = this.client!!.networkHandler!!.getPlayerListEntry(this.client!!.player!!.uuid) as AccessorPlayerListEntry
                playerListEntry.setTexturesLoaded(false)
            }
        })
        addDrawableChild(ButtonWidget(width / 2 - 155 + 160, height / 6, 150, 20, elytraMessage(config.enableElytraTexture)) { buttonWidget: ButtonWidget ->
            config.enableElytraTexture = !config.enableElytraTexture
            config.save()
            buttonWidget.message = elytraMessage(config.enableElytraTexture)
        }).active = !FabricLoader.getInstance().getModContainer("capetweaks").isPresent
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
        addDrawableChild(ButtonWidget(width / 2 - 155 + 160, height / 6 + 3 * 24, 150, 20, MutableText.of(TranslatableTextContent("options.capes.optifineeditor"))) { buttonWidget: ButtonWidget ->
            try {
                val random1Bi = BigInteger(128, Random())
                val random2Bi = BigInteger(128, Random(System.identityHashCode(Object()).toLong()))
                val serverId = random1Bi.xor(random2Bi).toString(16)
                client!!.sessionService.joinServer(client!!.session.profile, client!!.session.accessToken, serverId)
                val url = "https://optifine.net/capeChange?u=${client!!.session.uuid}&n=${client!!.session.username}&s=$serverId"
                client!!.setScreen(ConfirmChatLinkScreen({ bool: Boolean ->
                    if (bool) {
                        Util.getOperatingSystem().open(url)
                    }
                    client!!.setScreen(this)
                }, url, true))
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        })

        addDrawableChild(ButtonWidget(width / 2 - 100, height / 6 + 4 * 24, 200, 20, ScreenTexts.DONE) { buttonWidget: ButtonWidget ->
            client!!.setScreen(parent)
        })
    }

    private fun elytraMessage(enabled: Boolean) = ScreenTexts.composeToggleText(MutableText.of(TranslatableTextContent("options.capes.elytra")), enabled)

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        DrawableHelper.drawCenteredText(matrices, textRenderer, title, width / 2, 20, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
    }
}