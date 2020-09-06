package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer


object Capes : ClientModInitializer {

    override fun onInitializeClient() {
        AutoConfig.register(CapeConfig::class.java, ::JanksonConfigSerializer)
    }
}