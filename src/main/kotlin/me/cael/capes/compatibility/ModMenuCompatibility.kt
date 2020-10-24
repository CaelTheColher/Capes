package me.cael.capes.compatibility

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.cael.capes.CapeMenu
import net.minecraft.client.MinecraftClient

class ModMenuCompatibility : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        CapeMenu(it, MinecraftClient.getInstance().options)
    }
}