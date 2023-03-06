package me.cael.capes.fabric

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object FabricCapes : ClientModInitializer {

    override fun onInitializeClient() {
        Capes.CONFIG
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("capes")
                    .then(literal("debug")
                        .then(argument("target", string())
                            .executes { context ->
                                val target = context.source.world.players.firstOrNull {
                                    it.gameProfile.name == getString(
                                        context,
                                        "target"
                                    )
                                } ?: throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create()
                                val debugInfo = getDebugInfoForPlayer(target.gameProfile)
                                context.source.player.sendMessage(debugInfo)
                                return@executes 1
                            }
                        )
                        .executes { context ->
                            val debugInfo = getDebugInfoForPlayer(context.source.player.gameProfile)
                            context.source.player.sendMessage(debugInfo)
                            return@executes 1
                        }
                    )
            )
        }
    }

    private fun getDebugInfoForPlayer(profile: GameProfile) : Text {
        val handler = PlayerHandler.fromProfile(profile)

        val infoText = Text.empty()
            .append("Name: ${profile.name}\n")
            .append("UUID: ${profile.id}\n")
            .append("Type: ${handler.capeType}\n")
            .append("IsAnimated: ${handler.hasAnimatedCape}\n")
            .append("HasElytraTexture: ${handler.hasElytraTexture}\n")
            .append("URL: ${handler.capeType?.getURL(profile)}")

        val text = Text.literal("Click here to copy debug info for player ${profile.name}.")
        val clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, infoText.string)
        val hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, infoText)
        val style = Style.EMPTY
            .withClickEvent(clickEvent)
            .withHoverEvent(hoverEvent)
            .withColor(Formatting.BLUE)
            .withUnderline(true)
        text.style = style

        return text
    }

}