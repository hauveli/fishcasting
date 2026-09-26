package hauveli.fishcasting.interop.inline.structure

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import hauveli.fishcasting.casting.iota.EnvironmentIota
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineStructureData(val structure: String) : InlineData<InlineStructureData> {
    override fun getType(): InlineStructureDataType {
        return InlineStructureDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }


    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}.${structure.replace(":",".")}"
    }

    fun getName(): MutableComponent {
        return Component.translatable(getTranslatable())
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
    }

    fun displayWithTextAndInline(): Component {
        val name = "${Fishcasting.MODID}.environment.structure.${structure.replace(":",".")}"
        val everythingAfterTHeNamespace = structure.substringAfter(":")
        val titleCase = everythingAfterTHeNamespace.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val comp = name.asTranslatedComponent.translation(titleCase)
        // what even sets this... does translation set it forever? why?
        val nameWithColon = comp.withStyle(comp.style.withItalic(false))

        val baseText = nameWithColon.styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(asText(true)).append("   ") // inline was being evil and this is simple
    }

    class InlineStructureDataType : InlineData.InlineDataType<InlineStructureData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineStructureData> {
            return Codec.STRING.xmap(
                { value -> InlineStructureData(value) },
                { data -> data.structure }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineStructureDataType = InlineStructureDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("structure")
    }
}