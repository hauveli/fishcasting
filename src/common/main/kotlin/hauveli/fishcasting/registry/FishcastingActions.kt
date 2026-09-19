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
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetMoonPhase
import hauveli.fishcasting.casting.actions.patterns.bobber.OpGetOwnersBobber
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetBiome
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetDayTime
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetStructure
import hauveli.fishcasting.casting.actions.patterns.environment.OpGetWeather
import hauveli.fishcasting.casting.actions.spells.fish.OpFishifyItem
import hauveli.fishcasting.casting.actions.spells.fish.OpItemifyFish
import hauveli.fishcasting.casting.actions.spells.environment.OpMoonPhaseChange

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

    val BOBBER_ATTACH = make("bobber/attach", HexDir.SOUTH_EAST, "weeedd", OpGetCatchesBobber)
    val BOBBER_DETACH = make("bobber/detach", HexDir.NORTH_EAST, "aaqqqw", OpGetCatchesBobber)

    val FISHIFY_ITEM = make("fish/from_item", HexDir.SOUTH_EAST, "dewqdaqeqqqeaeqwede", OpFishifyItem)
    val ITEMIFY_FISH = make("fish/to_item", HexDir.SOUTH_EAST, "dewqeaeqqqeqadqwede", OpItemifyFish)

    // dimension, weather, moon can be their own base shape
    val MOVE_MOON = make("world/moon", HexDir.SOUTH_WEST, "awa", OpMoonPhaseChange)
    val PHASE_FROM_MOON = make("world/moon/phase", HexDir.NORTH_EAST, "wqaqwedqdqdwwdqd", OpGetMoonPhase)

    // medium, biome, structure can be their own base shape
    val BIOME_FROM_BLOCKPOS = make("world/biome", HexDir.NORTH_EAST, "wqaqwdaaeaeawwaea", OpGetBiome)
    val PRECIPITATION_FROM_BLOCKPOS = make("world/weather", HexDir.NORTH_EAST, "wqaqwdaaeaeawwaea", OpGetWeather)
    val STRUCTURES_FROM_BLOCKPOS = make("world/structure", HexDir.NORTH_EAST, "wqaqwdwdqdwwdqdqd", OpGetStructure)

    // daytime get
    val DAYTIME = make("world/daytime", HexDir.WEST, "dwdwewewewewewqeweeqee", OpGetDayTime)
    // val MOVE_SUN = make("world/sun", HexDir.SOUTH_WEST, "awaw", OpSpinTheEarth)

    // I didnt check the regex but I'm hoping this stops it
    //val CONGRATULATE = make("congratulate" - , HexDir.WEST, - "eed", OpCongratulate)
    //val GREAT_CONGRATULATE = make("congratulate/great", - HexDir.EAST -, "qwwqqqwwqwded", OpCongratulate)

    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
