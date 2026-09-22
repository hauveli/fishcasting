package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetBobbersCatch
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetBobbersOwner
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetCatchesBobber
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetMedium
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetMoonPhase
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetOwnersBobber
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetBiome
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetClimate
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetDayTime
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetDimension
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetStructure
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetWeather
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromBiome
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromDimension
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromMedium
import hauveli.fishcasting.casting.actions.patterns.fish.OpGetFishMaximum
import hauveli.fishcasting.casting.actions.patterns.fish.OpGetFishMinimum
import hauveli.fishcasting.casting.actions.spells.bobber.OpAttachBobber
import hauveli.fishcasting.casting.actions.spells.bobber.OpDetachBobber
import hauveli.fishcasting.casting.actions.spells.fish.OpFishifyItem
import hauveli.fishcasting.casting.actions.spells.fish.OpItemifyFish
import hauveli.fishcasting.casting.actions.spells.environment.OpMoonPhaseChange
import hauveli.fishcasting.casting.actions.spells.fish.OpClobberFishToDeath

object FishcastingActions : FishcastingRegistrar<ActionRegistryEntry>(
    HexRegistries.ACTION,
    { HexActions.REGISTRY },
) {
    /*

        wrap("bobber/from_owner", "weeed", HexDir.SOUTH_EAST, OpGetOwnersBobberJ.INSTANCE);
        wrap("owner/from_bobber", "aqqqw", HexDir.WEST, OpGetBobbersOwnerJ.INSTANCE);
        wrap("catch/from_bobber", "weeede", HexDir.SOUTH_EAST, OpGetBobbersCatchJ.INSTANCE);
        wrap("bobber/from_catch", "qaqqqw", HexDir.NORTH_WEST, OpGetCatchesBobberJ.INSTANCE);

        // The FishIota type can be safely deleted too, probably.
        // wrap("fish/from_entity", "wewweeedaaedqew", HexDir.SOUTH_WEST, OpGetFishIotaFromEntity.INSTANCE);
     */
    val BOBBER_FROM_OWNER = make("bobber/from_owner", HexDir.SOUTH_EAST, "weeed", OpGetOwnersBobber)
    val OWNER_FROM_BOBBER = make("owner/from_bobber", HexDir.WEST, "aqqqw", OpGetBobbersOwner)
    val CATCH_FROM_BOBBER = make("catch/from_bobber", HexDir.SOUTH_EAST, "weeede", OpGetBobbersCatch)
    val BOBBER_FROM_CATCH = make("bobber/from_catch", HexDir.NORTH_WEST, "qaqqqw", OpGetCatchesBobber)

    val BOBBER_ATTACH = make("bobber/attach", HexDir.SOUTH_EAST, "weeedd", OpAttachBobber)
    val BOBBER_DETACH = make("bobber/detach", HexDir.NORTH_EAST, "aaqqqw", OpDetachBobber)

    val BOBBER_MEDIM = make("bobber/medium", HexDir.SOUTH_EAST, "weeedew", OpGetMedium)

    val FISHIFY_ITEM = make("fish/from_item", HexDir.SOUTH_EAST, "dewqdaqeqqqeaeqwede", OpFishifyItem)
    val ITEMIFY_FISH = make("fish/to_item", HexDir.SOUTH_EAST, "dewqeaeqqqeqadqwede", OpItemifyFish)
    val KILL_FISH = make("fish/kill", HexDir.SOUTH_EAST, "dewqeaeqqqeqadqwedewwww", OpClobberFishToDeath)

    val FISH_MIN = make("fish/min", HexDir.NORTH_WEST, "wqaqwwewq", OpGetFishMinimum)
    val FISH_MAX = make("fish/max", HexDir.NORTH_WEST, "wqaqwwqwe", OpGetFishMaximum)
    val FISH_MEDIUM = make("fish/medium", HexDir.NORTH_WEST, "wqaqwqdadad", OpFishFoundFromMedium) // hehe min max medium
    val FISH_BIOME = make("fish/biome", HexDir.NORTH_WEST, "wqaqwqwdaqqqa", OpFishFoundFromBiome) // hehe min max medium
    val FISH_DIMENSION = make("fish/dimension", HexDir.NORTH_WEST, "wqaqwqwewdwew", OpFishFoundFromDimension) // hehe min max medium

    // dimension, weather, moon can be their own base shape
    val MOVE_MOON = make("environment/moon/increment", HexDir.SOUTH_WEST, "awa", OpMoonPhaseChange)
    val PHASE_FROM_MOON = make("environment/moon/get", HexDir.NORTH_EAST, "wqaqwedqdqdwwdqd", OpGetMoonPhase)

    val DIMENSION_FROM_LEVEL = make("environment/dimension", HexDir.NORTH_EAST, "wqaqwqaeawwaeaea", OpGetDimension)

    // daytime get (DISTINCT FROM GAMETIME IN HEXAL!!!!) (THIS ONE IS USEFUL FOR FISHING!!!!!)
    val DAYTIME = make("environment/daytime", HexDir.WEST, "dwdwewewewewewqeweeqee", OpGetDayTime)
    // val MOVE_SUN = make("environment/sun", HexDir.SOUTH_WEST, "awaw", OpSpinTheEarth)

    // medium, biome, structure can be their own base shape
    val BIOME_FROM_BLOCKPOS = make("environment/biome", HexDir.NORTH_EAST, "wqaqwdaaeaeawwaea", OpGetBiome)
    val PRECIPITATION_FROM_BLOCKPOS = make("environment/weather", HexDir.NORTH_EAST, "wqaqwqaeawwaeawdd", OpGetWeather)
    val STRUCTURES_FROM_BLOCKPOS = make("environment/structure", HexDir.NORTH_EAST, "wqaqwdwdqdwwdqdqd", OpGetStructure)
    val CLIMATE_FROM_BLOCKPOS = make("environment/climate", HexDir.NORTH_EAST, "wqaqweddwaeawwaea", OpGetClimate)


    // I didnt check the regex but I'm hoping this stops it
    //val CONGRATULATE = make("congratulate" - , HexDir.WEST, - "eed", OpCongratulate)
    //val GREAT_CONGRATULATE = make("congratulate/great", - HexDir.EAST -, "qwwqqqwwqwded", OpCongratulate)

    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
