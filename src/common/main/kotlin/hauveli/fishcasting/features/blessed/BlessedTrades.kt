package hauveli.fishcasting.features.blessed;

import com.google.common.collect.ImmutableMap;
import com.li64.tide.registries.TideFish;
import hauveli.fishcasting.common.registries.FishcastingItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlessedTrades {
    private static final int DEFAULT_SUPPLY = 12;
    private static final int COMMON_ITEMS_SUPPLY = 16;
    private static final int UNCOMMON_ITEMS_SUPPLY = 3;
    private static final int XP_LEVEL_1_SELL = 1;
    private static final int XP_LEVEL_1_BUY = 2;
    private static final int XP_LEVEL_2_SELL = 5;
    private static final int XP_LEVEL_2_BUY = 10;
    private static final int XP_LEVEL_3_SELL = 10;
    private static final int XP_LEVEL_3_BUY = 20;
    private static final int XP_LEVEL_4_SELL = 15;
    private static final int XP_LEVEL_4_BUY = 30;
    private static final int XP_LEVEL_5_TRADE = 30;
    private static final float LOW_TIER_PRICE_MULTIPLIER = 0.05F;
    private static final float HIGH_TIER_PRICE_MULTIPLIER = 0.2F;
    public static final Int2ObjectMap<net.minecraft.world.entity.npc.VillagerTrades.ItemListing[]> BLESSED_TRADER_TRADES;

    private static Int2ObjectMap<net.minecraft.world.entity.npc.VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, net.minecraft.world.entity.npc.VillagerTrades.ItemListing[]> map) {
        return new Int2ObjectOpenHashMap(map);
    }


    static {
        BLESSED_TRADER_TRADES = toIntMap(ImmutableMap.of(
                // we always get two of these trades
                1, new VillagerTrades.ItemListing[]{
                        new ItemsForItems(TideFish.SLIMY_SALMON.getDefaultInstance(), true, Items.SLIME_BALL.getDefaultInstance(), 2, 3, 5, 1),
                        new ItemsForItems(TideFish.DRIPSTONE_DARTER.getDefaultInstance(), true, Items.POINTED_DRIPSTONE.getDefaultInstance(), 3, 1, 5, 1),
                        new ItemsForItems(TideFish.CARP.getDefaultInstance(), true, Items.STRING.getDefaultInstance(), 2, 3, 5, 1),
                        new ItemsForItems(TideFish.MIRAGE_CATFISH.getDefaultInstance(), true, Items.STRING.getDefaultInstance(), 4, 3, 5, 1),
                        new ItemsForItems(TideFish.DEEP_GROUPER.getDefaultInstance(), 4, true, Items.DEEPSLATE.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.FROSTBITE_FLOUNDER.getDefaultInstance(), true, Items.BLUE_ICE.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.BLOSSOM_BASS.getDefaultInstance(), Items.CHERRY_SAPLING.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.ANGLERFISH.getDefaultInstance(), true, Items.GLOWSTONE_DUST.getDefaultInstance(), 2, 1, 5, 1),
                        new ItemsForItems(TideFish.RED_SNAPPER.getDefaultInstance(), true, Items.REDSTONE.getDefaultInstance(), 4, 1, 5, 1),
                        new ItemsForItems(TideFish.SAND_TIGER_SHARK.getDefaultInstance(), true, Items.SAND.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.DEEP_BLUE.getDefaultInstance(), true, Items.LEATHER.getDefaultInstance(), 2, 1, 5, 1),
                        new ItemsForItems(TideFish.ANGELFISH.getDefaultInstance(), Items.FEATHER.getDefaultInstance(), 3, 1, 5, 1),
                        new ItemsForItems(TideFish.INCANDESCENT_LARVA.getDefaultInstance(), Items.END_STONE.getDefaultInstance(), 1, 5, 1)
                },
                // Rare trades, only one of these will show up per trader
                2, new VillagerTrades.ItemListing[]{
                        new ItemsForItems(TideFish.PENTAPUS.getDefaultInstance(), Items.CHORUS_FLOWER.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.WINDBASS.getDefaultInstance(), true, Items.WIND_CHARGE.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.ECHO_SNAPPER.getDefaultInstance(), true, Items.ECHO_SHARD.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.NEPHROSILU.getDefaultInstance(), FishcastingItems.MESSAGE_IN_A_BOTTLE.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(FishcastingItems.CURSED.getDefaultInstance(), true, Items.RABBIT_FOOT.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.SPORE_STALKER.getDefaultInstance(), true, Items.BROWN_MUSHROOM.getDefaultInstance(), 6, 1, 5, 1),
                        new ItemsForItems(TideFish.SPORE_STALKER.getDefaultInstance(), true, Items.RED_MUSHROOM.getDefaultInstance(), 6, 1, 5, 1)
                },
                // non-overworld trades
                3, new VillagerTrades.ItemListing[]{
                        new ItemsForItems(TideFish.MAGMA_MACKEREL.getDefaultInstance(), true, Items.MAGMA_BLOCK.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.CRIMSON_FANGJAW.getDefaultInstance(), true, Items.CRIMSON_NYLIUM.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.WARPED_GUPPY.getDefaultInstance(), true, Items.WARPED_NYLIUM.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.INFERNO_GUPPY.getDefaultInstance(), Items.LAVA_BUCKET.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.SOULSCALE.getDefaultInstance(), true, Items.SOUL_SAND.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.ENDERFIN.getDefaultInstance(), 3, true, Items.ENDER_PEARL.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.ENDERGAZER.getDefaultInstance(), 2, true, Items.ENDER_EYE.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.CHORUS_COD.getDefaultInstance(), 2, true, Items.CHORUS_FLOWER.getDefaultInstance(), 1, 5, 1)
                        // doesn't make sense for alpha fish to be here, player must have access to void to have access to alpha fish
                        // new ItemsForItems(TideFish.ALPHA_FISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1)
                },
                // I really want to have chasm eel be indicative of something to the player, so I hope this will make it more clear...
                4, new VillagerTrades.ItemListing[]{
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.INK_SAC.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.GRAY_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.LIGHT_GRAY_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.WHITE_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.BLUE_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.BROWN_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.CYAN_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.GREEN_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.LIGHT_BLUE_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.LIME_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.MAGENTA_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.ORANGE_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.PINK_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.PURPLE_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.RED_DYE.getDefaultInstance(), 16, 1, 5, 1),
                        new ItemsForItems(TideFish.PLUTO_SNAIL.getDefaultInstance(), Items.YELLOW_DYE.getDefaultInstance(), 16, 1, 5, 1),
                },
                // I really want to have chasm eel be indicative of something to the player, so I hope this will make it more clear...
                5, new VillagerTrades.ItemListing[]{
                        new ItemsForItems(TideFish.DEVILS_HOLE_PUPFISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.MIDAS_FISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.COELACANTH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1),
                        new ItemsForItems(TideFish.SHOOTING_STARFISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1)
                        // doesn't make sense for alpha fish to be here, player must have access to void to have access to alpha fish
                        // new ItemsForItems(TideFish.ALPHA_FISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1)
                }
        ));
    }

    static class ItemsAndEmeraldsToItems implements net.minecraft.world.entity.npc.VillagerTrades.ItemListing {
        private final ItemCost fromItem;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;
        private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;

        public ItemsAndEmeraldsToItems(ItemLike fromItem, int fromItemCount, int emeraldCost, Item toItem, int toItemCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(fromItem, fromItemCount, emeraldCost, new ItemStack(toItem), toItemCount, maxUses, villagerXp, priceMultiplier);
        }

        private ItemsAndEmeraldsToItems(ItemLike fromItem, int fromItemCount, int emeraldCost, ItemStack toItem, int toItemCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(new ItemCost(fromItem, fromItemCount), emeraldCost, toItem.copyWithCount(toItemCount), maxUses, villagerXp, priceMultiplier, Optional.empty());
        }

        ItemsAndEmeraldsToItems(ItemLike fromItem, int fromItemAmount, int emeraldCost, ItemLike toItem, int toItemCount, int maxUses, int villagerXp, float priceMultiplier, ResourceKey<EnchantmentProvider> enchantmentProvider) {
            this(new ItemCost(fromItem, fromItemAmount), emeraldCost, new ItemStack(toItem, toItemCount), maxUses, villagerXp, priceMultiplier, Optional.of(enchantmentProvider));
        }

        public ItemsAndEmeraldsToItems(ItemCost fromItem, int emeraldCost, ItemStack toItem, int maxUses, int villagerXp, float priceMultiplier, Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider) {
            this.fromItem = fromItem;
            this.emeraldCost = emeraldCost;
            this.toItem = toItem;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.enchantmentProvider = enchantmentProvider;
        }

        @Nullable
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            ItemStack itemstack = this.toItem.copy();
            Level level = trader.level();
            this.enchantmentProvider.ifPresent((p_348335_) -> EnchantmentHelper.enchantItemFromProvider(itemstack, level.registryAccess(), p_348335_, level.getCurrentDifficultyAt(trader.blockPosition()), random));
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(this.fromItem), itemstack, 0, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    public static class ItemsForItems implements VillagerTrades.ItemListing {
        private final ItemStack itemStackHave;
        private final ItemStack itemStackWant;
        private final boolean wantTool;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        // without tool
        public ItemsForItems(ItemStack want, ItemStack have) {
            this(want, have, 0);
        }

        public ItemsForItems(ItemStack want, ItemStack have, int villagerXp) {
            this(want, have, 1, villagerXp);
        }

        public ItemsForItems(ItemStack want, ItemStack have, int maxUses, int villagerXp) {
            this(want, have, maxUses, villagerXp, 1.0f);
        }

        public ItemsForItems(ItemStack want, ItemStack have, int haveCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, 1, false, have, haveCount, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForItems(ItemStack want, int wantCount, ItemStack have, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, wantCount, false, have, 1, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForItems(ItemStack want, ItemStack have, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, 1, false, have, 1, maxUses, villagerXp, priceMultiplier);
        }

        // With tool
        public ItemsForItems(ItemStack want, boolean wantTool, ItemStack have) {
            this(want, wantTool, have, 0);
        }

        public ItemsForItems(ItemStack want, boolean wantTool, ItemStack have, int villagerXp) {
            this(want, wantTool, have, 1, villagerXp);
        }

        public ItemsForItems(ItemStack want, boolean wantTool, ItemStack have, int maxUses, int villagerXp) {
            this(want, wantTool, have, maxUses, villagerXp, 1.0f);
        }

        public ItemsForItems(ItemStack want, boolean wantTool, ItemStack have, int haveCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, 1, wantTool, have, haveCount, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForItems(ItemStack want, int wantCount, boolean wantTool, ItemStack have, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, wantCount, wantTool, have, 1, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForItems(ItemStack want, boolean wantTool, ItemStack have, int maxUses, int villagerXp, float priceMultiplier) {
            this(want, 1, wantTool, have, 1, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForItems(ItemStack want, int wantCount, boolean wantTool, ItemStack have, int haveCount, int maxUses, int villagerXp, float priceMultiplier) {
            this.itemStackWant = want;
            this.itemStackWant.setCount(wantCount);
            this.wantTool = wantTool;
            this.itemStackHave = have;
            this.itemStackHave.setCount(haveCount);
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            ItemStack itemstack = this.itemStackHave.copy();
            if (this.wantTool) {
                return new MerchantOffer(new ItemCost(itemStackWant.getItem(), itemStackWant.getCount()), Optional.of(new ItemCost(Items.FLINT)), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
            } else {
                return new MerchantOffer(new ItemCost(itemStackWant.getItem(), itemStackWant.getCount()), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
            }

        }
    }
}
