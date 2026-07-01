package hauveli.fishcasting.mixin.casting_rod;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.registries.items.TideFishingRodItem;
import hauveli.fishcasting.config.FishcastingConfigs;
import hauveli.fishcasting.features.paraphernalia.HexyRodItem;
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class RodCooldownAndBobberExecutionTideFishingHookMixin {

    @Shadow
    public abstract TideFishingRodItem getRodItem();
    @Shadow
    public abstract ItemStack getHook();
    @Shadow
    public abstract Player getPlayerOwner();
    @Shadow
    public abstract ItemStack getBobber();

    // executed bobber if it is attach but also applies a cooldown if we did retrieve it.
    @Inject(method = "startRetrieving", at = @At("HEAD"))
    public void retrieve(CallbackInfo ci) {
        // For executing bobbert and
        // For adding a cooldown to the rod, I would like a less jank way at some point...
        if (this.getRodItem() instanceof HexyRodItem castingRod) {
            // bobber thing
            ItemStack bobberItemStack = this.getBobber();
            if (bobberItemStack != null
                    && bobberItemStack.getItem() instanceof TideyFocusItem) {
                Player player = this.getPlayerOwner();
                //if (player.level().isClientSide()) return;
                castingRod.executeBobber(player.level(),
                        player, player.getUsedItemHand(),
                        bobberItemStack,
                        ((TideFishingHook)(Object)this).position());
            }
            // cooldown thing
            //hexyRodItem.setTicksSinceFishingMinigame(0);
            this.getPlayerOwner()
                    .getCooldowns()
                    .addCooldown(
                            this.getRodItem(),
                            FishcastingConfigs.INSTANCE.getCOMMON_CONFIG().getCooldownAfterFishingMinigame());
        }
    }
}
