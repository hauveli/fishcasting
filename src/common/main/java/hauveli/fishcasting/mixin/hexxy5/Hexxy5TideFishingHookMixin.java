package hauveli.fishcasting.mixin.hexxy5;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.config.FishcastingConfigs;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class Hexxy5TideFishingHookMixin {
    @Shadow
    public abstract Player getPlayerOwner();

    @Inject(
            method = "onClientRemoval",
            at = @At("HEAD"),
            cancellable = true)
    private void onClientRemoval(CallbackInfo ci) {
        if (!FishcastingConfigs.INSTANCE.getCLIENT_CONFIG().getHexxy5KiltSableBugFix().get())
            return;
        ci.cancel();
        TideFishingHook hook = ((TideFishingHook) (Object) this);
        if (getPlayerOwner().fishing != null && hook.tickCount == 0L) {
            return;
        }
        ((Hexxy5TideFishingHookAccessor) hook).invokeUpdateOwnerInfo(null);
    }
}