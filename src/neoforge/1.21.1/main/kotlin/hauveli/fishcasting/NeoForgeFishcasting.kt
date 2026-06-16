package hauveli.fishcasting

//import hauveli.fishcasting.common.registries.FishcastingEntities
import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.forge.cap.HexCapabilities
import com.li64.tide.client.TideItemModelProperties
import com.li64.tide.registries.TideEntityTypes
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.client.NeoForgeFishcastingClient
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.CursedModel
import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingItems.ToTideFishingHookEntity
import hauveli.fishcasting.datagen.NeoForgeFishcastingDatagen
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.casting.recipe.brainsweep.FishcastingBrainsweepeeIngredients
import hauveli.fishcasting.registry.FishcastingAttributes
import hauveli.fishcasting.registry.FishcastingCreativeTabs
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingSounds
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.BiConsumer
import java.util.function.Consumer


@Mod(Fishcasting.MODID)
class NeoForgeFishcasting(modBus: IEventBus, container: ModContainer) {


    fun fuckingUnfreeze() {
        val rootRegistry = BuiltInRegistries.REGISTRY as? MappedRegistry<*>
            ?: return

        rootRegistry.unfreeze()
    }

    fun fuckingFreeze() {
        val rootRegistry = BuiltInRegistries.REGISTRY as? MappedRegistry<*>
            ?: return

        rootRegistry.freeze()
    }

    init {
        // thank you
        // https://github.com/VazkiiMods/Botania/blob/1.18.x/Forge/src/main/java/vazkii/botania/forge/ForgeCommonInitializer.java
        fun <T> bind(
            registry: ResourceKey<out Registry<T>>,
            source: Consumer<BiConsumer<T, ResourceLocation>>
        ) {
            modBus.addListener({ event: RegisterEvent ->
                if (registry == event.registryKey) {
                    source.accept(BiConsumer { t: T, rl: ResourceLocation ->
                        event.register<T>(
                            registry,
                            rl
                        ) { t }
                    })
                }
            })
        }


        bind(Registries.ENTITY_TYPE, FishcastingEntities::registerEntities)
        bind(Registries.SOUND_EVENT, FishcastingSounds::registerSounds)
        bind(Registries.ATTRIBUTE, FishcastingAttributes::registerAttributes)
        bind(Registries.ITEM, FishcastingItems::registerItems)
        bind(Registries.CREATIVE_MODE_TAB, FishcastingCreativeTabs::registerCreativeTabs)
        //bind(HexRegistries.IOTA_TYPE, FishcastingIotaTypes::registerTypes)
        bind(HexRegistries.BRAINSWEEPEE_INGREDIENT, FishcastingBrainsweepeeIngredients::register)
        Registry.register(HexArithmetics.REGISTRY, Fishcasting.id("patterns"), FishcastingFishArithmetic())
        /*


         */

        /*
        // I don't know how much I have to do to register an attribute but I don't like what I've done
        modBus.addListener<EntityAttributeModificationEvent?>(Consumer { event: EntityAttributeModificationEvent? ->
            event!!.add(EntityType.PLAYER, BOBBER_RADIUS as Holder<Attribute?>)
        })
        */
        modBus.apply {
            addListener(NeoForgeFishcastingClient::init)
            addListener(NeoForgeFishcastingDatagen::init)
            addListener(NeoForgeFishcastingServer::init)
            //addListener(::registerAttributeHolder)
            addListener(::registerEntityAttributes)
            addListener(::registerItemModelProperties)
            addListener(::registerLayerDefinitions)
            addListener(::registerEntityRenderers)
            addListener(::registerCaps)
            addListener(::registerCreativeModeTabItems)
        }

        Fishcasting.init()
    }

    // why?
    @SubscribeEvent
    fun registerAttributeHolder(event: ServerStartedEvent) {
        FishcastingAttributes.registerHolder(event.server.allLevels.first())
    }

    // ugh I couldn't do anything better than this
    fun registerEntityAttributes(event: EntityAttributeCreationEvent) {
        event.put(
            FishcastingEntities.CURSED,
            Axolotl.createAttributes().build()
        )
        event.put(
            FishcastingEntities.BLESSED,
            Villager.createAttributes().build()
        )
    }


    fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(
            TackleBoxChairModel.LAYER_LOCATION,
            { TackleBoxChairModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            CursedModel.LAYER_LOCATION,
            { CursedModel.createBodyLayer() }
        )
        event.registerLayerDefinition(
            BlessedModel.LAYER_LOCATION,
            { BlessedModel.createBodyLayer() }
        )
    }

    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(
            FishcastingEntities.TACKLEBOX_CHAIR,
            ::TackleBoxChairRenderer
        )
        event.registerEntityRenderer(
            FishcastingEntities.CURSED,
            ::CursedRenderer
        )
        event.registerEntityRenderer(
            FishcastingEntities.BLESSED,
            ::BlessedRenderer
        )
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

    fun registerCreativeModeTabItems(event: BuildCreativeModeTabContentsEvent) {
        FishcastingItems.registerItemCreativeTab(event, event.tab);
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
}
