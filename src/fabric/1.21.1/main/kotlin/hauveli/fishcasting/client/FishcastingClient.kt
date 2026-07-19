package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.client.FishcastingClient
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.CursedModel
import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems.SHEPHERDS_CASTING_ROD
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.item.ItemProperties

object FabricFishcastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        FishcastingClient.init()
        ItemProperties.register(
            SHEPHERDS_CASTING_ROD.value,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION
        )
        registerLayerDefinitions()
        registerEntityRenderers()
    }



    fun registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(
            TackleBoxChairModel.LAYER_LOCATION,
            { TackleBoxChairModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            CursedModel.LAYER_LOCATION,
            { CursedModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            BlessedModel.LAYER_LOCATION,
            { BlessedModel.createBodyLayer() }
        )
    }

    fun registerEntityRenderers() {
        EntityRendererRegistry.register(
            FishcastingEntities.TACKLEBOX_CHAIR,
            ::TackleBoxChairRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.CURSED,
            ::CursedRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.BLESSED,
            ::BlessedRenderer
        )
    }

}