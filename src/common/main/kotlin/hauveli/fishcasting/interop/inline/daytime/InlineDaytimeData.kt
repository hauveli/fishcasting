package hauveli.fishcasting.interop.inline.daytime

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.li64.tide.Tide
import com.li64.tide.util.TideUtils.mcTempToRealTemp
import com.li64.tide.util.TideUtils.ticksToRealTime
import com.mojang.serialization.Codec
import com.samsthenerd.inline.api.InlineData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.EnvironmentIota
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternData.java


class InlineDaytimeData(val daytime: Long) : InlineData<InlineDaytimeData> {
    override fun getType(): InlineDaytimeDataType {
        return InlineDaytimeDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}"
    }

    val SHORTNAME = "daytime"
    fun getName(): MutableComponent {
        return "${Fishcasting.MODID}.environment.$SHORTNAME".asTranslatedComponent
    }

    fun asTextStupid(withExtra: Boolean): Component {
        return rendererId.toLanguageKey().asTranslatedComponent
            .withStyle(asStyle(withExtra).withItalic(false))
    }

    override fun asText(withExtra: Boolean): Component {
        val name = getName()
            .append(": ${ticksToRealTime(daytime, Tide.CLIENT_CONFIG.journal.useAmPm)} ")
            .withColor(EnvironmentIota.TYPE.color())

            // I don't quite understand how the asText thing is supposed to work... renderer puts it at start of line unless i offset it like this
        return name.append(asTextStupid(withExtra))
    }

    class InlineDaytimeDataType : InlineData.InlineDataType<InlineDaytimeData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineDaytimeData> {
            return Codec.LONG.xmap(
                { value -> InlineDaytimeData(value) },
                { data -> data.daytime }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineDaytimeDataType = InlineDaytimeDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("daytime")
    }
}