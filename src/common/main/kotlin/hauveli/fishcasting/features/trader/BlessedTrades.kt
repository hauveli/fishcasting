package hauveli.fishcasting.features.trader

import com.google.common.collect.ImmutableMap
import com.li64.tide.registries.TideFish
import hauveli.fishcasting.registry.FishcastingItems
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import java.util.*

object BlessedTrades {
    private const val DEFAULT_SUPPLY = 12
    private const val COMMON_ITEMS_SUPPLY = 16
    private const val UNCOMMON_ITEMS_SUPPLY = 3
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


    init {
        BLESSED_TRADER_TRADES = toIntMap(
            ImmutableMap.of<Int, Array<VillagerTrades.ItemListing>>( // we always get two of these trades
                1, arrayOf(
                    ItemsForItems(
                        TideFish.SLIMY_SALMON.getDefaultInstance(),
                        true,
                        Items.SLIME_BALL.getDefaultInstance(),
                        2,
                        3,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.DRIPSTONE_DARTER.getDefaultInstance(),
                        true,
                        Items.POINTED_DRIPSTONE.getDefaultInstance(),
                        3,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.CARP.getDefaultInstance(),
                        true,
                        Items.STRING.getDefaultInstance(),
                        2,
                        3,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.MIRAGE_CATFISH.getDefaultInstance(),
                        true,
                        Items.STRING.getDefaultInstance(),
                        4,
                        3,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.DEEP_GROUPER.getDefaultInstance(),
                        4,
                        true,
                        Items.DEEPSLATE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.FROSTBITE_FLOUNDER.getDefaultInstance(),
                        true,
                        Items.BLUE_ICE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.BLOSSOM_BASS.getDefaultInstance(),
                        Items.CHERRY_SAPLING.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.ANGLERFISH.getDefaultInstance(),
                        true,
                        Items.GLOWSTONE_DUST.getDefaultInstance(),
                        2,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.RED_SNAPPER.getDefaultInstance(),
                        true,
                        Items.REDSTONE.getDefaultInstance(),
                        4,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.SAND_TIGER_SHARK.getDefaultInstance(),
                        true,
                        Items.SAND.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.DEEP_BLUE.getDefaultInstance(),
                        true,
                        Items.LEATHER.getDefaultInstance(),
                        2,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.ANGELFISH.getDefaultInstance(),
                        Items.FEATHER.getDefaultInstance(),
                        3,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.INCANDESCENT_LARVA.getDefaultInstance(),
                        Items.END_STONE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    )
                ),  // Rare trades, only one of these will show up per trader
                2, arrayOf<VillagerTrades.ItemListing>(
                    ItemsForItems(
                        TideFish.PENTAPUS.getDefaultInstance(),
                        Items.CHORUS_FLOWER.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.WINDBASS.getDefaultInstance(),
                        true,
                        Items.WIND_CHARGE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.ECHO_SNAPPER.getDefaultInstance(),
                        true,
                        Items.ECHO_SHARD.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.NEPHROSILU.getDefaultInstance(),
                        FishcastingItems.MESSAGE_IN_A_BOTTLE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        FishcastingItems.CURSED.getDefaultInstance(),
                        true,
                        Items.RABBIT_FOOT.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.SPORE_STALKER.getDefaultInstance(),
                        true,
                        Items.BROWN_MUSHROOM.getDefaultInstance(),
                        6,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.SPORE_STALKER.getDefaultInstance(),
                        true,
                        Items.RED_MUSHROOM.getDefaultInstance(),
                        6,
                        1,
                        5,
                        1f
                    )
                ),  // non-overworld trades
                3, arrayOf<VillagerTrades.ItemListing>(
                    ItemsForItems(
                        TideFish.MAGMA_MACKEREL.getDefaultInstance(),
                        true,
                        Items.MAGMA_BLOCK.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.CRIMSON_FANGJAW.getDefaultInstance(),
                        true,
                        Items.CRIMSON_NYLIUM.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.WARPED_GUPPY.getDefaultInstance(),
                        true,
                        Items.WARPED_NYLIUM.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.INFERNO_GUPPY.getDefaultInstance(),
                        Items.LAVA_BUCKET.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.SOULSCALE.getDefaultInstance(),
                        true,
                        Items.SOUL_SAND.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.ENDERFIN.getDefaultInstance(),
                        3,
                        true,
                        Items.ENDER_PEARL.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.ENDERGAZER.getDefaultInstance(),
                        2,
                        true,
                        Items.ENDER_EYE.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.CHORUS_COD.getDefaultInstance(),
                        2,
                        true,
                        Items.CHORUS_FLOWER.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ) // doesn't make sense for alpha fish to be here, player must have access to void to have access to alpha fish
                    // new ItemsForItems(TideFish.ALPHA_FISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1)
                ),  // I really want to have chasm eel be indicative of something to the player, so I hope this will make it more clear...
                4, arrayOf<VillagerTrades.ItemListing>(
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.INK_SAC.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.GRAY_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.LIGHT_GRAY_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.WHITE_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.BLUE_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.BROWN_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.CYAN_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.GREEN_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.LIGHT_BLUE_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.LIME_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.MAGENTA_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.ORANGE_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.PINK_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.PURPLE_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.RED_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.PLUTO_SNAIL.getDefaultInstance(),
                        Items.YELLOW_DYE.getDefaultInstance(),
                        16,
                        1,
                        5,
                        1f
                    ),
                ),  // I really want to have chasm eel be indicative of something to the player, so I hope this will make it more clear...
                5, arrayOf<VillagerTrades.ItemListing>(
                    ItemsForItems(
                        TideFish.DEVILS_HOLE_PUPFISH.getDefaultInstance(),
                        TideFish.CHASM_EEL.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.MIDAS_FISH.getDefaultInstance(),
                        TideFish.CHASM_EEL.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.COELACANTH.getDefaultInstance(),
                        TideFish.CHASM_EEL.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ),
                    ItemsForItems(
                        TideFish.SHOOTING_STARFISH.getDefaultInstance(),
                        TideFish.CHASM_EEL.getDefaultInstance(),
                        1,
                        5,
                        1f
                    ) // doesn't make sense for alpha fish to be here, player must have access to void to have access to alpha fish
                    // new ItemsForItems(TideFish.ALPHA_FISH.getDefaultInstance(), TideFish.CHASM_EEL.getDefaultInstance(), 1, 5, 1)
                )
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
            this.itemStackWant.setCount(wantCount)
            this.wantTool = wantTool
            this.itemStackHave = have
            this.itemStackHave.setCount(haveCount)
            this.maxUses = maxUses
            this.villagerXp = villagerXp
            this.priceMultiplier = priceMultiplier
        }

        override fun getOffer(trader: Entity, random: RandomSource): MerchantOffer? {
            val itemstack = this.itemStackHave.copy()
            if (this.wantTool) {
                return MerchantOffer(
                    ItemCost(itemStackWant.getItem(), itemStackWant.getCount()), Optional.of<ItemCost?>(
                        ItemCost(Items.FLINT)
                    ), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier
                )
            } else {
                return MerchantOffer(
                    ItemCost(itemStackWant.getItem(), itemStackWant.getCount()),
                    itemstack,
                    this.maxUses,
                    this.villagerXp,
                    this.priceMultiplier
                )
            }
        }
    }
}