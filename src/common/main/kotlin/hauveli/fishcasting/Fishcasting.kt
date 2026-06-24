package hauveli.fishcasting

import hauveli.fishcasting.config.FishcastingConfigs
import hauveli.fishcasting.networking.FishcastingNetworking
import hauveli.fishcasting.registry.FishcastingActions
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.Random

object Fishcasting {
    const val MODID = "fishcasting"
    const val MOD_NAME: String = "Fishcasting"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmField
    val FISHBERT_TAG: String = "$MODID:recently_caught"

    @JvmStatic
    fun tryGrantingAdvancement(serverPlayer: ServerPlayer, advancementResLoc: ResourceLocation) {
        val advancement: AdvancementHolder = checkNotNull(serverPlayer.server.advancements.get(advancementResLoc))
        val progress = serverPlayer.advancements.getOrStartProgress(advancement)
        if (progress.isDone) return
        for (criterion in progress.remainingCriteria) {
            serverPlayer.advancements.award(advancement, criterion)
        }
    }

    // I dont know if I should avoid using this or not, I noticed some classes have access to Entity.random...
    @JvmField
    val random: Random = Random()

    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)

    fun init() {
        initRegistries(
            FishcastingActions
        )
        FishcastingNetworking.init()
        FishcastingConfigs.init()
    }

    fun initServer() {
    }
}
