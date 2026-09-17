package hauveli.fishcasting.interop.inline.weather

import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineWeatherData(val phase: Int) : InlineData<InlineWeatherData> {
    override fun getType(): InlineWeatherDataType {
        return InlineWeatherDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    /*
    override fun getExtraStyle(): Style {
        val scrollStack = ItemStack(HexItems.SCROLL_MEDIUM.get())
        HexItems.SCROLL_MEDIUM.get().writeDatum(scrollStack, PatternIota(pattern))
        scrollStack.set<MutableComponent?>(
            DataComponents.ITEM_NAME, getPatternName(pattern)!!.copy().withStyle(
                ChatFormatting.WHITE
            )
        )
        val he = HoverEvent(HoverEvent.Action.SHOW_ITEM, HoverEvent.ItemStackInfo(scrollStack))
        val ce = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pattern.toString())
        return Style.EMPTY.withHoverEvent(he).withClickEvent(ce)
    }
     */

    override fun asText(withExtra: Boolean): Component {
        return getWeatherName(phase).withStyle(asStyle(withExtra))
    }

    class InlineWeatherDataType : InlineData.InlineDataType<InlineWeatherData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineWeatherData> {
            return Codec.INT.xmap(
                { value -> InlineWeatherData(value) },
                { data -> data.phase }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineWeatherDataType = InlineWeatherDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("weather")

        fun getWeatherName(phase: Int): MutableComponent {
            return Component.translatable(WeatherIota.getName(phase))
        }
    }
}