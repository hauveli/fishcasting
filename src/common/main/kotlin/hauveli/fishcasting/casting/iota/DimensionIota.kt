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
import hauveli.fishcasting.interop.inline.structure.InlineStructureData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
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
class DimensionIota : EnvironmentIota {
    val value: ResourceKey<Level>

    constructor(dimension: ResourceKey<Level>) : super(Supplier { FishcastingIotaTypes.DIMENSION.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.value = dimension
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is DimensionIota
                && this.value == that.value
    }

    override fun isTruthy(): Boolean {
        return true
    }

    final val SHORTNAME = "dimension"
    fun getName(): String {
        return "${Fishcasting.MODID}.environment.${SHORTNAME}.${value.location().toLanguageKey()}"
    }

    fun getNameWithFallback(): MutableComponent {
        val titleCase = value.location().path.replace("_", " ").capitalizeFirstLetterOfEachWord()
        val comp = getName().asTranslatedComponent.translation(titleCase)
        // what even sets this... does translation set it forever? why?
        return comp.withStyle(comp.style.withItalic(false))
    }

    fun getNameWithColon(): MutableComponent {
        return getNameWithFallback().append(": ")
    }

    override fun display(): Component {
        val inlineValue = (InlineDimensionData(value.location().toLanguageKey())).asText(true)
        val baseText = getNameWithColon().styledWith(Style.EMPTY.withColor(EnvironmentIota.TYPE.color()))
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return value.hashCode() // what am I supposed to put here.....
    }

    companion object {

        var TYPE: IotaType<DimensionIota> = object : IotaType<DimensionIota>() {

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

            val CODEC: MapCodec<DimensionIota> =
                Codec.STRING
                    .fieldOf("dimension")
                    .xmap(
                        { string ->
                            DimensionIota(
                                ResourceKey.create(
                                    Registries.DIMENSION,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { iota ->
                            iota.value.location().toString()
                        }
                    )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DimensionIota> =
                StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    { it.value.location().toString() },
                    { string ->
                        DimensionIota(
                            ResourceKey.create(
                                Registries.DIMENSION,
                                ResourceLocation.parse(string)
                            )
                        )
                    }
                )

            override fun validate(
                iota: DimensionIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<DimensionIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DimensionIota> {
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

        iotaToCompare as DimensionIota

        return value == iotaToCompare.value
    }
}