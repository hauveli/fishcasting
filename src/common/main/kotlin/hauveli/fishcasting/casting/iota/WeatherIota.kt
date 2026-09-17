package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
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
        val baseText = getNameWithColon(weather).styledWith(Style.EMPTY.withColor(0xBACADA))
        return baseText.append(inlineWeather).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return weather // what am I supposed to put here.....
    }

    companion object {

        fun getNameWithColon(weather: Int): String {
            return getName(weather) + ": "
        }

        fun getName(phase: Int): String {
            return when (phase) {
                WeatherType.CLEAR.ordinal -> "Clear Skies"
                WeatherType.RAIN.ordinal -> "Rainy Clouds"
                WeatherType.STORM.ordinal -> "Thunderstorm"
                else -> "Unknown Weather"
            }
        }

        var TYPE: IotaType<WeatherIota> = object : IotaType<WeatherIota>() {

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
                return 0xBACADA
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