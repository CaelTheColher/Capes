package me.cael.capes

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer


object Capes : ClientModInitializer {

    override fun onInitializeClient() {
        AutoConfig.register(CapeConfig::class.java, ::JanksonConfigSerializer)
    }
}