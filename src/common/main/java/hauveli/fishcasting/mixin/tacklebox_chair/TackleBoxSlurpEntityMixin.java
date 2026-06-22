package hauveli.fishcasting.mixin.tacklebox_chair;

import com.li64.tide.Tide;
import com.li64.tide.config.TideConfig;
import com.li64.tide.data.FishLengthHolder;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.item.TideItemData;
import hauveli.fishcasting.features.chair.TackleBoxChairEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

//import static com.li64.tide.data.item.TideItemData.CATCH_TIMESTAMP;
import static com.li64.tide.data.item.TideItemData.FISH_LENGTH;

@Mixin(ItemEntity.class)
public class TackleBoxSlurpEntityMixin {

    /*
        Note:
        edge case exists whem:
            1. Fish item fished up on exact same gametick
            2. Fish is exact same species
            3. Fish is exact same length (length is a random DOUBLE.)
            4. Fish is fished up to the exact same spot
            5. Both fish merge into one ItemEntity before they touch the player
            6. Merged fish touch the same player on the same tick
            7. The player is on a tacklebox chair

        I wrote this before I changed how I detect recently caught fish (for the bobber to detect "hoooked" on reel) so I don't know if this is still true...

        I did not care to implement a check for this but if this happens, one fish (or more) is lost
        because this is so unlikely I'm calling it an easter egg and not caring.
        Chances of this occuring even intentionally are likely comparable to some lower Hash collisions due to
        how long it takes to fish (really really really unlikely)
     */
    // I would like to mixin to something better if I knew any...
    @Inject(
            method = "playerTouch",
            at = @At("HEAD"),
            cancellable = true)
    private void fishcasting$onTake(Player player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        if (player.getVehicle() instanceof TackleBoxChairEntity tackleBoxChairEntity) {
            ItemStack stack = itemEntity.getItem();
            // only consider recently caught fish, implicitly these are always alive(?), but this prevents
            // some behaviours I'm not sure I'd like but I may change the maximumFishAgeForAutomaticBoxing variable
            int maximumFishAgeForAutomaticBoxing = 500; // in ticks?
            //if (CATCH_TIMESTAMP.get(stack) != null) {
            assert Minecraft.getInstance().level != null;
            //if (Minecraft.getInstance().level.getDayTime() < CATCH_TIMESTAMP.get(stack) + maximumFishAgeForAutomaticBoxing) {
            int targetBucketSlot = -1; // remains -1 if no valid slot is found or config disallows
            // Yippee?
            if (Tide.CONFIG.items.bucketableFishItems != TideConfig.Items.BucketableMode.NEVER) {
                // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/mixin/ItemMixin.java#L38
                Optional<FishData> dataOp = FishData.get(stack);
                if (dataOp.isEmpty())
                    return; // skedaddle, I don't know when this could happen but might as well
                FishData fishData = dataOp.get();
                // bucket only if config allows it
                if (fishData.bucket().get().value() instanceof BucketItem fishBucketItem) {
                    Fluid fluid = getFluid(fishBucketItem);
                    targetBucketSlot = validBucketAtPositiveIndex(fluid, tackleBoxChairEntity);
                    if (targetBucketSlot != -1) {
                        stack = bucketedFishFromFish(fishData, stack);
                    }
                }
            }
            if (targetBucketSlot == -1) {
                // emergency exit if we don't have enough inventory space
                if (hasNoFreeSlots(tackleBoxChairEntity)) {
                    return;
                }
                // this tidies up the inventory to prepare it for the incoming item, if stack is
                stack = mergeIfNeeded(stack, tackleBoxChairEntity);
                for (int slotIndex = 0; slotIndex < tackleBoxChairEntity.getContainerSize(); slotIndex++) {
                    ItemStack slotStack = tackleBoxChairEntity.getItem(slotIndex);
                    if (!slotStack.isEmpty()) {
                        continue;
                    }
                    player.playSound(SoundEvents.BUCKET_EMPTY_FISH, 1.0f, 1.0f);
                    tackleBoxChairEntity.setItem(slotIndex, stack);
                    break;
                }
            } else {
                player.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
                tackleBoxChairEntity.setItem(targetBucketSlot, stack);
            }
            itemEntity.discard();
            ci.cancel();
            //}
            //}
        }
    }

    @Unique
    private boolean hasNoFreeSlots(TackleBoxChairEntity entity) {
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Determine and keep the largest fish, clean up/auto-merge box inventory
    @Unique
    private ItemStack mergeIfNeeded(ItemStack fishToDeposit, TackleBoxChairEntity entity) {
        int largerIndex = largerIdenticalFishAtPositiveIndex(fishToDeposit, entity);
        ItemStack outputStack;
        if (largerIndex >= 0) {
            outputStack = entity.getItem(largerIndex).copy();
        } else {
            outputStack = fishToDeposit;
        }
        tidyUpFishStacksMatching(fishToDeposit, entity);
        return outputStack;
    }

    @Unique
    private void tidyUpFishStacksMatching(ItemStack fishToDeposit, TackleBoxChairEntity entity) {
        // I get a total count so I can figure out how many fish I need to move around, makes stacking them easier (to me)
        int totalCount = 0;
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem().equals(fishToDeposit.getItem())) {
                int count = slotStack.getCount();
                totalCount += count;
            }
        }
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem().equals(fishToDeposit.getItem())) {
                // make them all stackable
                int count = Math.min(totalCount, 64);
                if (count > 0) {
                    totalCount -= count;

                    ItemStack defaultInstance = slotStack.getItem().getDefaultInstance();
                    defaultInstance.setCount(count);
                    entity.setItem(i, defaultInstance);
                } else {
                    entity.removeItem(i, entity.getItem(i).getCount());
                }
            }
        }
        // did we run out of slots before we could put it into storage?
        if (totalCount > 0) {

        }
    }

    @Unique
    private int largerIdenticalFishAtPositiveIndex(ItemStack fishToDeposit, TackleBoxChairEntity entity) {
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem().equals(fishToDeposit.getItem())
                    && FISH_LENGTH.isPresent(slotStack) && FISH_LENGTH.isPresent(fishToDeposit)
                    && FISH_LENGTH.get(slotStack) > FISH_LENGTH.get(fishToDeposit)) {
                // compare length, if to deposit is greater, move slotStack into an existing stack, if it exists
                return i;
            }
        }
        return -1;
    }

    // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/mixin/ItemMixin.java#L76C19-L76
    @Unique
    private ItemStack bucketedFishFromFish(FishData data, ItemStack fish) {
        ItemStack newStack = new ItemStack(data.bucket().get());
        if (TideItemData.FISH_LENGTH.isPresent(fish) /*|| TideItemData.IS_SHINY.isPresent(fish)*/) {
            double length = TideItemData.FISH_LENGTH.getOrDefault(fish, 0.0);
            /*boolean isShiny = TideItemData.IS_SHINY.getOrDefault(fish, false); */
            CustomData.update(DataComponents.BUCKET_ENTITY_DATA, newStack, tag -> {
                if (TideItemData.FISH_LENGTH.isPresent(fish)) tag.putDouble(FishLengthHolder.tide$LENGTH_KEY, length);
                //if (TideItemData.IS_SHINY.isPresent(fish)) tag.putBoolean(ShinyFish.tide$SHINY_KEY, isShiny);
            });
        }
        return newStack;
    }

    @Unique
    private int validBucketAtPositiveIndex(Fluid fluid, TackleBoxChairEntity entity) {
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem() instanceof BucketItem bucketItem
                    && !(bucketItem instanceof MobBucketItem)
                    && getFluid(bucketItem).isSame(fluid)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private Fluid getFluid(BucketItem bucketItem) {
        return ((ContentAccessorBucketItemMixin) bucketItem).getContent();
    }

}