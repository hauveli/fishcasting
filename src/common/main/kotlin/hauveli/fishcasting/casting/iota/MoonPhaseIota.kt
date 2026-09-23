package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import at.petrak.hexcasting.interop.inline.InlinePatternData
import com.li64.tide.util.MoonPhases.*
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class MoonPhaseIota : Iota {
    val moonPhase: Int

    constructor(moonPhase: Int) : super(Supplier { FishcastingIotaTypes.MOON_PHASE.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.moonPhase = moonPhase
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is MoonPhaseIota
                && this.moonPhase == that.moonPhase
    }

    override fun isTruthy(): Boolean {
        return true
    }

    // in case I decided I need to check it later
    fun isValid(): Boolean {
        return this.moonPhase in FULL_MOON..WAXING_GIBBOUS
    }

    override fun display(): Component {
        val inlineMoonPhase = (InlineMoonData(moonPhase)).asText(true)
        val baseText = getNameWithColon(moonPhase).styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(inlineMoonPhase).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return moonPhase // what am I supposed to put here.....
    }

    companion object {

        fun getNameWithColon(phase: Int): MutableComponent {
            return getName(phase).asTranslatedComponent.append(": ")
        }

        fun getName(phase: Int): String {
            return when (phase) {
                FULL_MOON -> "journal.info.moon_phase.0"
                WANING_GIBBOUS -> "journal.info.moon_phase.1"
                THIRD_QUARTER -> "journal.info.moon_phase.2"
                WANING_CRESCENT -> "journal.info.moon_phase.3"
                NEW_MOON -> "journal.info.moon_phase.4"
                WAXING_CRESCENT -> "journal.info.moon_phase.5"
                FIRST_QUARTER -> "journal.info.moon_phase.6"
                WAXING_GIBBOUS -> "journal.info.moon_phase.7"
                else -> "Unknown journal.info.moon_phase.tile"
            }
        }

        var TYPE: IotaType<MoonPhaseIota> = object : IotaType<MoonPhaseIota>() {

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

            val CODEC: MapCodec<MoonPhaseIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("moonPhase")
                            .forGetter { it.moonPhase }
                    ).apply(
                        inst,
                        ::MoonPhaseIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MoonPhaseIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.moonPhase },
                    ::MoonPhaseIota
                )

            override fun validate(
                iota: MoonPhaseIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && iota.isValid()
            }

            override fun codec(): MapCodec<MoonPhaseIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, MoonPhaseIota> {
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

        iotaToCompare as MoonPhaseIota

        return moonPhase == iotaToCompare.moonPhase
    }
}