package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.client.FishcastingClient
import hauveli.fishcasting.registry.FishcastingItems.SHEPHERDS_CASTING_ROD
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.item.ItemProperties

object FabricFishcastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        FishcastingClient.init()
        ItemProperties.register(
            SHEPHERDS_CASTING_ROD.value,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION
        )
    }
}