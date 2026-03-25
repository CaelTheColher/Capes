package me.cael.capes

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import me.cael.capes.handler.PlayerHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files

object Capes : ClientModInitializer {

	const val MOD_ID = "capes"

	val LOGGER = LoggerFactory.getLogger("Capes")

	val CONFIG: CapeConfig by lazy {
		val gson = GsonBuilder().setPrettyPrinting().create()
		val configFile = File("${FabricLoader.getInstance().configDir}${File.separator}capes.json5")
		var finalConfig: CapeConfig
		LOGGER.info("Trying to read config file...")
		try {
			if (configFile.createNewFile()) {
				LOGGER.info("No config file found, creating a new one...")
				val json: String = gson.toJson(JsonParser.parseString(gson.toJson(CapeConfig())))
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
		@Suppress("SENSELESS_COMPARISON")
		if (finalConfig.clientCapeType == null) finalConfig.clientCapeType = CapeType.MINECRAFT
		finalConfig
	}

	fun identifier(id: String) = Identifier.fromNamespaceAndPath(MOD_ID, id)

	override fun onInitializeClient() {
		CONFIG
		ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
			dispatcher.register(
				literal("capes")
					.then(literal("debug")
						.then(argument("target", string())
							.executes { context ->
								val target = context.source.level.players().firstOrNull {
									it.gameProfile.name == getString(
										context,
										"target"
									)
								} ?: throw EntityArgument.NO_PLAYERS_FOUND.create()
								val debugInfo = getDebugInfoForPlayer(target.gameProfile)
								context.source.player.sendSystemMessage(debugInfo)
								return@executes 1
							}
						)
						.executes { context ->
							val debugInfo = getDebugInfoForPlayer(context.source.player.gameProfile)
							context.source.player.sendSystemMessage(debugInfo)
							return@executes 1
						}
					)
			)
		}
	}

	private fun getDebugInfoForPlayer(profile: GameProfile) : Component {
		val handler = PlayerHandler.fromProfile(profile)

		val infoText = Component.empty()
			.append("Name: ${profile.name}\n")
			.append("UUID: ${profile.id}\n")
			.append("Type: ${handler.capeType}\n")
			.append("IsAnimated: ${handler.hasAnimatedCape}\n")
			.append("HasElytraTexture: ${handler.hasElytraTexture}\n")
			.append("URL: ${handler.capeType?.getURL(profile)}")

		val text = Component.literal("Click here to copy debug info for player ${profile.name}.")
		val clickEvent = ClickEvent.CopyToClipboard(infoText.string)
		val hoverEvent = HoverEvent.ShowText(infoText)
		val style = Style.EMPTY
			.withClickEvent(clickEvent)
			.withHoverEvent(hoverEvent)
			.withColor(ChatFormatting.BLUE)
			.withUnderlined(true)
		text.style = style

		return text
	}

}