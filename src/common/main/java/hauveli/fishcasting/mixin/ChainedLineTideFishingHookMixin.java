package hauveli.fishcasting.mixin;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.registry.FishcastingItems;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TideFishingHook.class)
public abstract class ChainedLineTideFishingHookMixin {
    @Shadow
    private Entity hookedIn;

    @Shadow
    public abstract ItemStack getHook();

    @Shadow
    public abstract ItemStack getLine();

    @Shadow
    public abstract Player getPlayerOwner();

    @Unique
    private Integer maxDistanceSquared = 16 * 16;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void fishcasting$forceHookToBeNearby(CallbackInfo ci) {
        if (!this.getLine().is(FishcastingItems.CHAIN_LINKED_FISHING_LINE.getValue()))
            return;
        TideFishingHook hook = (TideFishingHook) (Object) this;
        if (this.getPlayerOwner().position().distanceToSqr(hook.position()) < maxDistanceSquared)
            return;
        // correct line, and we are too far
        moveHookTowardsPlayerRespectTerrain(hook);
    }

    // maybe todo: care about teleporting.
    // this was mainly because I thought it would be REALLY cool to fish while on a simulated contraption, and the effect is surprisingly good
    // for how easy this was to add.
    private void moveHookTowardsPlayerRespectTerrain(TideFishingHook hook) {
        Vec3 playerPos = this.getPlayerOwner().position();
        Vec3 hookPos = hook.position();

        Vec3 direction = playerPos.subtract(hookPos).normalize();

        Vec3 newPos = hookPos;

        for (int i = 0; i < 15; i++) {
            Vec3 candidate = newPos.add(direction);

            BlockPos blockPos = BlockPos.containing(candidate);

            Level level = hook.level();
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.isAir()
                    || !blockState.getFluidState().isEmpty()) {
                newPos = candidate;
                break;
            }
        }

        hook.setPos(newPos);
    }
}
