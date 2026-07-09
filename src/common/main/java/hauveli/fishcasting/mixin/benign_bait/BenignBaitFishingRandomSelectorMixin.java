package hauveli.fishcasting.mixin.benign_bait;

import com.li64.tide.data.commands.TestType;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.selector.FishSelector;
import com.li64.tide.data.fishing.selector.FishingEntry;
import com.li64.tide.data.fishing.selector.FishingRandomSelector;
import com.li64.tide.util.BaitUtils;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FishingRandomSelector.class)
public class BenignBaitFishingRandomSelectorMixin {

    @Inject(
            method = "select(Ljava/util/List;Lcom/li64/tide/data/fishing/FishingContext;)Lcom/li64/tide/data/fishing/CatchResult;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static <T extends FishingEntry> void fishcasting$select(
            List<T> entries, FishingContext context, CallbackInfoReturnable<CatchResult> cir
    ) {
        boolean hasBenignBait = BaitUtils.getBaitItems(context.rod()).stream()
                .anyMatch(stack -> stack.is(FishcastingItems.BENIGN_BAIT.getValue()));
        if (!hasBenignBait || (cir.getReturnValue().entry().isPresent() &&
                                cir.getReturnValue().entry().get().matchesTestType(TestType.FISH))) {
            return;
        }

        List<T> fishOnly = entries.stream()
                .filter(entry -> entry instanceof FishSelector)
                .toList();

        if (fishOnly.isEmpty()) {
            return;
        }

        cir.setReturnValue(
                fishOnly.getFirst().getResult(context) // uses FishSelector, I'm not sure if I could just grab it directly so I'm doing this
        );
    }

    @Unique
    private static boolean fishcasting$shouldForceFishOnly(ItemStack activeBait) {
        return activeBait.getItem() == FishcastingItems.BENIGN_BAIT.getValue();
    }
}
