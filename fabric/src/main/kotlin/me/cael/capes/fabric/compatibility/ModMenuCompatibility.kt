package me.cael.capes.fabric.compatibility

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.cael.capes.menu.SelectorMenu
import net.minecraft.client.MinecraftClient

class ModMenuCompatibility : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        SelectorMenu(it, MinecraftClient.getInstance().options)
    }
}