package me.cael.capes

import draylar.omegaconfig.OmegaConfig
import net.fabricmc.api.ClientModInitializer


object Capes : ClientModInitializer {

    val CONFIG = OmegaConfig.register(CapeConfig::class.java)

    override fun onInitializeClient() {}
}