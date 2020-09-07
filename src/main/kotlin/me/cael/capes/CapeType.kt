package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText

enum class CapeType(val stylized: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), LABYMOD("LabyMod"), MINECRAFTCAPES("MinecraftCapes");

    fun cycle() = when(this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> LABYMOD
        LABYMOD -> MINECRAFTCAPES
        MINECRAFTCAPES -> MINECRAFT
    }

    fun getURL(player: PlayerEntity): String? {
        val config = AutoConfig.getConfigHolder(CapeConfig::class.java).config
        return when (this) {
            OPTIFINE -> if(config.enableOptifine) "http://s.optifine.net/capes/${player.entityName}.png" else null
            LABYMOD -> if(config.enableLabyMod) "https://www.labymod.net/page/php/getCapeTexture.php?uuid=${player.uuidAsString}" else null
            MINECRAFTCAPES -> if(config.enableMinecraftCapesMod) "https://minecraftcapes.net/profile/${player.uuidAsString.replace("-", "")}/cape" else null
            MINECRAFT -> null
        }
    }

    fun getText(): TranslatableText {
        return TranslatableText("options.capes.capetype", stylized)
    }

}