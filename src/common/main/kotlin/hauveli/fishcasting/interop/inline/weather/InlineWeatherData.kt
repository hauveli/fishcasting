package hauveli.fishcasting.interop.inline.weather

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.WeatherIota
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineWeatherData(val weather: Int) : InlineData<InlineWeatherData> {
    override fun getType(): InlineWeatherDataType {
        return InlineWeatherDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    override fun asText(withExtra: Boolean): Component {
        return getWeatherName(weather).withStyle(asStyle(withExtra))
    }

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

    fun getWeatherName(weather: Int): MutableComponent {
        return Component.translatable(getName(weather))
    }

    fun displayWithTextAndInline(): Component {
        val baseText = getNameWithColon(weather).styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(asText(true))
    }

    class InlineWeatherDataType : InlineData.InlineDataType<InlineWeatherData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineWeatherData> {
            return Codec.INT.xmap(
                { value -> InlineWeatherData(value) },
                { data -> data.weather }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineWeatherDataType = InlineWeatherDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("weather")

        fun getName(weather: Int): String {
            return when (weather) {
                WeatherType.CLEAR.ordinal -> WeatherType.CLEAR.name
                WeatherType.RAIN.ordinal -> WeatherType.RAIN.name
                WeatherType.STORM.ordinal -> WeatherType.STORM.name
                else -> "unknown_weather"
            }
        }
    }
}