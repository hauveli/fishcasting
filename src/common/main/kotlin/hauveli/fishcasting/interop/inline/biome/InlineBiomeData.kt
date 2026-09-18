package hauveli.fishcasting.interop.inline.biome

import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
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