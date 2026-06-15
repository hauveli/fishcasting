package hauveli.fishcasting.features.gacha

import net.minecraft.network.chat.Component
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ThrowablePotionItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


class GachaBottleItem(properties: Properties) : ThrowablePotionItem(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val itemStack = player.getItemInHand(hand)
        if (!level.isClientSide) {
            val thrownPotion = GachaBottleEntity(level, player)
            thrownPotion.item = itemStack
            thrownPotion.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
            level.addFreshEntity(thrownPotion)
        }

        player.awardStat(Stats.ITEM_USED.get(this))
        itemStack.consume(1, player)
        return InteractionResultHolder.sidedSuccess<ItemStack?>(itemStack, level.isClientSide())
    }

    override fun getDescriptionId(stack: ItemStack): String {
        return this.descriptionId
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        return
    }
}
