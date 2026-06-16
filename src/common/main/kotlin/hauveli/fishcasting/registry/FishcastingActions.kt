package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import hauveli.fishcasting.casting.actions.patterns.OpGetBobbersCatch
import hauveli.fishcasting.casting.actions.patterns.OpGetBobbersOwner
import hauveli.fishcasting.casting.actions.patterns.OpGetCatchesBobber
import hauveli.fishcasting.casting.actions.patterns.OpGetOwnersBobber

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

    // I didnt check the regex but I'm hoping this stops it
    //val CONGRATULATE = make("congratulate" - , HexDir.WEST, - "eed", OpCongratulate)
    //val GREAT_CONGRATULATE = make("congratulate/great", - HexDir.EAST -, "qwwqqqwwqwded", OpCongratulate)

    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
