package hauveli.fishcasting

import hauveli.fishcasting.client.NeoForgeFishcastingClient
import hauveli.fishcasting.datagen.NeoForgeFishcastingDatagen
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod

@Mod(Fishcasting.MODID)
class NeoForgeFishcasting(modBus: IEventBus, container: ModContainer) {
    init {
        modBus.apply {
            addListener(NeoForgeFishcastingClient::init)
            addListener(NeoForgeFishcastingDatagen::init)
            addListener(NeoForgeFishcastingServer::init)
        }
        Fishcasting.init()
    }

    companion object {
        internal val container: ModContainer
            get() = ModList.get().getModContainerById(Fishcasting.MODID).get()
    }
}
