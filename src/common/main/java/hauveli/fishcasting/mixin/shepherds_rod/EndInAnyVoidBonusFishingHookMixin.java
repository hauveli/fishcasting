package hauveli.fishcasting.mixin.shepherds_rod;

import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TideFishingHook.class)
public abstract class EndInAnyVoidBonusFishingHookMixin {

    @Shadow
    private ItemStack rod;

    @Shadow
    private FishingMedium medium;

    // it was between a terrible mixin target or reconstructing a record.
    // because fishing up items is done so rarely, and because I check for the rod type first
    // this should be mindful enough...
    @Inject(
            method = "getContext",
            at = @At("RETURN"),
            cancellable = true
    )
    private void fishcasting$overrideContext(CallbackInfoReturnable<FishingContext> cir) {
        if (fishcasting$shouldUseEndDimension()) {
            FishingContext context = cir.getReturnValue();
            cir.setReturnValue(new FishingContext(
                    context.level(),
                    context.hook(),
                    context.rod(),
                    context.rng(),
                    context.pos(),
                    context.blockPos(),
                    context.luck(),
                    context.medium(),
                    context.exactBiome(),
                    context.nearestBiome(),
                    Level.END,
                    context.temperature(),
                    context.moonPhase(),
                    context.season()
            ));
        }
    }

    @Unique
    private boolean fishcasting$shouldUseEndDimension() {
        return rod.is(FishcastingItems.SHEPHERDS_CASTING_ROD.getValue())
                && medium == FishingMedium.VOID;
    }
}