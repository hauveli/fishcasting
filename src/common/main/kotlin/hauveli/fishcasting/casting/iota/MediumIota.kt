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
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
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
class MediumIota : Iota {
    val medium: Medium

    enum class Medium(
        val fishingMedium: FishingMedium
    ) {
        WATER(FishingMedium.WATER),
        LAVA(FishingMedium.LAVA),
        VOID(FishingMedium.VOID);

        companion object {
            fun of(ordinal: Int): Medium {
                if (Medium.entries.lastIndex >= ordinal)
                    return Medium.entries[ordinal]
                return Medium.WATER
            }
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
        return this.medium.ordinal in Medium.WATER.ordinal..Medium.VOID.ordinal
    }

    override fun display(): Component {
        val inlineMedium = (InlineMediumData(medium.ordinal)).asText(true)
        val baseText = getNameWithColon(medium.ordinal).styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(inlineMedium).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return medium.ordinal // what am I supposed to put here.....
    }

    companion object {

        fun getNameWithColon(medium: Int): MutableComponent {
            return getName(medium).asTranslatedComponent.append(": ")
        }

        // FishingMedium.WATER FishingMedium.LAVA FishingMedium.VOID
        fun getName(medium: Int): String {
            return when (medium) {
                Medium.WATER.ordinal -> "journal.info.location.freshwater"
                Medium.LAVA.ordinal -> "journal.info.location.lava"
                Medium.VOID.ordinal -> "journal.info.location.void"
                else -> "Unknown Medium"
            }
        }

        var TYPE: IotaType<MediumIota> = object : IotaType<MediumIota>() {

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