package hauveli.fishcasting

import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.client.TideItemModelProperties
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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.client.model.FabricModelPredicateProviderRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
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
        registerAttributeHolder()
        registerItemModelProperties()
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

    fun registerAttributeHolder() {
        ServerLifecycleEvents.SERVER_STARTED.register { server: MinecraftServer ->
            FishcastingAttributes.registerHolder(server.allLevels.first())
        }
    }

    // why am I not moving some of these to common? this one seems like it would be fine there...
    // it's also a client only thing, so maybe I should really be moving it to client...
    fun registerItemModelProperties() {
        ItemProperties.register(
            FishcastingItems.SHEPHERDS_CASTING_ROD,
            TideItemModelProperties.CAST_PROPERTY,
            TideItemModelProperties.CAST_FUNCTION
        )
    }

    /*
        todo: what the fuck do I do to get capabilities in fabric? I can only think of registering a new entity, but I'd rather not...
        modBus.apply {
            addListener(::registerCaps)
        }

    fun registerCaps(event: RegisterCapabilitiesEvent) {
        event.registerEntity<ADIotaHolder?, Void?, TideFishingHook>(
            HexCapabilities.Entity.IOTA,
            TideEntityTypes.FISHING_BOBBER,
            ICapabilityProvider { entity: TideFishingHook, ctx: Void? -> ToTideFishingHookEntity(entity) }
        )
    }



    // I decided against it but I'm keeping it here just in case
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

     */
}
