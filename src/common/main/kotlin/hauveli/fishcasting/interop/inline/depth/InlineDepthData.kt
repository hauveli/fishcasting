package hauveli.fishcasting.interop.inline.depth

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.DepthIota
import hauveli.fishcasting.casting.iota.EnvironmentIota
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineDepthData(val depth: Int) : InlineData<InlineDepthData> {
    override fun getType(): InlineDepthDataType {
        return InlineDepthDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}"
    }

    val SHORTNAME = "depth"
    fun getName(): MutableComponent {
        return "${Fishcasting.MODID}.environment.$SHORTNAME".asTranslatedComponent
    }

    fun asTextStupid(withExtra: Boolean): Component {
        return rendererId.toLanguageKey().asTranslatedComponent
            .withStyle(asStyle(withExtra).withItalic(false))
    }

    override fun asText(withExtra: Boolean): Component {
        val name = getName()
            .append(": ${depth}m ")
            .withColor(EnvironmentIota.TYPE.color())

            // I don't quite understand how the asText thing is supposed to work... renderer puts it at start of line unless i offset it like this
        return name.append(asTextStupid(withExtra))
    }

    class InlineDepthDataType : InlineData.InlineDataType<InlineDepthData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineDepthData> {
            return Codec.INT.xmap(
                { value -> InlineDepthData(value) },
                { data -> data.depth }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineDepthDataType = InlineDepthDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("depth")
    }
}