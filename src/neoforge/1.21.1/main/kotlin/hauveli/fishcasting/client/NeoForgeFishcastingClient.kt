package hauveli.fishcasting.client

import hauveli.fishcasting.client.FishcastingClient
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT

object NeoForgeFishcastingClient {
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLClientSetupEvent) {
        FishcastingClient.init()
    }
}