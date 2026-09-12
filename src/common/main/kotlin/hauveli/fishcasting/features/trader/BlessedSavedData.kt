package hauveli.fishcasting.features.trader

import hauveli.fishcasting.config.FishcastingConfigs
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.saveddata.SavedData
import java.util.UUID

// https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata
class BlessedSavedData : SavedData() {

    override fun save(
        tag: CompoundTag,
        lookupProvider: HolderLookup.Provider
    ): CompoundTag {
        val players = ListTag()

        for ((uuid, gameTime) in lastFishyTrader) {
            val playerTag = CompoundTag()

            playerTag.putUUID("UUID", uuid)
            playerTag.putLong("GameTime", gameTime)

            players.add(playerTag)
        }

        tag.put(DATA_NAME, players)

        return tag
    }

    private val lastFishyTrader = mutableMapOf<UUID, Long>()

    companion object {
        private const val DATA_NAME = "fishcasting_trader_cooldown"


        fun create(): BlessedSavedData {
            return BlessedSavedData()
        }

        private fun load(
            tag: CompoundTag,
            lookupProvider: HolderLookup.Provider
        ): BlessedSavedData {
            val data = BlessedSavedData.create() // why do the neoforge docs do this... is it a java thing?

            val players = tag.getList(
                DATA_NAME,
                Tag.TAG_COMPOUND.toInt()
            )

            for (i in 0 until players.size) {
                val playerTag = players.getCompound(i)

                val uuid = playerTag.getUUID("UUID")
                val gameTime = playerTag.getLong("GameTime")

                data.lastFishyTrader[uuid] = gameTime
            }

            return data
        }

        fun get(server: MinecraftServer): BlessedSavedData {
            return server.overworld().dataStorage.computeIfAbsent(
                SavedData.Factory(
                    BlessedSavedData::create,
                    BlessedSavedData::load,
                    DataFixTypes.PLAYER // unsure about this...............
                ),
                DATA_NAME
            )
        }

        private fun recordFish(player: ServerPlayer, data: BlessedSavedData) {
            data.lastFishyTrader[player.uuid] = player.server.overworld().gameTime
        }

        private fun getLastFish(player: Player, data: BlessedSavedData): Long? {
            return data.lastFishyTrader[player.uuid]
        }

        private fun timeSinceLastFish(player: ServerPlayer, data: BlessedSavedData): Long {
            val last = getLastFish(player, data) ?: return Long.MAX_VALUE
            return player.server.overworld().gameTime - last
        }

        private fun ticksToMinutes(ticks: Long): Float {
            return ticks / 1200f
        }

        private fun canFish(player: ServerPlayer, data: BlessedSavedData): Boolean {
            return ticksToMinutes(timeSinceLastFish(player, data)) >= FishcastingConfigs.COMMON_CONFIG.fishyTraderPerPlayerIntervalMinutes.get()
        }


        fun trueIfOnCooldown(player: ServerPlayer): Boolean {
            val level = player.level() ?: return true
            val server = level.server ?: return true
            val savedData = BlessedSavedData.get(server)

            if (!canFish(player, savedData))
                return true

            recordFish(player, savedData)
            savedData.setDirty()
            return false
        }
    }
}