package hauveli.fishcasting.interop.inline.biome

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.Tide
import com.li64.tide.util.TideUtils.mcTempToRealTemp
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import hauveli.fishcasting.casting.iota.EnvironmentIota
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineBiomeData(val biome: String) : InlineData<InlineBiomeData> {
    override fun getType(): InlineBiomeDataType {
        return InlineBiomeDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}.${biome.replace(":",".")}"
    }

    fun getName(): MutableComponent {
        return Component.translatable(getTranslatable())
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
    }

    fun displayWithTextAndInline(): Component {
        val translatableName = "${Fishcasting.MODID}.environment.biome.${biome.replace(":", ".")}"
        val everythingAfterTHeNamespace = biome.substringAfter(":")
        val titleCase = everythingAfterTHeNamespace.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val comp = translatableName.asTranslatedComponent.translation(titleCase)
        val nameWithColon = comp.withStyle(comp.style.withItalic(false)).append(": ")

        val baseText = nameWithColon.styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))

        val inlineValue = asText(true)
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    class InlineBiomeDataType : InlineData.InlineDataType<InlineBiomeData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineBiomeData> {
            return Codec.STRING.xmap(
                { value -> InlineBiomeData(value) },
                { data -> data.biome }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineBiomeDataType = InlineBiomeDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("biome")
    }
}