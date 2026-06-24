package hauveli.fishcasting.mixin.blessed_bobber;

import com.li64.tide.data.TideFishingManager;
import com.li64.tide.data.commands.TestType;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishSelector;
import com.li64.tide.data.fishing.selector.FishingEntry;
import hauveli.fishcasting.registry.FishcastingTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static hauveli.fishcasting.features.paraphernalia.TideyFocusItem.LUCK_TWEAKING_BOBBER_PROBABILITY;

@Mixin(TideFishingManager.class)
public class LuckTweakingBobberTideFishingManagerMixin {

    private static final FishSelector fihSelector = new FishSelector();

    @Inject(method = "test", at = @At("RETURN"))
    private void onTestCatch(FishingContext context,
                             TestType type,
                             CallbackInfoReturnable<Map<FishingEntry, Double>> cir) {
        // Do nothing if wrong bait or wrong TestType
        if (context.hook() == null || context.hook().getBobber() == null) return;
        if (!context.hook().getBobber().is(FishcastingTags.LUCK_TWEAKING_BOBBERS)) {
            return;
        }

        Map<FishingEntry, Double> result = cir.getReturnValue();

        // Weight calculation
        // simplistic and NOT how I would prefer to do it, but it does the bare minimum.
        double sum = 0.0;
        for (double val : result.values()) {
            sum += val;
        }
        /*
            ex. for myself to reason/remember what I'm doing
            for P = 10% and W = 350
            (P * W) / (1 - P)
         */

        result.put(fihSelector, fishcasting$simpleSolver(LUCK_TWEAKING_BOBBER_PROBABILITY, sum));
    }

    @Unique
    private double fishcasting$simpleSolver(double targetProbability, double weightTotal) {
        return (weightTotal * targetProbability) / (1d-targetProbability);
    }
}