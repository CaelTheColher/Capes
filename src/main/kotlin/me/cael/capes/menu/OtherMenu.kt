package me.cael.capes.menu

import net.minecraft.client.gui.screen.ConfirmChatLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.math.BigInteger
import java.util.*

class OtherMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    override fun init() {
        super.init()

        val buttonW = 200

        addDrawableChild(ButtonWidget((width/2) - (buttonW / 2), height / 7 + 24, buttonW, 20, Text.translatable("options.capes.optifineeditor")) { buttonWidget: ButtonWidget ->
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

        addDrawableChild(ButtonWidget((width/2) - (buttonW / 2), height / 7 + 2 * 24, buttonW, 20, ScreenTexts.DONE) { buttonWidget: ButtonWidget ->
            client!!.setScreen(parent)
        })
    }

}