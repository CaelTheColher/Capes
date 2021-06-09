package me.cael.capes.compatibility

import dev.emi.trinkets.api.TrinketsApi
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items

object TrinketsCompatibility {

    fun displayElytra(player: PlayerEntity): Boolean {
        if (isTrinketsLoaded()) {
            val component = TrinketsApi.getTrinketComponent(player)
            return component.isPresent && component.get().isEquipped(Items.ELYTRA)
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