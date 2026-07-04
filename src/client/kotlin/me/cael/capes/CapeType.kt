package me.cael.capes

import com.mojang.authlib.GameProfile
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

enum class CapeType(val stylized: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), LABYMOD("LabyMod"), MINECRAFTCAPES("MinecraftCapes"), COSMETICA("Cosmetica"), CLOAKSPLUS("Cloaks+"), NORISK("Norisk");

    fun cycle() = when(this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> LABYMOD
        LABYMOD -> COSMETICA
        COSMETICA -> MINECRAFTCAPES
        MINECRAFTCAPES -> CLOAKSPLUS
        CLOAKSPLUS -> NORISK
        NORISK -> MINECRAFT
    }

    fun getURL(profile: GameProfile): String? {
        val config = Capes.CONFIG
        return when (this) {
            OPTIFINE -> if(config.enableOptifine) "http://s.optifine.net/capes/${profile.name}.png" else null
            LABYMOD -> if(config.enableLabyMod) "https://dl.labymod.net/capes/${profile.id}" else null
            COSMETICA -> if(config.enableCosmetica) "https://api.cosmetica.cc/users/${profile.id}/cape" else null
            MINECRAFTCAPES -> if(config.enableMinecraftCapesMod) "https://api.minecraftcapes.net/profile/${profile.id.toString().replace("-", "")}" else null
            CLOAKSPLUS -> if(config.enableCloaksPlus) "http://161.35.130.99/capes/${profile.name}.png" else null
            NORISK -> if(config.enableNorisk) "https://api.errexe.xyz/capes/norisk/${profile.id}?image=true" else null
            MINECRAFT -> null
        }
    }

    fun getToggleText(enabled: Boolean): Component = CommonComponents.optionStatus(Component.literal(stylized), enabled)

    fun getText(): Component = Component.translatable("options.capes.capetype", stylized)

}
