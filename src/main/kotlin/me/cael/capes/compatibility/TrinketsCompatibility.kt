package me.cael.capes.compatibility

import dev.emi.trinkets.api.TrinketsApi
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items

object TrinketsCompatibility {

    fun displayElytra(player: PlayerEntity): Boolean {
        if (isTrinketsLoaded()) {
            val inventory =
                TrinketsApi.TRINKET_COMPONENT.get(player).inventory
            return inventory.count(Items.ELYTRA) > 0
        }
        return false
    }

    private fun isTrinketsLoaded(): Boolean = try {
            Class.forName("dev.emi.trinkets.TrinketsMain")
            true
        } catch (classNotFoundException: ClassNotFoundException) {
            false
        }
}