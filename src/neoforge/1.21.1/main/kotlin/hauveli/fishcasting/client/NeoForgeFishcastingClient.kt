package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.client.renderer.item.ItemProperties
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent


object NeoForgeFishcastingClient {
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLClientSetupEvent) {
        event.enqueueWork(::registerItemModelProperties);
        FishcastingClient.init()
    }


    // why does it not work after I build it...
    @JvmStatic
    fun registerItemModelProperties() {
        ItemProperties.register(
            FishcastingItems.SHEPHERDS_CASTING_ROD,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION
        )
    }

}