package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.interop.inline.biome.InlineBiomeData
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.Structures
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure
import java.util.Locale
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
abstract class EnvironmentIota(
    val supplier: Supplier<IotaType<out Iota>>
) : Iota(supplier) {
/*
    Considerations for me to think about:
    I want to be able to store basically anything in the iota...
    How the fuck do I do this?
    does Any<*> work?
    should I have one optional field per possible value?
    how the fuck should the codec look?
    can I just create an "EnvironmentIota" and then do BiomeIota : EnvironmentIota? - This seems really tempting.........
    if typechecking works with the above, I would basically be done afterwards...
    wait a minute, IotaType?
    can I just lie and say they are the same IotaType? please
 */
    override fun isTruthy(): Boolean {
        return true
    }

    override fun toleratesOther(otherIota: Iota?): Boolean {
        return otherIota is EnvironmentIota
    }

    //val value: Iota

    // I wonder if the compiler is smart enuogh to see the usages of these are such that it could just squish them together instead of using jmp...
    fun String.capitalizeFirstLetterOfEachWord(): String {
        return this
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { char ->
                    char.titlecase(Locale.getDefault())
                }
            }
    }

    override fun display(): Component {
        return Component.nullToEmpty("REPORT THIS TO DEVELOPER")
    }

    override fun hashCode(): Int {
        return 0
    }

    companion object {

        var TYPE: IotaType<EnvironmentIota> = object : IotaType<EnvironmentIota>() {

            override fun validate(
                iota: EnvironmentIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<EnvironmentIota> {
                Fishcasting.LOGGER.info("codec: {}", this)
                return TODO()
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, EnvironmentIota> {
                Fishcasting.LOGGER.info("stream_codec: {}", this)
                return TODO()
            }

            override fun color(): Int {
                return COLOR
            }

            val COLOR = 0x82add9
        }
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as EnvironmentIota

        return true // value == iotaToCompare.value
    }
}