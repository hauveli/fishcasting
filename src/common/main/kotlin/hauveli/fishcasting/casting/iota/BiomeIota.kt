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
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.Structures
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
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class BiomeIota : Iota {
    val value: ResourceKey<Biome>

    constructor(biome: ResourceKey<Biome>) : super(Supplier { FishcastingIotaTypes.BIOME.value }) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.value = biome
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is BiomeIota
                && this.value == that.value
    }

    override fun isTruthy(): Boolean {
        return true
    }

    final val SHORTNAME = "biome"
    fun getName(): String {
        return "${Fishcasting.MODID}.environment.${SHORTNAME}.${value.location().toLanguageKey()}"
    }

    fun getNameWithColon(): MutableComponent {
        return getName().asTranslatedComponent.append(": ")
    }

    override fun display(): Component {
        val inlineValue = (InlineDimensionData(value.location().toLanguageKey())).asText(true)
        val baseText = getNameWithColon().styledWith(Style.EMPTY.withColor(0xBACADA))
        return baseText.append(inlineValue).append("   ") // inline was being evil and this is simple
    }

    override fun hashCode(): Int {
        return value.hashCode() // what am I supposed to put here.....
    }

    companion object {

        var TYPE: IotaType<BiomeIota> = object : IotaType<BiomeIota>() {

            val CODEC: MapCodec<BiomeIota> =
                Codec.STRING
                    .fieldOf("biome")
                    .xmap(
                        { string ->
                            BiomeIota(
                                ResourceKey.create(
                                    Registries.BIOME,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { iota ->
                            iota.value.location().toString()
                        }
                    )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BiomeIota> =
                StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    { it.value.location().toString() },
                    { string ->
                        BiomeIota(
                            ResourceKey.create(
                                Registries.BIOME,
                                ResourceLocation.parse(string)
                            )
                        )
                    }
                )

            override fun validate(
                iota: BiomeIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }

            override fun codec(): MapCodec<BiomeIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BiomeIota> {
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

        iotaToCompare as BiomeIota

        return value == iotaToCompare.value
    }
}