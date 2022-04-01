package me.cael.capes

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

@Config(name = "capes")
class CapeConfig : ConfigData {
    var clientCapeType = CapeType.MINECRAFT
    var enableOptifine = true
    var enableLabyMod = false
    var enableWynntils = false
    var enableMinecraftCapesMod = false
    var enableElytraTexture = true
}