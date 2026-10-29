package hauveli.fishcasting.interop.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingConfigs
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.trading.MerchantOffer

class BlessedTraderEMIRecipe(
    private val tradeSlot: Int, // todo: maybe list which slot the item would be in?
    private val offer: MerchantOffer,
    private val chance: Double
) : EmiRecipe {
    // synthetic because no real recipe
    private val recipeId = Fishcasting.id("/${BlessedTraderEmiPlugin.BLESSED_TRADER_CATEGORY_ID.path
    }/${BuiltInRegistries.ITEM.getKey(offer.result.item).path
    }/${BuiltInRegistries.ITEM.getKey(offer.costA.item).path}")

    override fun getCategory(): EmiRecipeCategory = BlessedTraderEmiPlugin.BLESSED_TRADER_CATEGORY
    override fun getId(): ResourceLocation = recipeId

    override fun getInputs(): List<EmiStack> {
        val inputs = mutableListOf<EmiStack>()

        inputs.add(EmiStack.of(offer.baseCostA))
        if (!offer.costB.isEmpty) {
            inputs.add(EmiStack.of(offer.costB))
        }

        return inputs
    }

    override fun getOutputs(): List<EmiStack> = listOf(EmiStack.of(offer.result))

    override fun getDisplayWidth(): Int = 100
    override fun getDisplayHeight(): Int = 30

    // todo: is there a more convenient and similarly verbose way to do this?
    private val TOP_OFFSET = 6
    private val SIDE_OFFSET = 10
    private val ITEM_STACK_SPACING = getDisplayHeight() - TOP_OFFSET

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addSlot(inputs[0], SIDE_OFFSET, TOP_OFFSET)

        var bonusOffset = 0
        if (inputs.size > 1) {
            bonusOffset = ITEM_STACK_SPACING / 2
            widgets.addSlot(inputs[1], SIDE_OFFSET + ITEM_STACK_SPACING, TOP_OFFSET) // seems good enough from testing
        }

        widgets.addText(
            Component.literal("→"),
            2 * ITEM_STACK_SPACING + ITEM_STACK_SPACING / 2 - SIDE_OFFSET + bonusOffset, // from the left to the right, two items and a little bit
            12, // idk how to get the height of the string...
            0xFFFFFF,
            false
        )

        widgets.addText(
            Component.literal("?"),
            - ITEM_STACK_SPACING + SIDE_OFFSET / 2 - 4,
            12, // magic number hell I've lost track, whatever... TODO: un-magic number hell this
            0xFFFFFF,
            false
        )

        val categoryText = "Trade slot: $tradeSlot"
        val chanceText = "${"%.1f".format(chance * 100)}%"

        val font = Minecraft.getInstance().font
        val textX = -ITEM_STACK_SPACING + SIDE_OFFSET / 2

        val widestText = maxOf(font.width(chanceText), font.width(categoryText))

        val tooltipText = mutableListOf(
            Component.literal(categoryText),
            Component.literal("Chance: $chanceText")
        )

        if (tradeSlot == 8) {
            tooltipText.add(Component.literal("Includes listing and item weights"))
            val chance = FishcastingConfigs.COMMON_CONFIG.traderPhialWeights.chanceOf(offer.result) * 100
            tooltipText.add(Component.literal("Chance for this tier to be selected: ${"%.1f".format(chance)}%"))
        }

        val refusalReason = when (tradeSlot) {
            7 -> "completed their journal"
            8 -> "unlocked the great work"
            else -> null
        }

        refusalReason?.let {
            tooltipText.add(
                Component.literal("The trader will refuse to offer this trade if the fisher has not $it")
            )
        }

        widgets.addTooltipText(
            tooltipText.toList(),
            textX - widestText / 2,
            4,
            widestText,
            17
        )
        widgets.addSlot(outputs[0], getDisplayWidth() - ITEM_STACK_SPACING, TOP_OFFSET)
            .recipeContext(this)
    }
}