package hauveli.fishcasting

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import at.petrak.hexcasting.fabric.cc.adimpl.CCIotaHolder
import at.petrak.hexcasting.fabric.cc.adimpl.CCItemIotaHolder
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.registries.TideFish
import com.li64.tide.registries.entities.fish.SmoothSwimmingFish
import hauveli.fishcasting.interop.emi.FishcastingEMIPlugin
import hauveli.fishcasting.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import java.util.function.BiConsumer


object FabricFishcasting : ModInitializer {
    override fun onInitialize() {
        Fishcasting.init()
        registerEntityAttributes() // todo: do something better than this
        registerIotaHolderItems() // todo: do something better than this
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
        // I don't understand what changed to make this be needed but it is needed since some time in dev54+ I think, todo: figure out why and how I can simplify
    }

    fun registerIotaHolderItems() {
        registerIotaHolderItems(FishcastingItems.BLESSED_FOCUS_BOBBER.value)
        registerIotaHolderItems(FishcastingItems.HEXXY_FOCUS_BOBBER.value)
        registerIotaHolderItems(FishcastingItems.SAMPLING_FOCUS_BOBBER.value)
    }

    fun registerIotaHolderItems(item: IotaHolderItem) {
        // https://github.com/FallingColors/HexMod/blob/edba45d957b19ba9b053993f09c1bcd3cca487fd/Forge/src/main/java/at/petrak/hexcasting/forge/cap/adimpl/CapItemIotaHolder.java#L33
        HexCardinalComponents.IOTA_HOLDER_LOOKUP.registerForItems(
            ItemApiLookup.ItemApiProvider { stack: ItemStack?, context: Void? ->
                object : CCIotaHolder {
                    override fun readIota(): Iota? {
                        return item.readIota(stack)
                    }

                    override fun writeIota(iota: Iota?, simulate: Boolean): Boolean {
                        if (!item.canWrite(stack, iota)) {
                            return false
                        }
                        if (!simulate) {
                            item.writeDatum(stack, iota)
                        }
                        return true
                    }

                    override fun writeable(): Boolean {
                        return item.writeable(stack)
                    }

                    override fun readFromNbt(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
                    }

                    override fun writeToNbt(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
                    }
                }
            },
            item as Item
        )
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
            FishcastingEntities.GOOP.value,
            SmoothSwimmingFish.createMobAttributes()
        )

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
                .add(Attributes.MAX_HEALTH, 65.0) // survives a blast unless really close or chained
        )

        FabricDefaultAttributeRegistry.register(
            FishcastingEntities.BLESSED.value,
            Villager.createAttributes().build()
        )
    }

    fun registerMoonPhaseFishies() {

        // hee hee hee...
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
