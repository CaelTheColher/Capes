package me.cael.capes

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

enum class CapeType(val stylized: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), LABYMOD("LabyMod"), WYNNTILS("Wynntils"), MINECRAFTCAPES("MinecraftCapes"), COSMETICA("Cosmetica"), CLOAKSPLUS("Cloaks+");

    fun cycle() = when (this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> LABYMOD
        LABYMOD -> WYNNTILS
        WYNNTILS -> COSMETICA
        COSMETICA -> MINECRAFTCAPES
        MINECRAFTCAPES -> CLOAKSPLUS
        CLOAKSPLUS -> MINECRAFT
    }

    fun getURL(profile: GameProfile): String? {
        if (!isEnabled(profile)) return null
        return when (this) {
            OPTIFINE -> "http://s.optifine.net/capes/${profile.name}.png"
            LABYMOD -> "https://dl.labymod.net/capes/${profile.id}"
            WYNNTILS -> "https://athena.wynntils.com/user/getInfo"
            COSMETICA -> "https://api.cosmetica.cc/get/cloak?username=${profile.name}&uuid=${profile.id}&nothirdparty"
            MINECRAFTCAPES -> "https://api.minecraftcapes.net/profile/${profile.id.toString().replace("-", "")}"
            CLOAKSPLUS -> "http://161.35.130.99/capes/${profile.name}.png"
            MINECRAFT -> null
        }
    }

    fun isEnabled(profile: GameProfile): Boolean {
        return if (MinecraftClient.getInstance().uuidEquals(profile.id)) {
            Capes.CONFIG.clientCapeType === this
        } else return when (this) {
            MINECRAFT -> false
            LABYMOD -> Capes.CONFIG.enableLabyMod
            OPTIFINE -> Capes.CONFIG.enableOptifine
            WYNNTILS -> Capes.CONFIG.enableWynntils
            COSMETICA -> Capes.CONFIG.enableCosmetica
            CLOAKSPLUS -> Capes.CONFIG.enableCloaksPlus
            MINECRAFTCAPES -> Capes.CONFIG.enableMinecraftCapesMod
        }
    }

    fun getToggleText(enabled: Boolean): Text = ScreenTexts.composeToggleText(Text.of(stylized), enabled)

    fun getText(): Text = Text.translatable("options.capes.capetype", stylized)

}
