package hauveli.fishcasting

import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.xplat.IXplatAbstractions
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.CursedModel
import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.FishcastingAttributes
import hauveli.fishcasting.registry.FishcastingAttributes.registerAttributes
import hauveli.fishcasting.registry.FishcastingBrainsweepeeIngredients
import hauveli.fishcasting.registry.FishcastingBrainsweepeeIngredients.registerBrainsweepeeIngredients
import hauveli.fishcasting.registry.FishcastingCreativeTabs
import hauveli.fishcasting.registry.FishcastingCreativeTabs.key
import hauveli.fishcasting.registry.FishcastingCreativeTabs.registerCreativeTabs
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingEntities.registerEntities
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingItems.registerItems
import hauveli.fishcasting.registry.FishcastingSounds
import hauveli.fishcasting.registry.FishcastingSounds.registerSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.item.CreativeModeTabs
import java.util.function.BiConsumer
import java.util.function.Consumer

object FabricFishcasting : ModInitializer {
    override fun onInitialize() {
        Fishcasting.init()


    }

    init {
        fun <T> bind(registry: Registry<in T>): BiConsumer<T, ResourceLocation> =
            BiConsumer<T, ResourceLocation> { t, id ->
                if (t != null) {
                    Registry.register(registry, id, t)
                }
            }
        FishcastingEntities.registerEntities(bind(BuiltInRegistries.ENTITY_TYPE))
        FishcastingAttributes.registerAttributes(bind(BuiltInRegistries.ATTRIBUTE))
        FishcastingSounds.registerSounds(bind(BuiltInRegistries.SOUND_EVENT))
        FishcastingItems.registerItems(bind(BuiltInRegistries.ITEM))
        FishcastingCreativeTabs.registerCreativeTabs(bind(BuiltInRegistries.CREATIVE_MODE_TAB))
        //bind(HexRegistries.IOTA_TYPE, FishcastingIotaTypes::registerTypes)
        FishcastingBrainsweepeeIngredients.registerBrainsweepeeIngredients(bind(IXplatAbstractions.INSTANCE.brainsweepeeIngredientRegistry))
        //bind(HexRegistries.BRAINSWEEPEE_INGREDIENT, FishcastingBrainsweepeeIngredients::registerBrainsweepeeIngredients)
        //Registry.register(HexRegistries.BRAINSWEEPEE_INGREDIENT, Fishcasting.id(""), FishcastingBrainsweepeeIngredients)
        Registry.register(HexArithmetics.REGISTRY, Fishcasting.id("patterns"), FishcastingFishArithmetic())

        registerCreativeModeTabItems()
        registerEntityAttributes()
        registerLayerDefinitions()
        registerEntityRenderers()
    }


    fun registerCreativeModeTabItems() {
        ItemGroupEvents.modifyEntriesEvent(FishcastingCreativeTabs.FISHCASTING.key).register { entries ->
            FishcastingItems.registerItemCreativeTab(
                entries,
                FishcastingCreativeTabs.FISHCASTING
            )
        }
    }

    fun registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.CURSED,
            Axolotl.createAttributes().build()
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.BLESSED,
            Villager.createAttributes().build()
        )
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


    /*

        modBus.apply {
            addListener(NeoForgeFishcastingClient::init)
            addListener(NeoForgeFishcastingDatagen::init)
            addListener(NeoForgeFishcastingServer::init)
            //addListener(::registerAttributeHolder)
            addListener(::registerItemModelProperties)
            addListener(::registerLayerDefinitions)
            addListener(::registerEntityRenderers)
            addListener(::registerCaps)
        }

        Fishcasting.init()
    }

    // why?
    @SubscribeEvent
    fun registerAttributeHolder(event: ServerStartedEvent) {
        FishcastingAttributes.registerHolder(event.server.allLevels.first())
    }

    // from the neoforge documentation, found it after looking at the fabric documentation, god I'm just glad it works now
    fun registerItemModelProperties(event: FMLClientSetupEvent) {
        event.enqueueWork( { // I dont understand why the IDE chooses what it does when I convert java to kotlin
            ItemProperties.register(
                FishcastingItems.SHEPHERDS_CASTING_ROD,
                TideItemModelProperties.CAST_PROPERTY,
                TideItemModelProperties.CAST_FUNCTION
            )
        })
    }

    fun registerCaps(event: RegisterCapabilitiesEvent) {
        event.registerEntity<ADIotaHolder?, Void?, TideFishingHook>(
            HexCapabilities.Entity.IOTA,
            TideEntityTypes.FISHING_BOBBER,
            ICapabilityProvider { entity: TideFishingHook, ctx: Void? -> ToTideFishingHookEntity(entity) }
        )
    }



    // I decided against it but I'm keeping it here just in case
    /*
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
     */

    companion object {
        internal val container: ModContainer
            get() = ModList.get().getModContainerById(Fishcasting.MODID).get()
    }
     */
}
