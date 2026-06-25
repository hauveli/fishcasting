package hauveli.fishcasting.features.trader

import com.google.common.collect.ImmutableMap
import com.li64.tide.registries.TideFish
import hauveli.fishcasting.registry.FishcastingItems
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import java.util.*
import kotlin.math.ln

object BlessedTrades {
    private const val COMMON_ITEMS_SUPPLY = 16
    private const val DIM_ITEMS_SUPPLY = 12
    private const val RARE_ITEMS_SUPPLY = 2
    private const val DYE_ITEMS_SUPPLY = 4
    private const val CHASM_EEL_SUPPLY = 3
    private const val XP_LEVEL_1_SELL = 1
    private const val XP_LEVEL_1_BUY = 2
    private const val XP_LEVEL_2_SELL = 5
    private const val XP_LEVEL_2_BUY = 10
    private const val XP_LEVEL_3_SELL = 10
    private const val XP_LEVEL_3_BUY = 20
    private const val XP_LEVEL_4_SELL = 15
    private const val XP_LEVEL_4_BUY = 30
    private const val XP_LEVEL_5_TRADE = 30
    private const val LOW_TIER_PRICE_MULTIPLIER = 0.05f
    private const val HIGH_TIER_PRICE_MULTIPLIER = 0.2f
    val BLESSED_TRADER_TRADES: Int2ObjectMap<Array<VillagerTrades.ItemListing>>

    private fun toIntMap(map: ImmutableMap<Int, Array<VillagerTrades.ItemListing>>): Int2ObjectMap<Array<VillagerTrades.ItemListing>> {
        return Int2ObjectOpenHashMap(map)
    }

    private fun fishTrade(
        fish: Item,
        item: Item,
        count: Int = 1,
        wantTool: Boolean = true,
        maxUses: Int = COMMON_ITEMS_SUPPLY
    ): VillagerTrades.ItemListing =
        ItemsForItems(
            fish.defaultInstance,
            wantTool,
            item.defaultInstance,
            count,
            maxUses,
            5,
            1f
        )

    private fun rareFishTrade(
        fish: Item,
        item: Item,
        count: Int = 1,
        wantTool: Boolean = true,
        maxUses: Int = RARE_ITEMS_SUPPLY
    ): VillagerTrades.ItemListing =
        fishTrade(fish, item, count, wantTool, maxUses)

    private fun dimensionalFishTrade(
        fish: Item,
        item: Item,
        count: Int = 1,
        wantTool: Boolean = true,
        maxUses: Int = DIM_ITEMS_SUPPLY
    ): VillagerTrades.ItemListing =
        fishTrade(fish, item, count, wantTool, maxUses)

    private val COMMON_FISH_TRADES = arrayOf(
        fishTrade(Items.TADPOLE_BUCKET, Items.EMERALD, 2, false),
        fishTrade(Items.FROGSPAWN, Items.EMERALD, 2, false),
        fishTrade(TideFish.SLIMY_SALMON, Items.SLIME_BALL, 2),
        fishTrade(TideFish.DRIPSTONE_DARTER, Items.POINTED_DRIPSTONE, 3),
        fishTrade(TideFish.CARP, Items.STRING, 2),
        fishTrade(TideFish.MIRAGE_CATFISH, Items.STRING, 4),
        fishTrade(TideFish.DEEP_GROUPER, Items.DEEPSLATE, 4),
        fishTrade(TideFish.FROSTBITE_FLOUNDER, Items.BLUE_ICE),
        fishTrade(TideFish.BLOSSOM_BASS, Items.CHERRY_SAPLING, wantTool = false),
        fishTrade(TideFish.ANGLERFISH, Items.GLOWSTONE_DUST, 2),
        fishTrade(TideFish.RED_SNAPPER, Items.REDSTONE, 4),
        fishTrade(TideFish.SAND_TIGER_SHARK, Items.SAND, 16),
        fishTrade(TideFish.DEEP_BLUE, Items.LEATHER, 2),
        fishTrade(TideFish.ANGELFISH, Items.FEATHER, 3, false),
        fishTrade(TideFish.INCANDESCENT_LARVA, Items.END_STONE, wantTool = false)
    )

    private val RARE_FISH_TRADES = arrayOf<VillagerTrades.ItemListing>(
        rareFishTrade(TideFish.PENTAPUS, Items.CHORUS_FLOWER, wantTool = false),
        rareFishTrade(TideFish.WINDBASS, Items.WIND_CHARGE, 8),
        rareFishTrade(TideFish.ECHO_SNAPPER, Items.ECHO_SHARD),
        rareFishTrade(TideFish.NEPHROSILU, FishcastingItems.MESSAGE_IN_A_BOTTLE, wantTool = false),
        rareFishTrade(FishcastingItems.CURSED, Items.RABBIT_FOOT),
        rareFishTrade(TideFish.SPORE_STALKER, Items.BROWN_MUSHROOM, 6),
        rareFishTrade(TideFish.SPORE_STALKER, Items.RED_MUSHROOM, 6)
    )

    private val NETHER_AND_END_TRADES = arrayOf<VillagerTrades.ItemListing>(
        dimensionalFishTrade(TideFish.MAGMA_MACKEREL, Items.MAGMA_BLOCK),
        dimensionalFishTrade(TideFish.CRIMSON_FANGJAW, Items.CRIMSON_NYLIUM),
        dimensionalFishTrade(TideFish.WARPED_GUPPY, Items.WARPED_NYLIUM),
        dimensionalFishTrade(TideFish.INFERNO_GUPPY, Items.LAVA_BUCKET, wantTool = false),
        dimensionalFishTrade(TideFish.SOULSCALE, Items.SOUL_SAND),
        dimensionalFishTrade(TideFish.ENDERFIN, Items.ENDER_PEARL, 3),
        dimensionalFishTrade(TideFish.ENDERGAZER, Items.ENDER_EYE, 2),
        dimensionalFishTrade(TideFish.CHORUS_COD, Items.CHORUS_FLOWER, 2),
    )

    private fun itemsForDyes(itemStackHave: ItemStack): VillagerTrades.ItemListing {
        return ItemsForItems(TideFish.PLUTO_SNAIL.defaultInstance, itemStackHave,
            DYE_ITEMS_SUPPLY, 5)
    }

    private val ALL_DYES: TagKey<Item?> = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes"))

    private val ALL_DYE_TRADES: Array<VillagerTrades.ItemListing> =
        BuiltInRegistries.ITEM.getTag(ALL_DYES)
            .orElseThrow()
            .map { holder ->
                itemsForDyes(holder.value().defaultInstance)
            }
            .toTypedArray()

    private val CHASM_EEL_TRADES = listOf(
        TideFish.DEVILS_HOLE_PUPFISH,
        TideFish.MIDAS_FISH,
        TideFish.COELACANTH,
        TideFish.SHOOTING_STARFISH
    )

    private fun itemsForChasmEel(itemStackWant: ItemStack): ItemsForItems {
        return ItemsForItems(itemStackWant,
            TideFish.CHASM_EEL.defaultInstance,
            CHASM_EEL_SUPPLY, 5)
    }

    private val ALL_CHASM_EEL_TRADES: Array<VillagerTrades.ItemListing> =
        CHASM_EEL_TRADES
            .map { fish -> itemsForChasmEel(fish.defaultInstance) }
            .toTypedArray()

    init {
        BLESSED_TRADER_TRADES = toIntMap(
            ImmutableMap.of<Int, Array<VillagerTrades.ItemListing>>(
                1, COMMON_FISH_TRADES,
                2, RARE_FISH_TRADES,
                3, NETHER_AND_END_TRADES,
                4, ALL_DYE_TRADES,
                5, ALL_CHASM_EEL_TRADES
            )
        )
    }

    class ItemsForItems(
        private val itemStackWant: ItemStack,
        wantCount: Int,
        wantTool: Boolean,
        have: ItemStack,
        haveCount: Int,
        maxUses: Int,
        villagerXp: Int,
        priceMultiplier: Float
    ) : VillagerTrades.ItemListing {
        private val itemStackHave: ItemStack
        private val wantTool: Boolean
        private val maxUses: Int
        private val villagerXp: Int
        private val priceMultiplier: Float

        // without tool
        @JvmOverloads
        constructor(want: ItemStack, have: ItemStack, villagerXp: Int = 0) : this(want, have, 1, villagerXp)

        constructor(
            want: ItemStack,
            have: ItemStack,
            haveCount: Int,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float
        ) : this(want, 1, false, have, haveCount, maxUses, villagerXp, priceMultiplier)

        constructor(
            want: ItemStack,
            wantCount: Int,
            have: ItemStack,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float
        ) : this(want, wantCount, false, have, 1, maxUses, villagerXp, priceMultiplier)

        @JvmOverloads
        constructor(
            want: ItemStack,
            have: ItemStack,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float = 1.0f
        ) : this(want, 1, false, have, 1, maxUses, villagerXp, priceMultiplier)

        // With tool
        @JvmOverloads
        constructor(want: ItemStack, wantTool: Boolean, have: ItemStack, villagerXp: Int = 0) : this(
            want,
            wantTool,
            have,
            1,
            villagerXp
        )

        constructor(
            want: ItemStack,
            wantTool: Boolean,
            have: ItemStack,
            haveCount: Int,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float
        ) : this(want, 1, wantTool, have, haveCount, maxUses, villagerXp, priceMultiplier)

        constructor(
            want: ItemStack,
            wantCount: Int,
            wantTool: Boolean,
            have: ItemStack,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float
        ) : this(want, wantCount, wantTool, have, 1, maxUses, villagerXp, priceMultiplier)

        @JvmOverloads
        constructor(
            want: ItemStack,
            wantTool: Boolean,
            have: ItemStack,
            maxUses: Int,
            villagerXp: Int,
            priceMultiplier: Float = 1.0f
        ) : this(want, 1, wantTool, have, 1, maxUses, villagerXp, priceMultiplier)

        init {
            this.itemStackWant.count = wantCount
            this.wantTool = wantTool
            this.itemStackHave = have
            this.itemStackHave.count = haveCount
            this.maxUses = maxUses
            this.villagerXp = villagerXp
            this.priceMultiplier = priceMultiplier
        }

        override fun getOffer(trader: Entity, random: RandomSource): MerchantOffer {
            val itemstack = this.itemStackHave.copy()
            val blessed = trader as BlessedEntity
            val priceMultActual: Float = (1 + ln(1 + BlessedEntity.Mood.VERY_HAPPY.value - blessed.mood.value.toDouble())).toFloat()
            if (this.wantTool) {
                return MerchantOffer(
                    ItemCost(itemStackWant.item, itemStackWant.count), Optional.of(
                        ItemCost(Items.FLINT)
                    ), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier * priceMultActual
                )
            } else {
                return MerchantOffer(
                    ItemCost(itemStackWant.item, itemStackWant.count),
                    itemstack,
                    this.maxUses,
                    this.villagerXp,
                    this.priceMultiplier * priceMultActual
                )
            }
        }
    }
}