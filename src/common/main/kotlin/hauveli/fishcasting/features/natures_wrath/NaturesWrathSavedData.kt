package hauveli.fishcasting.features.natures_wrath

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
import kotlin.math.max

// https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata
class NaturesWrathSavedData : SavedData() {

    override fun save(
        tag: CompoundTag,
        lookupProvider: HolderLookup.Provider
    ): CompoundTag {
        val players = ListTag()

        for ((uuid, gameTime) in timedOutUntil) {
            val playerTag = CompoundTag()

            playerTag.putUUID("UUID", uuid)
            playerTag.putLong("GameTime", gameTime)

            players.add(playerTag)
        }

        tag.put(DATA_NAME, players)

        return tag
    }

    private val timedOutUntil = mutableMapOf<UUID, Long>()

    companion object {
        private const val DATA_NAME = "fishcasting_natures_cooldown"

        fun create(): NaturesWrathSavedData {
            return NaturesWrathSavedData()
        }

        private fun load(
            tag: CompoundTag,
            lookupProvider: HolderLookup.Provider
        ): NaturesWrathSavedData {
            val data = NaturesWrathSavedData.create() // why do the neoforge docs do this... is it a java thing?

            val players = tag.getList(
                DATA_NAME,
                Tag.TAG_COMPOUND.toInt()
            )

            for (i in 0 until players.size) {
                val playerTag = players.getCompound(i)

                val uuid = playerTag.getUUID("UUID")
                val gameTime = playerTag.getLong("GameTime")

                data.timedOutUntil[uuid] = gameTime
            }

            return data
        }

        fun get(server: MinecraftServer): NaturesWrathSavedData {
            return server.overworld().dataStorage.computeIfAbsent(
                Factory(
                    NaturesWrathSavedData::create,
                    NaturesWrathSavedData::load,
                    DataFixTypes.PLAYER // unsure about this...............
                ),
                DATA_NAME
            )
        }

        private fun setTimestamp(player: ServerPlayer, data: NaturesWrathSavedData) {
            data.timedOutUntil[player.uuid] = player.server.overworld().gameTime
        }

        private fun getTimestamp(player: Player, data: NaturesWrathSavedData): Long? {
            return data.timedOutUntil[player.uuid]
        }

        private fun timeSinceTimestamp(player: ServerPlayer, data: NaturesWrathSavedData): Long {
            val last = getTimestamp(player, data) ?: return Long.MAX_VALUE
            return player.server.overworld().gameTime - last
        }

        private fun ticksToMinutes(ticks: Long): Float {
            return ticks / 1200f
        }

        private fun pastTimestamp(player: ServerPlayer, data: NaturesWrathSavedData): Boolean {
            return ticksToMinutes(timeSinceTimestamp(player, data)) >= FishcastingConfigs.COMMON_CONFIG.timeSkipPerPlayerIntervalMinutes.get()
        }

        private fun configToTicks(): Long {
            val configValue = FishcastingConfigs.COMMON_CONFIG.timeSkipPerPlayerIntervalMinutes.get()
            return (configValue * 60 * 20).toLong()
        }

        fun ticksRemaining(player: ServerPlayer): Long {
            val level = player.level() ?: return -1L
            val server = level.server ?: return -1L
            val savedData = NaturesWrathSavedData.get(server)

            return max(configToTicks() - timeSinceTimestamp(player, savedData), 0)
        }

        fun trueIfOnCooldown(player: ServerPlayer): Boolean {
            val level = player.level() ?: return true
            val server = level.server ?: return true
            val savedData = NaturesWrathSavedData.get(server)

            if (!pastTimestamp(player, savedData))
                return true

            setTimestamp(player, savedData)
            savedData.setDirty()
            return false
        }
    }
}