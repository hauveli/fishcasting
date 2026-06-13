package hauveli.fishcasting

import net.fabricmc.api.DedicatedServerModInitializer

object FabricFishcastingServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        Fishcasting.initServer()
    }
}
