package me.cael.capes

import com.mojang.authlib.GameProfile
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

enum class CapeType(val stylized: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), LABYMOD("LabyMod"), WYNNTILS("Wynntils"), MINECRAFTCAPES("MinecraftCapes");

    fun cycle() = when(this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> LABYMOD
        LABYMOD -> WYNNTILS
        WYNNTILS -> MINECRAFTCAPES
        MINECRAFTCAPES -> MINECRAFT
    }

    fun getURL(profile: GameProfile): String? {
        val config = AutoConfig.getConfigHolder(CapeConfig::class.java).config
        return when (this) {
            OPTIFINE -> if(config.enableOptifine) "http://s.optifine.net/capes/${profile.name}.png" else null
            LABYMOD -> if(config.enableLabyMod) "https://dl.labymod.net/capes/${profile.id}" else null
            WYNNTILS -> if(config.enableWynntils) "https://athena.wynntils.com/user/getInfo" else null
            MINECRAFTCAPES -> if(config.enableMinecraftCapesMod) "https://minecraftcapes.net/profile/${profile.id.toString().replace("-", "")}" else null
            MINECRAFT -> null
        }
    }

    fun getToggleText(enabled: Boolean): Text = ScreenTexts.composeToggleText(Text.of(stylized), enabled)

    fun getText(): TranslatableText = TranslatableText("options.capes.capetype", stylized)

}
