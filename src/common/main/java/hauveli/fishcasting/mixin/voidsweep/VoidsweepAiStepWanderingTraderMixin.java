package hauveli.fishcasting.mixin.voidsweep;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static hauveli.fishcasting.features.trader.BlessedEntity.cursedTheatrics;

// uhhhhhhhh I'm not sure if this is a good idea but I can always fix it later
// my reason for doing this is that I want to have it happen at least one tick after being brainswept,
// and I'm not sure how to do that in a reasonable way...
// anyway, this  should hopefully be ok, as wandering traders rarely appear, and it is an almost immediate boolean check...
@Mixin(WanderingTrader.class)
public abstract class VoidsweepAiStepWanderingTraderMixin {

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void doTheThing(CallbackInfo ci) {
        WanderingTrader trader = (WanderingTrader) (Object) this;
        if (HexAPI.instance().isBrainswept(trader) && trader.hurtTime < 9 && trader.isAlive()) {
            ((AbstractVillagerInvoker) this).invokeStopTrading();
            cursedTheatrics(trader);
            // as tempting as it is to cancel aiStep, I need it in order for deltaMovement to happen on subsequent steps?
            //ci.cancel();
        }
    }
}
