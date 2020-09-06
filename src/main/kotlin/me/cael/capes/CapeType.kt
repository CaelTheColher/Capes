package me.cael.capes

import net.minecraft.text.TranslatableText

enum class CapeType(val stylized: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), MINECRAFTCAPES("MinecraftCapes");

    fun cycle() = when(this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> MINECRAFTCAPES
        MINECRAFTCAPES -> MINECRAFT
    }

    fun getText(): TranslatableText {
        return TranslatableText("options.capes.capetype", stylized)
    }

}