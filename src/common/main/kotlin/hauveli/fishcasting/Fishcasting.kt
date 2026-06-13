package hauveli.fishcasting

import hauveli.fishcasting.config.FishcastingConfigs
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import hauveli.fishcasting.networking.FishcastingNetworking
import hauveli.fishcasting.registry.FishcastingActions

object Fishcasting {
    const val MODID = "fishcasting"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)



    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)

    fun init() {
        initRegistries(
            FishcastingActions,
        )
        FishcastingNetworking.init()
        FishcastingConfigs.init()
    }

    fun initServer() {
    }
}
