package hauveli.fishcasting

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.client.TideItemModelProperties
import com.li64.tide.registries.TideEntityTypes
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.CursedModel
import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.*
import hauveli.fishcasting.registry.FishcastingCreativeTabs.key
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.BiConsumer


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

        FishcastingRecipeRegistry.registerSerializers(bind(BuiltInRegistries.RECIPE_SERIALIZER))
        FishcastingRecipeRegistry.registerTypes(bind(BuiltInRegistries.RECIPE_TYPE))
        FishcastingEntities.registerEntities(bind(BuiltInRegistries.ENTITY_TYPE))
        FishcastingAttributes.registerAttributes(bind(BuiltInRegistries.ATTRIBUTE))
        FishcastingSounds.registerSounds(bind(BuiltInRegistries.SOUND_EVENT))
        FishcastingCreativeTabs.registerCreativeTabs(bind(BuiltInRegistries.CREATIVE_MODE_TAB))
        //bind(HexRegistries.IOTA_TYPE, FishcastingIotaTypes::registerTypes)
        FishcastingBrainsweepeeIngredients.registerBrainsweepeeIngredients(bind(IXplatAbstractions.INSTANCE.brainsweepeeIngredientRegistry))
        //bind(HexRegistries.BRAINSWEEPEE_INGREDIENT, FishcastingBrainsweepeeIngredients::registerBrainsweepeeIngredients)
        //Registry.register(HexRegistries.BRAINSWEEPEE_INGREDIENT, Fishcasting.id(""), FishcastingBrainsweepeeIngredients)
        Registry.register(HexArithmetics.REGISTRY, Fishcasting.id("patterns"), FishcastingFishArithmetic())

        registerOnJoinEvent()
        registerCreativeModeTabItems()
        registerEntityAttributes()
        registerLayerDefinitions()
        registerEntityRenderers()
        registerAttributeHolder()
        // why is this ok in fabric but not neoforge? what...
        //registerItemModelProperties()
    }

    fun registerOnJoinEvent() {
        ServerPlayConnectionEvents.JOIN.register { impl, sender, server ->
            FishcastingAdvancements.onJoinAdvancementUpdate(impl.player)
        }
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

/*

    // I decided against it but I'm keeping it here just in case
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

     */
}
