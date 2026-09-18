package hauveli.fishcasting.interop.inline.dimension

import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.MediumIota
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


    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}.${dimension.replace(":",".")}"
    }

    fun getName(): MutableComponent {
        return Component.translatable(getTranslatable())
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
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