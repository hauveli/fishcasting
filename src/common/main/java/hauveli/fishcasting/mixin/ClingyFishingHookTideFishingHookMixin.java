package hauveli.fishcasting.mixin;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class ClingyFishingHookTideFishingHookMixin {
    @Shadow
    public abstract ItemStack getHook();

    @Shadow
    private int nibble;

    @Inject(method = "catchingFish", at = @At("TAIL"))
    private void fishcasting$clingy(CallbackInfo ci) {
        if (this.nibble == 1 // checking nibble first is probably smarter...
                && this.getHook().is(FishcastingTags.NO_NIBBLE_TIMEOUT_HOOKS)) {
            this.nibble = 2;
        }
    }

}
