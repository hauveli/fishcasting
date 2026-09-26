package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.EnvironmentValue.Climate
import hauveli.fishcasting.casting.iota.EnvironmentValue.Companion.ENVIRONMENT_CODEC
import hauveli.fishcasting.casting.iota.EnvironmentValue.Companion.ENVIRONMENT_STREAM_CODEC
import hauveli.fishcasting.casting.iota.EnvironmentValue.Daytime
import hauveli.fishcasting.casting.iota.EnvironmentValue.Depth
import hauveli.fishcasting.interop.inline.biome.InlineBiomeData
import hauveli.fishcasting.interop.inline.climate.InlineClimateData
import hauveli.fishcasting.interop.inline.daytime.InlineDaytimeData
import hauveli.fishcasting.interop.inline.depth.InlineDepthData
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import hauveli.fishcasting.interop.inline.structure.InlineStructureData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import java.util.Locale
import java.util.function.Supplier


// I could use an enum but then I would lose .value ...
sealed interface EnvironmentValue {
    companion object {
        val ENVIRONMENT_CODEC =
            mapOf(
                "biome" to Biome.CODEC,
                "climate" to Climate.CODEC,
                "daytime" to Daytime.CODEC,
                "depth" to Depth.CODEC,
                "dimension" to Dimension.CODEC,
                "medium" to Medium.CODEC,
                "moon" to MoonPhase.CODEC,
                "structure" to Structure.CODEC,
                "weather" to Weather.CODEC,
            )
        // yes really, this is simple and low effort
        val ENVIRONMENT_STREAM_CODEC =
            mapOf(
                "biome" to Biome.STREAM_CODEC,
                "climate" to Climate.STREAM_CODEC,
                "daytime" to Daytime.STREAM_CODEC,
                "depth" to Depth.STREAM_CODEC,
                "dimension" to Dimension.STREAM_CODEC,
                "medium" to Medium.STREAM_CODEC,
                "moon" to MoonPhase.STREAM_CODEC,
                "structure" to Structure.STREAM_CODEC,
                "weather" to Weather.STREAM_CODEC,
            )
    }

    fun display(): Component

    val type: String

    data class Biome(
        val value: ResourceKey<net.minecraft.world.level.biome.Biome>
    ) : EnvironmentValue {

        override val type: String = "biome"
        override fun display(): Component {
            return (InlineBiomeData(value.location().toString())).displayWithTextAndInline()
        }

        companion object {
            val CODEC: Codec<Biome> =
                Codec.STRING.xmap(
                    { string ->
                        Biome(
                            ResourceKey.create(
                                Registries.BIOME,
                                ResourceLocation.parse(string)
                            )
                        )
                    },
                    { biome ->
                        biome.value.location().toString()
                    }
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Biome> =
                ByteBufCodecs.STRING_UTF8
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(
                        { string ->
                            Biome(
                                ResourceKey.create(
                                    Registries.BIOME,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { biome ->
                            biome.value.location().toString()
                        }
                    )
        }
    }

    data class Climate(
        val value: Float
    ) : EnvironmentValue {

        override val type: String = "climate"
        override fun display(): Component {
            return (InlineClimateData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<Climate> =
                Codec.FLOAT.xmap(
                    ::Climate,
                    Climate::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Climate> =
                ByteBufCodecs.FLOAT
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::Climate, Climate::value)
        }
    }

    data class Daytime(
        val value: Long
    ) : EnvironmentValue {

        override val type: String = "daytime"
        override fun display(): Component {
            return (InlineDaytimeData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<Daytime> =
                Codec.LONG.xmap(
                    ::Daytime,
                    Daytime::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Daytime> =
                ByteBufCodecs.VAR_LONG
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::Daytime, Daytime::value)
        }
    }

    data class Depth(
        val value: Int
    ) : EnvironmentValue {

        override val type: String = "depth"
        override fun display(): Component {
            return (InlineDepthData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<Depth> =
                Codec.INT.xmap(
                    ::Depth,
                    Depth::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Depth> =
                ByteBufCodecs.INT
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::Depth, Depth::value)
        }
    }

    data class Dimension(
        val value: ResourceKey<Level>
    ) : EnvironmentValue {

        override val type: String = "dimension"
        override fun display(): Component {
            return (InlineDimensionData(value.location().toString())).asText(true)
        }

        companion object {
            val CODEC: Codec<Dimension> =
                Codec.STRING.xmap(
                    { string ->
                        Dimension(
                            ResourceKey.create(
                                Registries.DIMENSION,
                                ResourceLocation.parse(string)
                            )
                        )
                    },
                    { dimension ->
                        dimension.value.location().toString()
                    }
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Dimension> =
                ByteBufCodecs.STRING_UTF8
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(
                        { string ->
                            Dimension(
                                ResourceKey.create(
                                    Registries.DIMENSION,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { dimension ->
                            dimension.value.location().toString()
                        }
                    )
        }
    }

    data class Medium(
        val value: Int
    ) : EnvironmentValue {

        override val type: String = "medium"
        override fun display(): Component {
            return (InlineMediumData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<Medium> =
                Codec.INT.xmap(
                    ::Medium,
                    Medium::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Medium> =
                ByteBufCodecs.INT
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::Medium, Medium::value)
        }
    }

    data class MoonPhase(
        val value: Int
    ) : EnvironmentValue {

        override val type: String = "moon"
        override fun display(): Component {
            return (InlineMoonData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<MoonPhase> =
                Codec.INT.xmap(
                    ::MoonPhase,
                    MoonPhase::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MoonPhase> =
                ByteBufCodecs.INT
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::MoonPhase, MoonPhase::value)
        }
    }

    data class Structure(
        val value: ResourceKey<net.minecraft.world.level.levelgen.structure.Structure>
    ) : EnvironmentValue {

        override val type: String = "structure"
        override fun display(): Component {
            return (InlineStructureData(value.location().toString())).asText(true)
        }

        companion object {
            val CODEC: Codec<Structure> =
                Codec.STRING.xmap(
                    { string ->
                        Structure(
                            ResourceKey.create(
                                Registries.STRUCTURE,
                                ResourceLocation.parse(string)
                            )
                        )
                    },
                    { structure ->
                        structure.value.location().toString()
                    }
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Structure> =
                ByteBufCodecs.STRING_UTF8
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(
                        { string ->
                            Structure(
                                ResourceKey.create(
                                    Registries.STRUCTURE,
                                    ResourceLocation.parse(string)
                                )
                            )
                        },
                        { structure ->
                            structure.value.location().toString()
                        }
                    )
        }
    }

    data class Weather(
        val value: Int
    ) : EnvironmentValue {

        override val type: String = "weather"
        override fun display(): Component {
            return (InlineWeatherData(value)).asText(true)
        }

        companion object {
            val CODEC: Codec<Weather> =
                Codec.INT.xmap(
                    ::Weather,
                    Weather::value
                )

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Weather> =
                ByteBufCodecs.INT
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .map(::Weather, Weather::value)
        }
    }
}

// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
class RealEnvironmentIota(
    val value: EnvironmentValue
) : Iota(Supplier { FishcastingIotaTypes.ENVIRONMENT.value }) {

    override fun isTruthy(): Boolean {
        return true
    }

    override fun toleratesOther(otherIota: Iota?): Boolean {
        return otherIota is RealEnvironmentIota
    }

    // todo: get display based on type
    override fun display(): Component {
        return value.display()
    }

    override fun hashCode(): Int {
        return 0
    }

    companion object {

        var TYPE: IotaType<RealEnvironmentIota> = object : IotaType<RealEnvironmentIota>() {

            override fun validate(
                iota: RealEnvironmentIota?,
                level: ServerLevel
            ): Boolean {
                return iota != null && true // iota.isValid()
            }.

            val CODEC: MapCodec<EnvironmentValue> = Codec.STRING
                .dispatchMap(
                    "environment_type",
                    EnvironmentValue::type
                ) { type ->
                    ENVIRONMENT_CODEC[type]?.fieldOf(type)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EnvironmentValue> = ByteBufCodecs.STRING_UTF8
                    .mapStream<RegistryFriendlyByteBuf> { it }
                    .dispatch(
                        EnvironmentValue::type
                    ) { type ->
                        ENVIRONMENT_STREAM_CODEC[type]
                    }

            override fun codec(): MapCodec<RealEnvironmentIota> =
                CODEC.xmap(
                    ::RealEnvironmentIota,
                    RealEnvironmentIota::value
                )

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, RealEnvironmentIota> =
                STREAM_CODEC.map(
                    ::RealEnvironmentIota,
                    RealEnvironmentIota::value
                )

            override fun color(): Int {
                return COLOR
            }

            val COLOR = 0x82add9
        }
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as RealEnvironmentIota

        return true // value == iotaToCompare.value
    }
}