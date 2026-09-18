package hauveli.fishcasting.interop.inline.structure

import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
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