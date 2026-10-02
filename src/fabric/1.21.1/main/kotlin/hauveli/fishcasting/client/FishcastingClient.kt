package hauveli.fishcasting.client

// import hauveli.fishcasting.features.fish.CursedRenderer
import at.petrak.hexcasting.common.lib.HexAttributes
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
import hauveli.fishcasting.features.fish.perhosgata.PerhosgataModel
import hauveli.fishcasting.features.fish.perhosgata.PerhosgataRenderer
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

        registerRodWithCastProperty(FishcastingItems.SHEPHERDS_CASTING_ROD.value)
        registerRodWithCastProperty(FishcastingItems.ROCKY_CASTING_ROD.value)
        registerItemPropertyForScryingVision(FishcastingItems.PERHOSGATA.value)
        registerItemPropertyForScryingVision(FishcastingItems.PERHOSGATA_BUCKET.value)

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

    fun registerRodWithCastProperty(item: Item) {
        ItemProperties.register(
            item,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION
        )
    }

    fun registerItemPropertyForScryingVision(item: Item) {
        ItemProperties.register(
            item,
            Fishcasting.id("no_scry_sight"), // todo: put this somewhere...
            {
                stack: ItemStack?, level: ClientLevel?, entity: LivingEntity?, seed: Int ->
                val player = Minecraft.getInstance().player // thank god this is on the client side of things
                if (player == null) {
                    1f
                } else {
                    if (player.getAttributeValue(HexAttributes.SCRY_SIGHT) > 0.0
                        && player.getAttributeValue(HexAttributes.FEEBLE_MIND) <= 0.0)
                        0f
                    else
                        1f
                }
            }
        )
    }

    fun registerLayerDefinitions() {
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

        /*
        EntityRendererRegistry.register(
            FishcastingEntities.TACKLEBOX_CHAIR_SECRET.value,
            ::TackleBoxChairSecretRenderer
        )

         */
    }

}