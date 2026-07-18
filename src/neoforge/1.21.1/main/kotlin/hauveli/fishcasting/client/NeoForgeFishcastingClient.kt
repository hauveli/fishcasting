package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingItems.SHEPHERDS_CASTING_ROD
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent


object NeoForgeFishcastingClient {
    fun init(event: FMLClientSetupEvent) {
        FishcastingClient.init()
        // what the fuck neoforge
        ItemProperties.register(
            SHEPHERDS_CASTING_ROD.value,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION as ItemPropertyFunction
        )
    }

    @SubscribeEvent
    fun registerEntityLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
    }
}