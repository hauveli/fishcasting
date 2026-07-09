package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer


object FishcastingAdvancements {
    @JvmField
    val TACKLEBOX_CHAIR_FISHER = Fishcasting.id("instructional/fished_in_tacklebox_chair")
    @JvmField
    val TRADER_BRAINSWEPT = Fishcasting.id("instructional/brainswept_trader_in_void")
    val CURSED_ZAPPED = Fishcasting.id("instructional/lightning_struck_cursed")


    val TRADER_BRAINSWEPT_CLUED = Fishcasting.id("lore/notes3")
    val CURSED_ZAPPED_CLUED = Fishcasting.id("lore/notes4")

    val TRADER_BRAINSWEPT_CLUELESS = Fishcasting.id("hint/voidsweep")
    val CURSED_ZAPPED_CLUELESS = Fishcasting.id("hint/annihilation")

    val dependents = mapOf(
        TRADER_BRAINSWEPT to TRADER_BRAINSWEPT_CLUED,
        TRADER_BRAINSWEPT_CLUED to TRADER_BRAINSWEPT_CLUELESS,

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
        tryUpdateProgress(serverPlayer, TRADER_BRAINSWEPT)
        tryUpdateProgress(serverPlayer, CURSED_ZAPPED)
    }
}