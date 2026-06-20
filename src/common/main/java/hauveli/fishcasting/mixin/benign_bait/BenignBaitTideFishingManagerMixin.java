package hauveli.fishcasting.mixin.benign_bait;

import com.li64.tide.data.TideData;
import com.li64.tide.data.TideFishingManager;
import com.li64.tide.data.commands.TestType;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishSelector;
import com.li64.tide.data.fishing.selector.FishingEntry;
import com.li64.tide.registries.entities.misc.fishing.HookAccessor;
import hauveli.fishcasting.registry.FishcastingItems;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static hauveli.fishcasting.Fishcasting.random;

@Mixin(TideFishingManager.class)
public class BenignBaitTideFishingManagerMixin {

    private static final FishSelector fihSelector = new FishSelector();

    @Inject(method = "test", at = @At("RETURN"))
    private void onTestCatch(FishingContext context,
                             TestType type,
                             CallbackInfoReturnable<Map<FishingEntry, Double>> cir) {
        // Do nothing if wrong bait or wrong TestType
        if (context.activeBait().getItem() != FishcastingItems.BENIGN_BAIT
                || type != TestType.LOOT) {
            return;
        }

        Map<FishingEntry, Double> result = cir.getReturnValue();

        result.put(fihSelector, Double.MAX_VALUE);
    }
}