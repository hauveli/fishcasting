package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.interop.inline.depth.InlineDepthData
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.structure.InlineStructureData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import java.util.Locale
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class DepthIota : EnvironmentIota {
    val value: Int

    constructor(yLevel: Int) : super(Supplier { FishcastingIotaTypes.DEPTH.value }) {
        this.value = yLevel
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is DepthIota
                && this.value == that.value
    }

    override fun isTruthy(): Boolean {
        return true
    }

    override fun display(): Component {
        return (InlineDepthData(value)).asText(true) // baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    companion object {

        var TYPE: IotaType<DepthIota> = object : IotaType<DepthIota>() {

            // I couldn't think of a lower effort way, since they all have their own renderer thingies...
            // I suppose I should re-do the renderers if I ever redo this, so that my equality check doesn't look like this..
            // (for making them all actually a single type of Iota)
            override fun equals(other: Any?): Boolean {
                return BiomeIota.TYPE === other
                        || DimensionIota.TYPE === other
                        || MediumIota.TYPE === other
                        || MoonPhaseIota.TYPE === other
                        || StructureIota.TYPE === other
                        || WeatherIota.TYPE === other
            }

            val CODEC: MapCodec<DepthIota> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        Codec.INT.fieldOf("depth")
                            .forGetter { it.value }
                    ).apply(
                        inst,
                        ::DepthIota
                    )
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DepthIota> =
                StreamCodec.composite(
                    ByteBufCodecs.INT,
                    { it.value },
                    ::DepthIota
                )

            override fun validate(
                iota: DepthIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<DepthIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DepthIota> {
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

        iotaToCompare as DepthIota

        return value == iotaToCompare.value
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }
}