package hauveli.fishcasting.client

import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingConfigs
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.chair.secret.TackleBoxChairSecretModel
import hauveli.fishcasting.features.fish.cursed.CursedModel
import hauveli.fishcasting.features.fish.cursed.CursedRenderer
import hauveli.fishcasting.features.fish.edified.EdifiedFishModel
import hauveli.fishcasting.features.fish.edified.EdifiedFishRenderer
// import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems.SHEPHERDS_CASTING_ROD
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.loader.api.FabricLoader
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

        // Hmm
        if (!FishcastingConfigs.CLIENT_CONFIG.hexxy5KiltSableBugFix.get()
            && FabricLoader.getInstance().isModLoaded("kilt")
            && FabricLoader.getInstance().isModLoaded("sable")) {
            Fishcasting.LOGGER.info("Detected kilt+sable, setting bugfix to true in client config.")
            FishcastingConfigs.CLIENT_CONFIG.hexxy5KiltSableBugFix.validateAndSet(true)
        }
    }



    fun registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(
            EdifiedFishModel.LAYER_LOCATION,
            { EdifiedFishModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            CursedModel.LAYER_LOCATION,
            { CursedModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            BlessedModel.LAYER_LOCATION,
            { BlessedModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            TackleBoxChairModel.LAYER_LOCATION,
            { TackleBoxChairModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            TackleBoxChairSecretModel.LAYER_LOCATION,
            { TackleBoxChairSecretModel.createBodyLayer() }
        )
    }

    fun registerEntityRenderers() {

        EntityRendererRegistry.register(
            FishcastingEntities.EDIFIED_FISH.value,
            ::EdifiedFishRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.CURSED.value,
            ::CursedRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.BLESSED.value,
            ::BlessedRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.TACKLEBOX_CHAIR.value,
            ::TackleBoxChairRenderer
        )
    }

}