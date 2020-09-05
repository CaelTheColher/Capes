package me.cael.capes

enum class CapeType(name: String) {
    MINECRAFT("Minecraft"), OPTIFINE("OptiFine"), MINECRAFTCAPES("MinecraftCapes Mod"), DEBUG("Debug");

    fun cycle() = when(this) {
        MINECRAFT -> OPTIFINE
        OPTIFINE -> MINECRAFTCAPES
        MINECRAFTCAPES -> DEBUG
        DEBUG -> MINECRAFT
    }

}