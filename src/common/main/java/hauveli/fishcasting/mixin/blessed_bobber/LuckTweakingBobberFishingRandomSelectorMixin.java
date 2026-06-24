package hauveli.fishcasting.mixin.blessed_bobber;

import com.li64.tide.data.TideData;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishingEntry;
import com.li64.tide.data.fishing.selector.FishingRandomSelector;
import hauveli.fishcasting.registry.FishcastingTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static hauveli.fishcasting.Fishcasting.random;
import static hauveli.fishcasting.features.paraphernalia.TideyFocusItem.LUCK_TWEAKING_BOBBER_PROBABILITY;

@Mixin(FishingRandomSelector.class)
public class LuckTweakingBobberFishingRandomSelectorMixin {

    @Unique
    private static List<FishingEntry> allPossibleCatches =
            Stream.of(
                            TideData.FISHING_LOOT.get().values(),
                            TideData.FISH.get().values(),
                            TideData.CRATES.get().values()
                    )
                    .flatMap(Collection::stream)
                    .map(e -> (FishingEntry) e)
                    .toList();

    @Inject(
            method = "select(Ljava/util/List;Lcom/li64/tide/data/fishing/FishingContext;)Lcom/li64/tide/data/fishing/CatchResult;",
            at = @At("RETURN"), // hmmm I need to figure out priorities and/or implement a more proper solution...
            cancellable = true
    )
    private static <T extends FishingEntry> void fishcasting$select(
            List<T> entries, FishingContext context, CallbackInfoReturnable<CatchResult> cir
    ) {
        if (context.hook() == null || context.hook().getBobber() == null) return;
        if (!context.hook().getBobber().is(FishcastingTags.LUCK_TWEAKING_BOBBERS)
                || random.nextFloat() > LUCK_TWEAKING_BOBBER_PROBABILITY) {
            return;
        }

        cir.setReturnValue(allPossibleCatches.get(random.nextInt(allPossibleCatches.size())).getResult(context));
    }
}
