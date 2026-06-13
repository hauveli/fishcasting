package hauveli.fishcasting

import net.fabricmc.api.ModInitializer

object FabricFishcasting : ModInitializer {
    override fun onInitialize() {
        Fishcasting.init()
    }
}
