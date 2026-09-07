package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.client.FishcastingClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.item.ItemProperties

object FabricFishcastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        FishcastingClient.init()
    }

}