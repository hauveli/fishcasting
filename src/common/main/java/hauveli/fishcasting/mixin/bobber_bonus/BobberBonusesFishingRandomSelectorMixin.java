package hauveli.fishcasting.mixin.bobber_bonus;

import com.li64.tide.data.TideData;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishingEntry;
import com.li64.tide.data.fishing.selector.FishingRandomSelector;
import com.li64.tide.registries.TideFish;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.world.item.ItemStack;
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
public class BobberBonusesFishingRandomSelectorMixin {

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
        if (random.nextFloat() > LUCK_TWEAKING_BOBBER_PROBABILITY)
            return;
        ItemStack bobberStack = context.hook().getBobber();
        if (bobberStack.is(FishcastingTags.LUCK_TWEAKING_BOBBERS)) {
            cir.setReturnValue(allPossibleCatches.get(random.nextInt(allPossibleCatches.size())).getResult(context));
        } else if (bobberStack.is(FishcastingTags.SLIMY_BOBBERS)) {
            // god I'm lazy, todo: be smarter about this.
            if (!context.medium().equals("water")) {
                return;
            }
            var maybeSlimySalmon = FishData.get(TideFish.SLIMY_SALMON.asItem());
            maybeSlimySalmon.ifPresent(
                    fishData ->
                            cir.setReturnValue(fishData.getResult(context)));
        }
    }
}
