package me.cael.capes

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files

object Capes {

    const val MOD_ID = "capes"

    val LOGGER = LoggerFactory.getLogger("Capes")

    val CONFIG: CapeConfig by lazy {
        val parser = JsonParser()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val configFile = File("${Platform.getConfigDirectory()}${File.separator}capes.json5")
        var finalConfig: CapeConfig
        LOGGER.info("Trying to read config file...")
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...")
                val json: String = gson.toJson(parser.parse(gson.toJson(CapeConfig())))
                PrintWriter(configFile).use { out -> out.println(json) }
                finalConfig = CapeConfig()
                LOGGER.info("Successfully created default config file.")
            } else {
                LOGGER.info("A config file was found, loading it..")
                finalConfig = gson.fromJson(String(Files.readAllBytes(configFile.toPath())), CapeConfig::class.java)
                if (finalConfig == null) {
                    throw NullPointerException("The config file was empty.")
                } else {
                    LOGGER.info("Successfully loaded config file.")
                }
            }
        } catch (exception: Exception) {
            LOGGER.error("There was an error creating/loading the config file!", exception)
            finalConfig = CapeConfig()
            LOGGER.warn("Defaulting to original config.")
        }
        finalConfig
    }

    fun identifier(id: String) = Identifier(MOD_ID, id)

}