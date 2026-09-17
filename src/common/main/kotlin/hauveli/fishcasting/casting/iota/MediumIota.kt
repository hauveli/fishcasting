package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
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
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class MediumIota : Iota {
    val medium: Int

    constructor(medium: Int) : super(Supplier { FishcastingIotaTypes.MEDIUM.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.medium = medium
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
        return this.medium in 0..2
    }

    override fun display(): Component {
        val inlineMedium = (InlineMediumData(medium)).asText(true)
        val baseText = getNameWithColon(medium).styledWith(Style.EMPTY.withColor(0xBACADA))
        return baseText.append(inlineMedium).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return medium // what am I supposed to put here.....
    }

    companion object {

        fun getNameWithColon(medium: Int): String {
            return getName(medium) + ": "
        }

        // FishingMedium.WATER FishingMedium.LAVA FishingMedium.VOID
        fun getName(medium: Int): String {
            return when (medium) {
                0 -> "Water"
                1 -> "Lava"
                2 -> "Void"
                else -> "Unknown Medium"
            }
        }

        var TYPE: IotaType<MediumIota> = object : IotaType<MediumIota>() {

            val CODEC: MapCodec<MediumIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("weather")
                            .forGetter { it.medium }
                    ).apply(
                        inst,
                        ::MediumIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MediumIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.medium },
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
                return 0xBACADA
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