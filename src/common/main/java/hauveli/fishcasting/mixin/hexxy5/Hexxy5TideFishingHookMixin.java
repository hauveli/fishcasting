package hauveli.fishcasting.mixin.hexxy5;

import com.li64.tide.data.TideTags;
import com.li64.tide.data.rods.BaitContents;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.config.FishcastingConfigs;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class Hexxy5TideFishingHookMixin {
    @Shadow
    public abstract Player getPlayerOwner();

    // I'm working on 0 hours of sleep so forgive me for not finishing the cleanup on this.
    /*
    @Shadow
    private void updateOwnerInfo();
     */

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
        //hook.updateOwnerInfo(null);
    }

    // fucking EVIL anti-bait waste mixin to fix the doubled bait usage bug on the midas/villager rods
    // this shouldn't cause issues (so long as I'm not mistaken in understanding the messages by the developer and the code)
    @ModifyExpressionValue(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/li64/tide/util/BaitUtils;hasBait(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean fishcasting$onlyFishEatBait(
            boolean hasBait,
            @Local(ordinal = 1) ItemStack stack
    ) {
        return hasBait && stack.is(TideTags.Items.FISH);
    }
}