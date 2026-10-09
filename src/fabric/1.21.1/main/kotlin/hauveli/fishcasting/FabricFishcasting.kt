package hauveli.fishcasting

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import at.petrak.hexcasting.fabric.cc.adimpl.CCItemIotaHolder
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.registries.TideFish
import com.li64.tide.registries.entities.fish.SmoothSwimmingFish
import hauveli.fishcasting.casting.arithmetic.FishcastingEnvironmentArithmetic
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
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

        registerCreativeModeTabItems()
        registerMoonPhaseFishies()
        // why is this ok in fabric but not neoforge? what...
        //registerItemModelProperties()
    }

    // I genuinely have no fucking idea how to register these in common without an interface and doing xplat implementations like hexmod does...
    // I should maybe do that just so I don't end up forgetting to do something thanks to the IDE nagging if I do forget, but I probably won't :clueless:
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
            FishcastingEntities.EDIFIED_FISH.value,
            SmoothSwimmingFish.createMobAttributes()
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.CURSED.value,
            Axolotl.createAttributes().build()
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.PERHOSGATA.value,
            SmoothSwimmingFish.createMobAttributes()
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.BLESSED.value,
            Villager.createAttributes().build()
        )
    }

    fun registerMoonPhaseFishies() {

        // hee hee hee...
        // todo:
        // I'll have to fix this in dev 53 and/or dev 54
        val mindsReflection = HexPattern.fromAngleString("qaq", HexDir.NORTH_EAST)
        val compassPurification = HexPattern.fromAngleString("aa", HexDir.EAST)
        val alidadesPurification = HexPattern.fromAngleString("wa", HexDir.NORTH_EAST)
        val archersDistillation = HexPattern.fromAngleString("wqaawdd", HexDir.EAST)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static ListIota(
                TreeList.from(
                    listOf<PatternIota>(
                        PatternIota(mindsReflection),
                        PatternIota(compassPurification),
                        PatternIota(mindsReflection),
                        PatternIota(alidadesPurification),
                        PatternIota(archersDistillation),
                    )
                )
            )
        }
        }, TideFish.MANTA_RAY)

        /*
        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static StructureIota(BuiltinStructures.ANCIENT_CITY)
        }
        }, TideFish.ECHO_SNAPPER)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static StructureIota(BuiltinStructures.TRIAL_CHAMBERS)
        }
        }, TideFish.WINDBASS)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static BiomeIota(Biomes.CHERRY_GROVE)
        }
        }, TideFish.BLOSSOM_BASS)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static BiomeIota(Biomes.JUNGLE)
        }
        }, TideFish.ARAPAIMA)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static DimensionIota(OVERWORLD)
        }
        }, TideFish.ALPHA_FISH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static DimensionIota(NETHER)
        }
        }, TideFish.BLAZING_SWORDFISH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
            return@Static DimensionIota(END)
        }
        }, TideFish.DRAGON_FISH)

        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems({
                stack, _ -> CCItemIotaHolder.Static(stack) {
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
         */
    }
/*

    // I decided against it but I'm keeping it here just in case
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FishcastingEntityTypes.BLESSED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WanderingTrader::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

     */
}
