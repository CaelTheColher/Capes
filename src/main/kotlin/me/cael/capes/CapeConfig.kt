package me.cael.capes

import draylar.omegaconfig.api.Config

class CapeConfig : Config {
    var clientCapeType = CapeType.MINECRAFT
    var enableOptifine = true
    var enableLabyMod = false
    var enableWynntils = false
    var enableMinecraftCapesMod = false
    var enableCosmetica = false
    var enableElytraTexture = true

    override fun getName(): String = "capes"
    override fun getExtension(): String = "json5"
}