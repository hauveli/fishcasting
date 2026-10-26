package hauveli.fishcasting.mixin.tacklebox_chair;

import com.li64.tide.Tide;
import com.li64.tide.client.TideClientHelper;
import com.li64.tide.config.TideConfig;
import com.li64.tide.config.TideServerConfig;
import com.li64.tide.data.FishLengthHolder;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.item.TideDataComponents;
import com.li64.tide.data.item.TideItemData;
import com.li64.tide.network.messages.ShowToastMsg;
import com.li64.tide.util.TideUtils;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.features.chair.TackleBoxChairEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

//import static com.li64.tide.data.item.TideItemData.CATCH_TIMESTAMP;
import static com.li64.tide.client.TideClientHelper.showToast;
import static com.li64.tide.data.item.TideItemData.FISH_LENGTH;
import static hauveli.fishcasting.registry.FishcastingAdvancements.tryGrantingAdvancement;
import static hauveli.fishcasting.registry.FishcastingAdvancements.TACKLEBOX_CHAIR_FISHER;

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

    // I would like to mixin to something better if I knew any... It is currently quite jarring because the plop animation doesn't play
    @Inject(
            method = "playerTouch",
            at = @At("HEAD"),
            cancellable = true)
    private void fishcasting$onTake(Player player, CallbackInfo ci) {
        if (!(player.getVehicle() instanceof TackleBoxChairEntity tackleBoxChairEntity))
            return;
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        if (!itemEntity.getTags().contains(Fishcasting.FISHBERT_TAG)
                && itemEntity.hasPickUpDelay()) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        // only consider recently caught fish, implicitly these are always alive(?), but this prevents
        // some behaviours I'm not sure I'd like but I may change the maximumFishAgeForAutomaticBoxing variable
        //if (CATCH_TIMESTAMP.get(stack) != null) {
        //if (Minecraft.getInstance().level.getDayTime() < CATCH_TIMESTAMP.get(stack) + maximumFishAgeForAutomaticBoxing) {
        int targetBucketSlot = -1; // remains -1 if no valid slot is found or config disallows
        // Yippee?
        if (Tide.CONFIG.server().items.bucketableFishItems != TideServerConfig.Items.BucketableMode.NEVER) {
            // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/mixin/ItemMixin.java#L38
            Optional<FishData> dataOp = FishData.get(stack);
            if (dataOp.isEmpty())
                return; // skedaddle, I don't know when this could happen but might as well
            FishData fishData = dataOp.get();
            // bucket only if config allows it
            if (fishData.bucket().get().value() instanceof BucketItem fishBucketItem) {
                Fluid fluid = fishcasting$getFluid(fishBucketItem);
                targetBucketSlot = fishcasting$validBucketAtPositiveIndex(fluid, tackleBoxChairEntity);
                if (targetBucketSlot != -1) {
                    stack = fishcasting$bucketedFishFromFish(fishData, stack);
                }
            }
        }
        if (targetBucketSlot == -1) {
            // emergency exit if we don't have enough inventory space
            // merges fish if possible BEFORE the new fish is added...
            if (fishcasting$hasNoFreeSlots(tackleBoxChairEntity)) {
                return;
            }
            // kills any fish not meeting the criteria
            fishcasting$killSmallerFish(stack, tackleBoxChairEntity);
            for (int slotIndex = 0; slotIndex < tackleBoxChairEntity.getContainerSize(); slotIndex++) {
                ItemStack slotStack = tackleBoxChairEntity.getItem(slotIndex);
                if (!slotStack.isEmpty()) continue;

                fishcasting$finalizeSlurp(tackleBoxChairEntity, slotIndex, stack,
                        player, itemEntity, ci);
                break;
            }
        } else {
            fishcasting$finalizeSlurp(tackleBoxChairEntity, targetBucketSlot, stack,
                    player, itemEntity, ci);
        }
    }

    @Unique
    private static void fishcasting$finalizeSlurp(
            TackleBoxChairEntity tackleBoxChairEntity, int targetSlot, ItemStack itemStack,
            Player player, ItemEntity itemEntity, CallbackInfo ci) {

        boolean isBucketed = itemStack.getItem() instanceof MobBucketItem;
        player.playSound(isBucketed ?
                SoundEvents.BUCKET_FILL_FISH : SoundEvents.BUCKET_EMPTY_FISH, 1.0f, 1.0f);

        tackleBoxChairEntity.setItem(targetSlot, itemStack);

        if (player instanceof ServerPlayer serverPlayer) {
            fishcasting$showFishPopUp(tackleBoxChairEntity, itemStack, itemEntity.getItem(), serverPlayer);
            tryGrantingAdvancement(serverPlayer, TACKLEBOX_CHAIR_FISHER);
        }

        itemEntity.discard();
        ci.cancel();

    }

    // todo: is this something I should also add a server config for?
    // adding a client config would possibly be preferable, but because I'm taking a gross shortcut by relying on Tide's stuff
    // it means doing so would be simpler (and better) if I add my own toast system...
    @Unique
    private static void fishcasting$showFishPopUp(TackleBoxChairEntity tackleBoxChairEntity, ItemStack displayItemStack, ItemStack lengthItemStack, ServerPlayer player) {
        if (player == null) return;
        // rate limit...
        if (tackleBoxChairEntity.getNotificationsThisTick() > 2) return;
        String fishName = TideUtils.removeRawTextInName(displayItemStack.getHoverName()).getString();
        if (displayItemStack.getItem() instanceof MobBucketItem mobBucketItem) {
            String toReplace = Component.translatable("fishcasting.remove_from_names.bucket").getString();
            fishName = fishName.replace(toReplace, "").trim();
        }
        Tide.NETWORK.sendToPlayer(new ShowToastMsg(
                Component.nullToEmpty(
                        Component.translatable("fishcasting.toast.tacklebox_chair.fish_boxed").getString() +
                                " - " + TideUtils.getFormattedLength(lengthItemStack).getString()
                ),
                Component.nullToEmpty(fishName),
                displayItemStack), player);

        tackleBoxChairEntity.setNotificationsThisTick(tackleBoxChairEntity.getNotificationsThisTick() + 1);
    }

    // I should try to tidy up inside this
    @Unique
    private static boolean fishcasting$hasNoFreeSlots(TackleBoxChairEntity entity) {
        // most likely case is that ther eis a slot free...
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.isEmpty()) {
                return false;
            }
        }
        // but if there isn't I should probably wait until I'm sure to try freeing up a slot...
        // and because I only need to free up one slot at a time, I can just return asap after one slot is freed up...
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            int maxStackSize = slotStack.getMaxStackSize();
            int slotStackCount = slotStack.getCount();
            if (maxStackSize == slotStackCount) continue;
            for (int j = i + 1; j < entity.getContainerSize(); j++) {
                ItemStack other = entity.getItem(j);
                if (maxStackSize == other.getCount()) continue; // if the items are the same, this implicitly is true when I want to continue anyway, so I don't need to call .getMaxStackSize() on other
                if (!ItemStack.isSameItemSameComponents(slotStack, other)) continue;

                int transfer = Math.min(maxStackSize - slotStackCount, other.getCount());
                slotStack.grow(transfer);
                other.shrink(transfer);

                if (other.isEmpty()) {
                    entity.setItem(j, ItemStack.EMPTY);
                    return false;
                }
            }
        }

        // if there somehow STILL are no free slots, give up...
        return true;
    }

    @Unique
    private static void fishcasting$killSmallerFish(ItemStack fishToDeposit, TackleBoxChairEntity entity) {
        TackleBoxChairEntity.FishKillSetting setting = entity.getFishKillSetting();
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem().equals(fishToDeposit.getItem())
                    && FISH_LENGTH.isPresent(slotStack) && FISH_LENGTH.isPresent(fishToDeposit)) {

                double existingLength = FISH_LENGTH.get(slotStack);
                double incomingLength = FISH_LENGTH.get(fishToDeposit);

                boolean keepIncoming = switch (setting) {
                    case KEEP_MAX -> incomingLength > existingLength;
                    case KEEP_MIN -> incomingLength < existingLength;
                    case KEEP_NONE -> false;
                    case KEEP_ALL -> true;
                };

                if (keepIncoming) {
                    slotStack.remove(TideDataComponents.FISH_LENGTH);
                    slotStack.remove(TideDataComponents.IS_BUCKETABLE);
                } else {
                    fishToDeposit.remove(TideDataComponents.FISH_LENGTH);
                    fishToDeposit.remove(TideDataComponents.IS_BUCKETABLE);
                }
            }
        }
    }

    // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/mixin/ItemMixin.java#L76C19-L76
    @Unique
    private ItemStack fishcasting$bucketedFishFromFish(FishData data, ItemStack fish) {
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
    private static int fishcasting$validBucketAtPositiveIndex(Fluid fluid, TackleBoxChairEntity entity) {
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack slotStack = entity.getItem(i);
            if (slotStack.getItem() instanceof BucketItem bucketItem
                    && !(bucketItem instanceof MobBucketItem)
                    && fishcasting$getFluid(bucketItem).isSame(fluid)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static Fluid fishcasting$getFluid(BucketItem bucketItem) {
        return ((ContentAccessorBucketItemMixin) bucketItem).getContent();
    }
}