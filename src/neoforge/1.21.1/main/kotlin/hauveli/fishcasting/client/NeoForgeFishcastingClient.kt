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
        registerItemModelProperties() // I couldn't figure out how to make this not cause an error I was unable to understand
        FishcastingClient.init()
    }

    // why does it not work after I build it...
    fun registerItemModelProperties() {
        ItemProperties.register(
            SHEPHERDS_CASTING_ROD,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION as ItemPropertyFunction
        )
    }

    @SubscribeEvent
    fun registerEntityLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
    }
}