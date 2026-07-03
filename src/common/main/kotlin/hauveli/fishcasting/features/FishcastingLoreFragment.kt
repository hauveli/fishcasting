package hauveli.fishcasting.features

import at.petrak.hexcasting.common.items.ItemLoreFragment
import at.petrak.hexcasting.common.lib.HexSounds
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*
import java.util.List


// From hexcasting repo https://github.com/FallingColors/HexMod/blob/main/Common/src/main/java/at/petrak/hexcasting/common/items/ItemLoreFragment.java
class FishcastingLoreFragment(properties: Properties?) : ItemLoreFragment(properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        player.playSound(HexSounds.READ_LORE_FRAGMENT, 1f, 1f)

        val handStack = player.getItemInHand(usedHand)
        if (player !is ServerPlayer) {
            handStack.shrink(1)
            return InteractionResultHolder.success<ItemStack?>(handStack)
        }

        var unfoundLore: AdvancementHolder? = null
        val shuffled = ArrayList(NAMES)
        shuffled.shuffle()
        for (advID in shuffled) {
            val adv = player.server.advancements.get(advID) ?: continue  // uh oh

            if (!player.advancements.getOrStartProgress(adv).isDone) {
                unfoundLore = adv
                break
            }
        }

        if (unfoundLore == null) {
            player.displayClientMessage(Component.translatable("item.hexcasting.lore_fragment.all"), true)
            player.giveExperiencePoints(20)
            level.playSound(
                null, player.position().x, player.position().y, player.position().z,
                HexSounds.READ_LORE_FRAGMENT, SoundSource.PLAYERS, 1f, 1f
            )
        } else {
            // et voila!
            player.advancements.award(unfoundLore, CRITEREON_KEY)
        }

        CriteriaTriggers.CONSUME_ITEM.trigger(player, handStack)
        player.awardStat(Stats.ITEM_USED.get(this))
        handStack.shrink(1)

        return InteractionResultHolder.success<ItemStack?>(handStack)
    }

    companion object {
        val NAMES: MutableList<ResourceLocation> = mutableListOf(
            id("lore/newsletter"),
            id("lore/newsletter2"),
            id("lore/notes"),
            id("lore/notes2"),
            id("lore/notes3"),
            id("lore/notes4")
        )

        const val CRITEREON_KEY: String = "grant"
    }
}