package hauveli.fishcasting.interop.emi

import at.petrak.hexcasting.common.items.magic.ItemMediaBattery
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import at.petrak.hexcasting.common.lib.HexItems
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingConfigs
import hauveli.fishcasting.features.trader.BlessedEntity
import hauveli.fishcasting.features.trader.BlessedTrades
import hauveli.fishcasting.features.trader.BlessedTrades.BLESSED_TRADER_TRADES
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.client.Minecraft
import net.minecraft.util.RandomSource
import net.minecraft.world.item.Items

object BlessedTraderEmiPlugin {
    val BLESSED_TRADER_CATEGORY_ID = FishcastingEntities.BLESSED.id

    val BLESSED_TRADER_CATEGORY = EmiRecipeCategory(
        BLESSED_TRADER_CATEGORY_ID,
        EmiStack.of(FishcastingItems.LOUD_FISHING_LINE.value.defaultInstance)
    )

    fun registerTrades(registry: EmiRegistry) {
        registry.addCategory(BLESSED_TRADER_CATEGORY)
        registry.addWorkstation(
            BLESSED_TRADER_CATEGORY,
            EmiStack.of(FishcastingItems.BLESSED_SPAWN_EGG.value.defaultInstance)
        )

        val level = Minecraft.getInstance().level ?: return
        val trader = BlessedEntity(FishcastingEntities.BLESSED.value, level)


        // my head hurts from staying up so I will just do this and blame my past self in the future if this was a bad idea


        BLESSED_TRADER_TRADES.forEach { (tradeSlotButAlsoLevelIfItWasNormal, listings) ->
            listings.forEachIndexed { index, listing ->
                // todo: is this the best way to get the offer?
                val offer = listing.getOffer(trader, RandomSource.create()) ?: return@forEachIndexed

                val listingsSize = listings.size
                var listingChance = 1.0 / listingsSize
                // I should have thought of a better way to do this, probably...
                // Because I use the config to modify weights, I can't just calculate the chance as above...
                if (offer.result.item is ItemMediaBattery) {
                    val phialChance = FishcastingConfigs.COMMON_CONFIG.traderPhialWeights.chanceOf(offer.result)
                    val fishChance = FishcastingConfigs.COMMON_CONFIG.traderPhialWeights.chanceOfFish(offer.result)
                    listingChance = phialChance * fishChance
                }

                registry.addRecipe(BlessedTraderEMIRecipe(tradeSlotButAlsoLevelIfItWasNormal, offer, listingChance))
            }
        }
    }
}