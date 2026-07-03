package hauveli.fishcasting.registry

import com.li64.tide.Tide
import com.li64.tide.data.TideData
import hauveli.fishcasting.Fishcasting
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item


object FishcastingAdvancements {
    @JvmField
    val TACKLEBOX_CHAIR_FISHER = Fishcasting.id("instructional/fished_in_tacklebox_chair")
    @JvmField
    val BLESSED_BRAINSWEPT = Fishcasting.id("instructional/brainswept_trader_in_void")
    val CURSED_ZAPPED = Fishcasting.id("instructional/lightning_struck_cursed")


    val BLESSED_BRAINSWEPT_CLUED = Fishcasting.id("lore/notes3")
    val CURSED_ZAPPED_CLUED = Fishcasting.id("lore/notes4")

    val BLESSED_BRAINSWEPT_CLUELESS = Fishcasting.id("hint/voidsweep")
    val CURSED_ZAPPED_CLUELESS = Fishcasting.id("hint/annihilation")

    val dependents = mapOf(
        BLESSED_BRAINSWEPT to BLESSED_BRAINSWEPT_CLUED,
        BLESSED_BRAINSWEPT_CLUED to BLESSED_BRAINSWEPT_CLUELESS,

        CURSED_ZAPPED to CURSED_ZAPPED_CLUED,
        CURSED_ZAPPED_CLUED to CURSED_ZAPPED_CLUELESS,
    )

    @JvmStatic
    fun tryGrantingAdvancement(serverPlayer: ServerPlayer, advancementResLoc: ResourceLocation) {
        val advancement: AdvancementHolder = checkNotNull(serverPlayer.server.advancements.get(advancementResLoc))
        val progress = serverPlayer.advancements.getOrStartProgress(advancement)
        if (progress.isDone) return
        for (criterion in progress.remainingCriteria) {
            serverPlayer.advancements.award(advancement, criterion)
        }
    }

    @JvmStatic
    fun tryRevokingAdvancement(serverPlayer: ServerPlayer, advancementResLoc: ResourceLocation) {
        val advancement: AdvancementHolder = checkNotNull(serverPlayer.server.advancements.get(advancementResLoc))
        val progress = serverPlayer.advancements.getOrStartProgress(advancement)
        for (criterion in progress.remainingCriteria) {
            serverPlayer.advancements.revoke(advancement, criterion)
        }
    }

    fun tryUpdateProgress(serverPlayer: ServerPlayer, advancementResLoc: ResourceLocation) {
        Fishcasting.LOGGER.info("This exists: {}", advancementResLoc)
        val advancement: AdvancementHolder = checkNotNull(serverPlayer.server.advancements.get(advancementResLoc))
        val progress = serverPlayer.advancements.getOrStartProgress(advancement)
        if (progress.isDone) return
        if (!dependents.containsKey(advancementResLoc)) {
            tryGrantingAdvancement(serverPlayer, advancementResLoc)
            return
        }
        val checkThisNow = dependents[advancementResLoc]
        tryUpdateProgress(serverPlayer, checkThisNow!!)
    }

    @JvmStatic
    fun onJoinAdvancementUpdate(serverPlayer: ServerPlayer) {
        tryUpdateProgress(serverPlayer, BLESSED_BRAINSWEPT)
        tryUpdateProgress(serverPlayer, CURSED_ZAPPED)
    }
}