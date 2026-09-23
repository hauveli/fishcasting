package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import at.petrak.hexcasting.interop.inline.InlinePatternData
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.util.MoonPhases.*
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class WeatherIota : Iota {
    val weather: Int

    /*
        Hmmm...
        IF I treat this as a precipitation spell instead, can I infer thunder?
     */

    constructor(weather: Int) : super(Supplier { FishcastingIotaTypes.WEATHER.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.weather = weather
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is WeatherIota
                && this.weather == that.weather
    }

    override fun isTruthy(): Boolean {
        return true
    }

    // in case I decided I need to check it later
    fun isValid(): Boolean {
        return this.weather in WeatherType.CLEAR.ordinal..WeatherType.STORM.ordinal
    }

    override fun display(): Component {
        val inlineWeather = (InlineWeatherData(weather)).asText(true)
        val baseText = getNameWithColon(weather).styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(inlineWeather).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return weather // what am I supposed to put here.....
    }

    companion object {

        fun getNameWithColon(weather: Int): MutableComponent {
            return getName(weather).asTranslatedComponent.append(": ")
        }

        fun getName(weather: Int): String {
            return when (weather) {
                WeatherType.CLEAR.ordinal -> "fishcasting.iota.weather.clear"
                WeatherType.RAIN.ordinal -> "fishcasting.iota.weather.rain"
                WeatherType.STORM.ordinal -> "fishcasting.iota.weather.storm"
                else -> "fishcasting.iota.weather.unknown"
            }
        }

        var TYPE: IotaType<WeatherIota> = object : IotaType<WeatherIota>() {

            // I couldn't think of a lower effort way, since they all have their own renderer thingies...
            // I suppose I should re-do the renderers if I ever redo this, so that my equality check doesn't look like this..
            // (for making them all actually a single type of Iota)
            override fun equals(other: Any?): Boolean {
                return BiomeIota.TYPE === other
                        || DimensionIota.TYPE === other
                        || MediumIota.TYPE === other
                        || MoonPhaseIota.TYPE === other
                        || StructureIota.TYPE === other
                        || WeatherIota.TYPE === other
            }

            val CODEC: MapCodec<WeatherIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("weather")
                            .forGetter { it.weather }
                    ).apply(
                        inst,
                        ::WeatherIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, WeatherIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.weather },
                    ::WeatherIota
                )

            override fun validate(
                iota: WeatherIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && iota.isValid()
            }

            override fun codec(): MapCodec<WeatherIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, WeatherIota> {
                return STREAM_CODEC
            }

            override fun color(): Int {
                return EnvironmentIota.TYPE.color()
            }
        }
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as WeatherIota

        return weather == iotaToCompare.weather
    }
}