package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.li64.tide.util.TideUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.EnvironmentValue.Companion.ENVIRONMENT_CODEC
import hauveli.fishcasting.casting.iota.EnvironmentValue.Companion.ENVIRONMENT_STREAM_CODEC
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
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import java.util.function.Supplier
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.sqrt

// todo: consider using this to re-implement all of these as their own Iota if I feel like it...
sealed interface EnvironmentValue {

    val type: String
    fun display(): Component
    fun getDouble(thisIota: RealEnvironmentIota? = null): Double
    fun of(newValue: Any): EnvironmentValue
    val UPPER_BOUND: Double?
    val LOWER_BOUND: Double?

    fun clampedInRange(inputValue: Double): Double {
        return inputValue.coerceIn(LOWER_BOUND, UPPER_BOUND)
    }

    fun getCodec(): Codec<out EnvironmentValue>
    fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue>

    data class Biome(
        val value: ResourceKey<net.minecraft.world.level.biome.Biome>,
        override val UPPER_BOUND: Double? = null,
        override val LOWER_BOUND: Double? = null
    ) : EnvironmentValue {

        override val type: String = "biome"
        override fun display(): Component {
            return (InlineBiomeData(value.location().toString())).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            throw MishapInvalidIota.ofType(thisIota!!, 0, "environment")
        }

        override fun of(newValue: Any): Biome {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            return Biome(newValue as ResourceKey<net.minecraft.world.level.biome.Biome>)
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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

    // practically, this only really needs -2 to +2 for minecrafts temperature thing...
    // what's Tide's temp range though?
    data class Climate(
        val value: Float,
        override val UPPER_BOUND: Double? = Float.MAX_VALUE.toDouble(),
        override val LOWER_BOUND: Double? = -Float.MAX_VALUE.toDouble()
    ) : EnvironmentValue {

        override val type: String = "climate"
        override fun display(): Component {
            return (InlineClimateData(value)).displayWithTextAndInline()
        }

        // this should give what the player sees on the iota Display
        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            return TideUtils.mcTempToRealTemp(value).toDouble()
        }

        override fun of(newValue: Any): Climate {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange(realTempToMcTemp((castValue)))
            return Climate(clampedValue.toFloat())
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
        }

        companion object {
            fun init() {}

            fun realTempToMcTemp(celsius: Double): Double {
                // x = mcTemp-0.23 <=>
                // c=11(x^3)+30x+21.9
                // ok I've triple checked I'm fairly sure this is correct but there are floating point errors....
                // mcTempToRealTemp(0) = 14.87 which is what I am seeing... why is it always 0?
                val a = (celsius - 21.9) / 22.0
                val b = (10.0 / 11.0).pow(3.0)
                val c = sqrt(a * a + b)

                val mcTemp = 0.23 +
                        cbrt(a + c) +
                        cbrt(a - c)
                return mcTemp
            }

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
        val value: Long,
        override val UPPER_BOUND: Double? = Long.MAX_VALUE.toDouble(),
        override val LOWER_BOUND: Double? = -Long.MAX_VALUE.toDouble()
    ) : EnvironmentValue {

        override val type: String = "daytime"
        override fun display(): Component {
            return (InlineDaytimeData(value)).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            return value.toDouble()
        }

        override fun of(newValue: Any): Daytime {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange((castValue))
            return Daytime(clampedValue.toLong())
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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

    // is this a problem if the world has a world border closer than 4 billion? I think it might be...
    // a problem for future me
    data class Depth(
        val value: Int,
        override val UPPER_BOUND: Double? = Int.MAX_VALUE.toDouble(),
        override val LOWER_BOUND: Double? = -Int.MAX_VALUE.toDouble()
    ) : EnvironmentValue {

        override val type: String = "depth"
        override fun display(): Component {
            return (InlineDepthData(value)).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            return value.toDouble()
        }

        override fun of(newValue: Any): Depth {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange((castValue))
            return Depth(clampedValue.toInt())
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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
        val value: ResourceKey<Level>,
        override val UPPER_BOUND: Double? = null,
        override val LOWER_BOUND: Double? = null
    ) : EnvironmentValue {

        override val type: String = "dimension"
        override fun display(): Component {
            return (InlineDimensionData(value.location().toString())).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            throw MishapInvalidIota.ofType(thisIota!!, 0, "environment")
        }

        override fun of(newValue: Any): Dimension {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            return Dimension(newValue as ResourceKey<Level>)
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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
        val value: Int,
        override val UPPER_BOUND: Double? = 0.0,
        override val LOWER_BOUND: Double? = 2.0
    ) : EnvironmentValue {

        override val type: String = "medium"
        override fun display(): Component {
            return (InlineMediumData(value)).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            throw MishapInvalidIota.ofType(thisIota!!, 0, "environment")
        }

        override fun of(newValue: Any): Medium {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange((castValue))
            return Medium(clampedValue.toInt())
        }

        fun mediumIdFromOrdinal(): FishingMedium {
            return FishingMedium.MEDIUMS[value]
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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
        val value: Int,
        override val UPPER_BOUND: Double? = 0.0,
        override val LOWER_BOUND: Double? = 7.0
    ) : EnvironmentValue {

        override val type: String = "moon"
        override fun display(): Component {
            return (InlineMoonData(value)).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            return value / 4.0 * Mth.PI
        }

        override fun of(newValue: Any): MoonPhase {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange((castValue))
            return MoonPhase(clampedValue.toInt())
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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
        val value: ResourceKey<net.minecraft.world.level.levelgen.structure.Structure>,
        override val UPPER_BOUND: Double? = null,
        override val LOWER_BOUND: Double? = null
    ) : EnvironmentValue {

        override val type: String = "structure"
        override fun display(): Component {
            return (InlineStructureData(value.location().toString())).displayWithTextAndInline()
        }

        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            throw MishapInvalidIota.ofType(thisIota!!, 0, "environment")
        }

        override fun of(newValue: Any): Structure {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            return Structure(newValue as ResourceKey<net.minecraft.world.level.levelgen.structure.Structure>)
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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
        val value: Int,
        override val UPPER_BOUND: Double? = 0.0,
        override val LOWER_BOUND: Double? = 2.0
    ) : EnvironmentValue {

        override val type: String = "weather"
        override fun display(): Component {
            return (InlineWeatherData(value)).displayWithTextAndInline()
        }

        // todo: maybe get length remaining here?
        override fun getDouble(thisIota: RealEnvironmentIota?): Double {
            throw MishapInvalidIota.ofType(thisIota!!, 0, "environment")
        }

        override fun of(newValue: Any): Weather {
            @Suppress("UNCHECKED_CAST") // todo: not fucking this, even if this is "fine"
            val castValue = newValue as Double
            val clampedValue = clampedInRange((castValue))
            return Weather(clampedValue.toInt())
        }

        fun weatherTypeFromOrdinal(): WeatherType {
            return WeatherType.entries[value]
        }

        override fun getCodec(): Codec<out EnvironmentValue> {
            return CODEC
        }

        override fun getStreamCodec(): StreamCodec<RegistryFriendlyByteBuf, out EnvironmentValue> {
            return STREAM_CODEC
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

    companion object {

        fun getCodec(type: String): Codec<out EnvironmentValue> {
            return when (type) {
                "biome" -> Biome.CODEC
                "climate" -> Climate.CODEC
                "daytime" -> Daytime.CODEC
                "depth" -> Depth.CODEC
                "dimension" -> Dimension.CODEC
                "medium" -> Medium.CODEC
                "moon" -> MoonPhase.CODEC
                "structure" -> Structure.CODEC
                "weather" -> Weather.CODEC
                else -> {throw Error("${Fishcasting.MODID}: How did we get here?")}
            }
        }

        val ENVIRONMENT_CODEC by lazy {
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
        }
        // yes really, this is simple and low effort
        val ENVIRONMENT_STREAM_CODEC by lazy {
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
    }
}

// todo: I think I'm repeating myself but maybe splitting it up into iota based on this one is reasonable
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

        fun init() {
            EnvironmentValue.Climate.init()
        }

        var TYPE: IotaType<RealEnvironmentIota> = object : IotaType<RealEnvironmentIota>() {

            override fun validate(
                iota: RealEnvironmentIota?,
                level: ServerLevel
            ): Boolean {
                // return  iota != null && iota.isValid()
                return super.validate(iota, level)
            }

            val CODEC: MapCodec<EnvironmentValue> = Codec.STRING
                .dispatchMap(
                    FishcastingIotaTypes.ENVIRONMENT.id.path,
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

    fun getDouble(): Double {
        return this.value.getDouble(this)
    }
}