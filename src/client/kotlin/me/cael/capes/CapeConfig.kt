package me.cael.capes

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.PrintWriter

class CapeConfig {
    var clientCapeType = CapeType.MINECRAFT
    var enableOptifine = true
    var enableLabyMod = false
    var enableMinecraftCapesMod = false
    var enableCosmetica = false
    var enableCloaksPlus = false
    var enableNorisk = false
    var enableElytraTexture = true

    fun save() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val configFile = File("${FabricLoader.getInstance().configDir}${File.separator}capes.json5")
        val json: String = gson.toJson(JsonParser.parseString(gson.toJson(this)))
        PrintWriter(configFile).use { out -> out.println(json) }
    }

}