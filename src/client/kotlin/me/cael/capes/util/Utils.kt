package me.cael.capes.util

import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import java.nio.charset.StandardCharsets
import java.util.*

fun GameProfile.isValidProfile(): Boolean {
    val profileName = runCatching {
        val textures = this.properties.get("textures").stream().findFirst().get().value
        val json = String(Base64.getDecoder().decode(textures), StandardCharsets.UTF_8)
        JsonParser.parseString(json).asJsonObject.get("profileName").asString
    }.getOrNull() ?: return false

    return this.id.version() == 4 || (this.id.version() == 2 && this.name == profileName)
}