package hauveli.fishcasting.interop.inline.medium

import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.MediumIota
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineMediumData(val medium: Int) : InlineData<InlineMediumData> {
    override fun getType(): InlineMediumDataType {
        return InlineMediumDataType.INSTANCE
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
        return getWeatherName(medium).withStyle(asStyle(withExtra))
    }

    class InlineMediumDataType : InlineData.InlineDataType<InlineMediumData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineMediumData> {
            return Codec.INT.xmap(
                { value -> InlineMediumData(value) },
                { data -> data.medium }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineMediumDataType = InlineMediumDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("medium")

        fun getWeatherName(medium: Int): MutableComponent {
            return Component.translatable(MediumIota.getName(medium))
        }
    }
}