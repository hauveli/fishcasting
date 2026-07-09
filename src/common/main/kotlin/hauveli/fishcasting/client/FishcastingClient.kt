package hauveli.fishcasting.client

import hauveli.fishcasting.registry.FishcastingItems
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screens.Screen

object FishcastingClient {
    fun init() {
        FishcastingItems.registerItemModelProperties()
    }
}