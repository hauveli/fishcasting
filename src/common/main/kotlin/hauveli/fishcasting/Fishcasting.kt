package hauveli.fishcasting

import hauveli.fishcasting.common.FishcastingConfig
import hauveli.fishcasting.config.FishcastingConfigs
import hauveli.fishcasting.networking.FishcastingNetworking
import hauveli.fishcasting.registry.FishcastingActions
import hauveli.fishcasting.registry.FishcastingSounds
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.function.BiConsumer

object Fishcasting {
    const val MODID = "fishcasting"
    const val MOD_NAME: String = "Fishcasting"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmField
    val FISHBERT_TAG: String = "$MODID:recently_caught"

    @JvmField
    var CONFIG: FishcastingConfig? = null

    // I dont know if I should avoid using this or not, I noticed some classes have access to Entity.random...
    @JvmField
    val random: Random = Random()

    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)


    fun <T> bind(registry: Registry<in T>): BiConsumer<T, ResourceLocation> =
        BiConsumer<T, ResourceLocation> { t, id -> Registry.register(registry, id, t) }

    fun init() {
        initRegistries(
            FishcastingActions

        )
        FishcastingNetworking.init()
        FishcastingConfigs.init()

        CONFIG = AutoConfig.register(
            FishcastingConfig::class.java,
            { definition: Config?, configClass: Class<FishcastingConfig?>? ->
                Toml4jConfigSerializer(
                    definition,
                    configClass
                )
            }).getConfig()
    }

    fun initServer() {
    }
}
