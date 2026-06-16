package hauveli.fishcasting.hexcasting;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.casting.actions.patterns.OpGetBobbersCatch;
import hauveli.fishcasting.casting.actions.patterns.OpGetBobbersOwner;
import hauveli.fishcasting.casting.actions.patterns.OpGetCatchesBobber;
import hauveli.fishcasting.casting.actions.patterns.OpGetOwnersBobber;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

// https://github.com/YukkuriC/HexOverpowered/blob/1.21/common/src/main/java/io/yukkuric/hexop/actions/HexOPActions.kt
// code adapted from the above
public class FishcastingActions {
    private static final Map<ResourceLocation, ActionRegistryEntry> CACHED = new HashMap<>();

    static {
        // with the exception of the bobber thingies here
        wrap("bobber/from_owner", "weeed", HexDir.SOUTH_EAST, OpGetOwnersBobber.INSTANCE);
        wrap("owner/from_bobber", "aqqqw", HexDir.WEST, OpGetBobbersOwner.INSTANCE);
        wrap("catch/from_bobber", "weeede", HexDir.SOUTH_EAST, OpGetBobbersCatch.INSTANCE);
        wrap("bobber/from_catch", "qaqqqw", HexDir.NORTH_WEST, OpGetCatchesBobber.INSTANCE);

        // The FishIota type can be safely deleted too, probably.
        // wrap("fish/from_entity", "wewweeedaaedqew", HexDir.SOUTH_WEST, OpGetFishIotaFromEntity.INSTANCE);

    }

    public static void registerActions(BiConsumer<ResourceLocation, ActionRegistryEntry> handler) {
        for (Map.Entry<ResourceLocation, ActionRegistryEntry> entry : CACHED.entrySet()) {
            handler.accept(entry.getKey(), entry.getValue());
        }
    }

    private static ActionRegistryEntry wrap(String name, String signature, HexDir dir, Action action) {
        HexPattern pattern = HexPattern.fromAngles(signature, dir);
        ResourceLocation key = Fishcasting.id(name);
        ActionRegistryEntry entry = new ActionRegistryEntry(pattern, action);
        CACHED.put(key, entry); // why won't java let me index normally here CACHED[key] = entry
        return entry;
    }
}