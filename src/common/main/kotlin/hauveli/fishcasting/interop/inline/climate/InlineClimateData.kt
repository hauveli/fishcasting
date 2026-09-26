package hauveli.fishcasting.interop.inline.climate

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.Tide
import com.li64.tide.config.TideClientConfig
import com.li64.tide.config.TideConfig
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


class InlineClimateData(val temperature: Float) : InlineData<InlineClimateData> {
    override fun getType(): InlineClimateDataType {
        return InlineClimateDataType.INSTANCE
    }

    override fun getRendererId(): ResourceLocation {
        return Companion.rendererId
    }

    fun getTranslatable(): String {
        return "${rendererId.toShortLanguageKey()}"
    }

    val SHORTNAME = "climate"
    fun getName(): MutableComponent {
        return "${Fishcasting.MODID}.environment.$SHORTNAME".asTranslatedComponent
    }

    fun asTextStupid(withExtra: Boolean): Component {
        return rendererId.toLanguageKey().asTranslatedComponent
            .withStyle(asStyle(withExtra).withItalic(false))
    }

    override fun asText(withExtra: Boolean): Component {
        return getName().withStyle(asStyle(withExtra))
    }


    fun displayWithTextAndInline(): Component {
        val translatableName = "${Fishcasting.MODID}.environment.climate"
        val temperature = ": ${"%.2f".format(mcTempToRealTemp(temperature))}°${if (Tide.CLIENT_CONFIG.journal.useFahrenheit) "F" else "C"} "
        val comp = translatableName.asTranslatedComponent.append(temperature)
        val nameWithColon = comp.withStyle(comp.style.withItalic(false))

        val baseText = nameWithColon.styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))

        val inlineValue = asText(true)
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    class InlineClimateDataType : InlineData.InlineDataType<InlineClimateData> {
        override fun getId(): ResourceLocation {
            return ID
        }

        override fun getCodec(): Codec<InlineClimateData> {
            return Codec.FLOAT.xmap(
                { value -> InlineClimateData(value) },
                { data -> data.temperature }
            )
        }

        companion object {
            private val ID: ResourceLocation = rendererId
            val INSTANCE: InlineClimateDataType = InlineClimateDataType()
        }
    }

    companion object {
        val rendererId: ResourceLocation = Fishcasting.id("climate")
    }
}