package me.cael.capes.menu

import me.cael.capes.Capes
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.Options
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import java.math.BigInteger
import java.util.*

class OtherMenu(parent: Screen, gameOptions: Options) : MainMenu(parent, gameOptions) {

    override fun init() {
        super.init()

        val buttonW = 200

        addRenderableWidget(Button.builder(Component.translatable("options.capes.optifineeditor")) {
            try {
                val random1Bi = BigInteger(128, Random())
                val random2Bi = BigInteger(128, Random(System.identityHashCode(Object()).toLong()))
                val serverId = random1Bi.xor(random2Bi).toString(16)
                minecraft!!.services().sessionService.joinServer(minecraft!!.gameProfile.id, minecraft!!.user.accessToken, serverId)
                val url = "https://optifine.net/capeChange?u=${minecraft!!.gameProfile.id.toString().replace("-", "")}&n=${minecraft!!.user.name}&s=$serverId"
                minecraft!!.gui.setScreen(ConfirmLinkScreen({ bool: Boolean ->
                    if (bool) {
                        Util.getPlatform().openUri(url)
                    }
                    minecraft!!.gui.setScreen(this)
                }, url, true))
            } catch (_: Exception) {
                Capes.LOGGER.error("Failed to authenticate for OptiFine cape editor.")
            }

        }.pos((width/2) - (buttonW / 2), height / 7 + 24).size(buttonW, 20).build())

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) {
            minecraft!!.gui.setScreen(lastScreen)
        }.pos((width/2) - (buttonW / 2), height / 7 + 2 * 24).size(buttonW, 20).build())

    }

}