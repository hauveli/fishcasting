package hauveli.fishcasting.client

import hauveli.fishcasting.client.FishcastingClient
import net.fabricmc.api.ClientModInitializer

object FabricFishcastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        FishcastingClient.init()
    }
}