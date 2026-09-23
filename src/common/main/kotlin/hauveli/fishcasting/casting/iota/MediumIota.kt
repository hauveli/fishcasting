package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.Locale
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class MediumIota : Iota {
    val medium: Medium

    enum class Medium(
        val fishingMedium: FishingMedium
    ) {
        Water(FishingMedium.WATER),
        Lava(FishingMedium.LAVA),
        Void(FishingMedium.VOID);

        companion object {
            private val byFishingMedium = entries.associateBy(Medium::fishingMedium)
            private val byFishingMediumString = entries.associateBy(Medium::name)

            fun of(ordinal: Int): Medium =
                entries.getOrNull(ordinal) ?: Water

            fun of(medium: FishingMedium): Medium =
                byFishingMedium[medium] ?: Water

            fun of(medium: String): Medium =
                byFishingMediumString[medium] ?: Water
        }
    }

    constructor(medium: Int) : super(Supplier { FishcastingIotaTypes. MEDIUM.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.medium = Medium.of(medium)
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is MediumIota
                && this.medium == that.medium
    }

    override fun isTruthy(): Boolean {
        return true
    }

    // in case I decided I need to check it later
    fun isValid(): Boolean {
        return this.medium.ordinal in Medium.Water.ordinal..Medium.Void.ordinal
    }

    final val SHORTNAME = "medium"
    fun getName(): String {
        return "${Fishcasting.MODID}.environment.${SHORTNAME}.${medium.name}"
    }

    // I wonder if the compiler is smart enuogh to see the usages of these are such that it could just squish them together instead of using jmp...
    fun String.capitalizeFirstLetterOfEachWord(): String {
        return this
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { char ->
                    char.titlecase(Locale.getDefault())
                }
            }
    }

    fun getNameWithFallback(): MutableComponent {
        val titleCase = medium.name.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val comp = getName().asTranslatedComponent.translation(titleCase)
        // what even sets this... does translation set it forever? why?
        return comp.withStyle(comp.style.withItalic(false))
    }

    fun getNameWithColon(): MutableComponent {
        return getNameWithFallback().append(": ")
    }

    override fun display(): Component {
        val inlineValue = (InlineMediumData(medium.ordinal)).asText(true)
        val baseText = getNameWithColon().styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return medium.ordinal // what am I supposed to put here.....
    }

    companion object {

        var TYPE: IotaType<MediumIota> = object : IotaType<MediumIota>() {

            // I couldn't think of a lower effort way, since they all have their own renderer thingies...
            // I suppose I should re-do the renderers if I ever redo this, so that my equality check doesn't look like this..
            // (for making them all actually a single type of Iota)
            override fun equals(other: Any?): Boolean {
                return when (other) {
                    BiomeIota.TYPE -> true
                    DimensionIota.TYPE -> true
                    MediumIota.TYPE -> true
                    MoonPhaseIota.TYPE -> true
                    StructureIota.TYPE -> true
                    WeatherIota.TYPE -> true
                    else -> super.equals(other)
                }
            }

            val CODEC: MapCodec<MediumIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("medium")
                            .forGetter { it.medium.ordinal }
                    ).apply(
                        inst,
                        ::MediumIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MediumIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.medium.ordinal },
                    ::MediumIota
                )

            override fun validate(
                iota: MediumIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && iota.isValid()
            }

            override fun codec(): MapCodec<MediumIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MediumIota> {
                return STREAM_CODEC
            }

            override fun color(): Int {
                return EnvironmentIota.TYPE.color()
            }
        }
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as MediumIota

        return medium == iotaToCompare.medium
    }
}