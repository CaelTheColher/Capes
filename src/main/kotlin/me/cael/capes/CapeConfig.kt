package me.cael.capes

import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config

@Config(name = "capes")
class CapeConfig : ConfigData {
    var clientCapeType = CapeType.MINECRAFT
    var glint = false
}