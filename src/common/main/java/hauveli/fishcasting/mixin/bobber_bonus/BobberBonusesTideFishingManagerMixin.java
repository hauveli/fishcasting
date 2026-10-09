package hauveli.fishcasting.mixin.bobber_bonus;

import com.li64.tide.data.TideFishingManager;
import com.li64.tide.data.commands.TestType;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishSelector;
import com.li64.tide.data.fishing.selector.FishingEntry;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static hauveli.fishcasting.features.paraphernalia.TideyFocusItem.LUCK_TWEAKING_BOBBER_PROBABILITY;

@Mixin(TideFishingManager.class)
public class BobberBonusesTideFishingManagerMixin {

    private static final FishSelector luckTweakSelector = new FishSelector() {
        @Override
        public MutableComponent getTestKey() {
            return Component.translatable("commands.fishing.entries.luck_tweaking_bobber_selector");
        }
    };


    private static final FishSelector slimyTweakSelector = new FishSelector() {
        @Override
        public MutableComponent getTestKey() {
            return Component.translatable("commands.fishing.entries.slimy_bobber_selector");
        }
    };

    @Inject(method = "test", at = @At("RETURN"))
    private void onTestCatch(FishingContext context,
                             TestType type,
                             CallbackInfoReturnable<Map<FishingEntry, Double>> cir) {
        // Do nothing if wrong bait or wrong TestType
        if (context.hook() == null || context.hook().getBobber() == null) return;
        ItemStack bobberStack = context.hook().getBobber();

        if (bobberStack.is(FishcastingTags.LUCK_TWEAKING_BOBBERS)) {
            fishcasting$luckTweak(cir);
        } else if (bobberStack.is(FishcastingTags.SLIMY_BOBBERS)) {
            fischasting$slimyTweak(cir);
        }
    }

    @Unique
    private void fischasting$slimyTweak(CallbackInfoReturnable<Map<FishingEntry, Double>> cir) {
        Map<FishingEntry, Double> result = cir.getReturnValue();
        double sum = 0.0;
        for (double val : result.values()) {
            sum += val;
        }
        result.put(luckTweakSelector, fishcasting$simpleSolver(LUCK_TWEAKING_BOBBER_PROBABILITY, sum));
    }

    @Unique
    private void fishcasting$luckTweak(CallbackInfoReturnable<Map<FishingEntry, Double>> cir) {
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

        result.put(luckTweakSelector, fishcasting$simpleSolver(LUCK_TWEAKING_BOBBER_PROBABILITY, sum));
    }

    @Unique
    private double fishcasting$simpleSolver(double targetProbability, double weightTotal) {
        return (weightTotal * targetProbability) / (1d-targetProbability);
    }
}