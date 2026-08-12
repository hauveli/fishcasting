package hauveli.fishcasting

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER_LOOKUP
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.client.TideItemModelProperties
import com.li64.tide.registries.TideEntityTypes
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairRenderer
import hauveli.fishcasting.features.fish.CursedModel
import hauveli.fishcasting.features.fish.CursedRenderer
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem
import hauveli.fishcasting.features.trader.BlessedModel
import hauveli.fishcasting.features.trader.BlessedRenderer
import hauveli.fishcasting.registry.*
import hauveli.fishcasting.registry.FishcastingCreativeTabs.key
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
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
        registerEntityAttributes() // todo: do something better than this
    }

    init {
        fun <T> bind(registry: Registry<in T>): BiConsumer<T, ResourceLocation> =
            BiConsumer<T, ResourceLocation> { t, id ->
                if (t != null) {
                    Registry.register(registry, id, t)
                }
            }

        FishcastingBrainsweepeeIngredients.registerBrainsweepeeIngredients(bind(IXplatAbstractions.INSTANCE.brainsweepeeIngredientRegistry))
        Registry.register(HexArithmetics.REGISTRY, Fishcasting.id("patterns"), FishcastingFishArithmetic())

        registerCreativeModeTabItems()
        // why is this ok in fabric but not neoforge? what...
        //registerItemModelProperties()
    }

    fun registerCreativeModeTabItems() {
        ItemGroupEvents.modifyEntriesEvent(FishcastingCreativeTabs.FISHCASTING.key).register { entries ->
            FishcastingItems.registerItemCreativeTab(
                entries,
                FishcastingCreativeTabs.FISHCASTING.value
            )
        }
    }

    fun registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.CURSED.value,
            Axolotl.createAttributes().build()
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.BLESSED.value,
            Villager.createAttributes().build()
        )
    }
/*

    // I decided against it but I'm keeping it here just in case
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

     */
}
