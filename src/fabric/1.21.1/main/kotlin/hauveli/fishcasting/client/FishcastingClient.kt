package hauveli.fishcasting.client

// import hauveli.fishcasting.features.fish.CursedRenderer
import at.petrak.hexcasting.common.lib.HexAttributes
import com.li64.tide.client.TideItemModelProperties
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingClientConfig
import hauveli.fishcasting.config.FishcastingConfigs
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
import hauveli.fishcasting.features.gacha.GachaShardEntity
import hauveli.fishcasting.features.gacha.GachaShardRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack


object FabricFishcastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        FishcastingClient.init()

        registerLayerDefinitions()
        registerEntityRenderers()

        // Hmm
        if (!FishcastingConfigs.CLIENT_CONFIG.hexxy5KiltSableBugFix.get()
            && FabricLoader.getInstance().isModLoaded("kilt")
            && FabricLoader.getInstance().isModLoaded("sable")) {
            Fishcasting.LOGGER.info("Detected kilt+sable, setting bugfix to true in client config.")
            FishcastingClientConfig.hexxy5KiltSableBugFixGate = true
            FishcastingConfigs.CLIENT_CONFIG.hexxy5KiltSableBugFix.validateAndSet(true)
        }
    }

    fun registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(
            GoopModel.LAYER_LOCATION,
            { GoopModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            EdifiedFishModel.LAYER_LOCATION,
            { EdifiedFishModel.createBodyLayer() }
        )
        EntityModelLayerRegistry.registerModelLayer(
            PerhosgataModel.LAYER_LOCATION,
            { PerhosgataModel.createBodyLayer() }
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
            FishcastingEntities.GOOP.value,
            ::GoopRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.EDIFIED_FISH.value,
            ::EdifiedFishRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.CURSED.value,
            ::CursedRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.PERHOSGATA.value,
            ::PerhosgataRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.BLESSED.value,
            ::BlessedRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.TACKLEBOX_CHAIR.value,
            ::TackleBoxChairRenderer
        )

        EntityRendererRegistry.register(
            FishcastingEntities.BOTTLE_SHARD.value,
            ::GachaShardRenderer
        )
        /*
        EntityRendererRegistry.register(
            FishcastingEntities.TACKLEBOX_CHAIR_SECRET.value,
            ::TackleBoxChairSecretRenderer
        )

         */
    }

}