package hauveli.fishcasting.client

import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.chair.secret.TackleBoxChairSecretModel
import hauveli.fishcasting.features.fish.cursed.CursedModel
import hauveli.fishcasting.features.fish.cursed.CursedRenderer
import hauveli.fishcasting.features.fish.edified.EdifiedFishModel
import hauveli.fishcasting.features.fish.edified.EdifiedFishRenderer
import hauveli.fishcasting.features.fish.goop.GoopModel
import hauveli.fishcasting.features.fish.goop.GoopRenderer
import hauveli.fishcasting.features.fish.perhosgata.PerhosgataModel
import hauveli.fishcasting.features.fish.perhosgata.PerhosgataRenderer
import hauveli.fishcasting.features.gacha.GachaShardRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.FishcastingEntities
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent

@EventBusSubscriber(
    modid = Fishcasting.MODID,
    value = [Dist.CLIENT]
)
object NeoForgeFishcastingClient {
    fun init(event: FMLClientSetupEvent) {
        FishcastingClient.init()
    }

    @SubscribeEvent
    @JvmStatic
    fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(
            GoopModel.LAYER_LOCATION,
            { GoopModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            EdifiedFishModel.LAYER_LOCATION,
            { EdifiedFishModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            PerhosgataModel.LAYER_LOCATION,
            { PerhosgataModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            CursedModel.LAYER_LOCATION,
            { CursedModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            BlessedModel.LAYER_LOCATION,
            { BlessedModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            TackleBoxChairModel.LAYER_LOCATION,
            { TackleBoxChairModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            TackleBoxChairSecretModel.LAYER_LOCATION,
            { TackleBoxChairSecretModel.createBodyLayer() }
        )
    }

    @SubscribeEvent
    @JvmStatic
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(
            FishcastingEntities.GOOP.value,
            ::GoopRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.EDIFIED_FISH.value,
            ::EdifiedFishRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.CURSED.value,
            ::CursedRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.PERHOSGATA.value,
            ::PerhosgataRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.BLESSED.value,
            ::BlessedRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.TACKLEBOX_CHAIR.value,
            ::TackleBoxChairRenderer
        )

        event.registerEntityRenderer(
            FishcastingEntities.BOTTLE_SHARD.value,
            ::GachaShardRenderer
        )
    }
}