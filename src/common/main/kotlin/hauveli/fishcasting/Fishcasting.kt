package hauveli.fishcasting

//import hauveli.fishcasting.networking.FishcastingNetworking
import at.petrak.hexcasting.api.HexAPI
import hauveli.fishcasting.config.FishcastingConfigs
import hauveli.fishcasting.interop.inline.InlineFishcastingServer
import hauveli.fishcasting.registry.*
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

object Fishcasting {
    const val MODID = "fishcasting"
    const val MOD_NAME = "Fishcasting"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    const val FISHBERT_TAG = "$MODID:recently_caught"


    fun String.capitalizeFirstLetterOfEachWord(): String {
        return this
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { char ->
                    char.titlecase(Locale.getDefault())
                }
            }
    }

    // I dont know if I should avoid using this or not, I noticed some classes have access to Entity.random...
    @JvmField
    val random: Random = Random()

    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)

    fun init() {
        initRegistries(
            FishcastingActions,
            FishcastingArithmetic,
            FishcastingAttributes,
            FishcastingCreativeTabs,
            FishcastingEntities,
            FishcastingIotaTypes,
            FishcastingRecipeTypes,
            FishcastingRecipeSerializers,
            FishcastingSounds,
            FishcastingItems
        )
        InlineFishcastingServer.init()
        // FishcastingNetworking.init()
        FishcastingConfigs.init()
    }

    fun initServer() {
    }
}
