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

    fun getName(): MutableComponent {
        return Component.translatable(getTranslatable())
    }

    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}.${MediumIota.Medium.of(medium).name.replace(":",".")}"
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
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
    }
}