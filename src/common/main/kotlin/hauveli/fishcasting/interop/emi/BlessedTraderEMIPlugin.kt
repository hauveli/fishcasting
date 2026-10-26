package hauveli.fishcasting.interop.emi

import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.features.trader.BlessedEntity
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

        BLESSED_TRADER_TRADES.forEach { (level, listings) ->
            listings.forEachIndexed { index, listing ->
                val offer = listing.getOffer(trader, RandomSource.create()) ?: return@forEachIndexed
                registry.addRecipe(BlessedTraderEMIRecipe(index = index, offer = offer))
            }
        }
    }
}