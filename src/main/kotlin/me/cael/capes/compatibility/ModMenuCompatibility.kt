package me.cael.capes.compatibility

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.cael.capes.CapeMenu
import net.minecraft.client.MinecraftClient

class ModMenuCompatibility : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        CapeMenu(it, MinecraftClient.getInstance().options)
    }
}