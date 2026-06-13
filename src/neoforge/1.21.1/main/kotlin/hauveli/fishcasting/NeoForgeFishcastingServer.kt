package hauveli.fishcasting

import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent

object NeoForgeFishcastingServer {
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLDedicatedServerSetupEvent) {
        Fishcasting.initServer()
    }
}

