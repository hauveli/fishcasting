package hauveli.fishcasting.interop.inline.dimension

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.MediumIota
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineDimensionData(val dimension: String) : InlineData<InlineDimensionData> {
    override fun getType(): InlineDimensionDataType {
        return InlineDimensionDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }


    // I think I was intiially intending to do it this way but then decided against it... I forget why...
    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}.${dimension.replace(":",".")}"
    }

    fun getName(): MutableComponent {
        return Component.translatable(getTranslatable())
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
    }

    fun displayWithTextAndInline(): Component {
        val text = "${Fishcasting.MODID}.environment.dimension.${dimension}"
        val everythingAfterTHeNamespace = dimension.substringAfter(":")
        val titleCase = everythingAfterTHeNamespace.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val comp = text.asTranslatedComponent.translation(titleCase)
        // what even sets this... does translation set it forever? why?
        val textWithFallback = comp.withStyle(comp.style.withItalic(false))

        val baseText = textWithFallback.append(": ")
            .styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(asText(true)).append("   ") // inline was being evil and this is simple
    }

    class InlineDimensionDataType : InlineData.InlineDataType<InlineDimensionData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineDimensionData> {
            return Codec.STRING.xmap(
                { value -> InlineDimensionData(value) },
                { data -> data.dimension }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineDimensionDataType = InlineDimensionDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("dimension")
    }
}