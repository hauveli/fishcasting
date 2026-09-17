package hauveli.fishcasting.interop.inline.moon

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.common.lib.HexItems
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.function.Function

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineMoonData(val phase: Int) : InlineData<InlineMoonData> {
    override fun getType(): InlineMoonDataType {
        return InlineMoonDataType.INSTANCE
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
        return getMoonName(phase).withStyle(asStyle(withExtra))
    }

    class InlineMoonDataType : InlineData.InlineDataType<InlineMoonData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineMoonData> {
            return Codec.INT.xmap(
                { value -> InlineMoonData(value) },
                { data -> data.phase }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineMoonDataType = InlineMoonDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("moon")

        fun getMoonName(phase: Int): MutableComponent {
            return Component.translatable(MoonPhaseIota.getName(phase))
        }
    }
}