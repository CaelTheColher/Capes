package me.cael.capes

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.File
import java.io.PrintWriter

class CapeConfig {
    var clientCapeType = CapeType.MINECRAFT
    var enableOptifine = true
    var enableLabyMod = false
    var enableWynntils = false
    var enableMinecraftCapesMod = false
    var enableCosmetica = false
    var enableCloaksPlus = false
    var enableElytraTexture = true

    fun save() {
        val parser = JsonParser()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val configFile = File("${Platform.getConfigDirectory()}${File.separator}capes.json5")
        val json: String = gson.toJson(parser.parse(gson.toJson(this)))
        PrintWriter(configFile).use { out -> out.println(json) }
    }

}