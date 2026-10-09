package hauveli.fishcasting.interop.inline.medium

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.Tide
import com.li64.tide.util.TideUtils.mcTempToRealTemp
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.MediumIota
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
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

    fun displayWithTextAndInline(): Component {
        val medium = MediumIota.Medium.of(medium)
        val titleCase = medium.name.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val translatableName = "${Fishcasting.MODID}.environment.medium.${medium.name}"

        val comp = translatableName.asTranslatedComponent.translation(titleCase)
        val textWithFallback = comp.withStyle(comp.style.withItalic(false)).append(": ")

        val baseText = textWithFallback.styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))

        val inlineValue = asText(true)
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
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