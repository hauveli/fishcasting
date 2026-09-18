package hauveli.fishcasting

import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import at.petrak.hexcasting.fabric.cc.adimpl.CCItemIotaHolder
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.registries.TideFish
import com.li64.tide.util.MoonPhases
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.StructureIota
import hauveli.fishcasting.casting.iota.WeatherIota
import hauveli.fishcasting.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.Structures
import net.minecraft.data.worldgen.TrialChambersStructurePools
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.level.Level.END
import net.minecraft.world.level.Level.NETHER
import net.minecraft.world.level.Level.OVERWORLD
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.structure.BuiltinStructures
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
        registerMoonPhaseFishies()
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

    fun registerMoonPhaseFishies() {
        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static StructureIota(BuiltinStructures.ANCIENT_CITY)
        }
        }, TideFish.ECHO_SNAPPER)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static StructureIota(BuiltinStructures.TRIAL_CHAMBERS)
        }
        }, TideFish.WINDBASS)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static BiomeIota(Biomes.CHERRY_GROVE)
        }
        }, TideFish.BLOSSOM_BASS)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static DimensionIota(OVERWORLD)
        }
        }, TideFish.ALPHA_FISH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static DimensionIota(NETHER)
        }
        }, TideFish.WITHERFIN)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            // todo: replace with weatherIota
            return@Static DimensionIota(END)
        }
        }, TideFish.ENDERGAZER)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
                    // todo: replace with weatherIota
            return@Static ListIota(listOf(WeatherIota(WeatherType.RAIN.ordinal), WeatherIota(WeatherType.STORM.ordinal)))
        }
        }, TideFish.COELACANTH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static MoonPhaseIota(MoonPhases.FULL_MOON)
        }
        }, TideFish.SHOOTING_STARFISH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static MoonPhaseIota(MoonPhases.FULL_MOON)
        }
        }, TideFish.SUN_EMBLEM)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static MoonPhaseIota(MoonPhases.FIRST_QUARTER)
        }
        }, TideFish.SATURN_CUTTLEFISH)


        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static MoonPhaseIota(MoonPhases.NEW_MOON)
        }
        }, TideFish.NEPTUNE_KOI)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static MoonPhaseIota(MoonPhases.THIRD_QUARTER)
        }
        }, TideFish.URANIAS_PISCES)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static ListIota(listOf(MoonPhaseIota(MoonPhases.WANING_CRESCENT), MoonPhaseIota(MoonPhases.WAXING_CRESCENT)))
        }
        }, TideFish.PLUTO_SNAIL)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static ListIota(listOf(MoonPhaseIota(MoonPhases.WANING_GIBBOUS), MoonPhaseIota(MoonPhases.WAXING_GIBBOUS)))
        }
        }, TideFish.MARSTILUS)
    }
/*

    // I decided against it but I'm keeping it here just in case
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

     */
}
