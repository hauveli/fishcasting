package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.DepthIota
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.FishIota
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.StructureIota
import hauveli.fishcasting.casting.iota.WeatherIota

// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/common/lib/hex/HexIotaTypes.java
object FishcastingIotaTypes : FishcastingRegistrar<IotaType<*>>(
    HexRegistries.IOTA_TYPE,
    { HexIotaTypes.REGISTRY }
) {

    // this mega sucks I feel like, I would prefer to have them all just be in my EnvironmentIota, but making them all behave nicely is a bit hard then...
    // Maybe I just need to cut all the additional dummy Iota types, then implement arithmetic for the EnvironmentIota? hmm....
    val ENVIRONMENT = make("environment") { EnvironmentIota.TYPE }
    val FISH = make("fish") { FishIota.TYPE }
    val MOON_PHASE = make("moon") { MoonPhaseIota.TYPE }
    val WEATHER = make("weather") { WeatherIota.TYPE }
    val MEDIUM = make("medium") { MediumIota.TYPE }
    val DIMENSION = make("dimension") { DimensionIota.TYPE }
    val STRUCTURE = make("structure") { StructureIota.TYPE }
    val BIOME = make("biome") { BiomeIota.TYPE }
    val DEPTH = make("depth") { DepthIota.TYPE }

    private fun <T : IotaType<*>> make(name: String, builder: () -> T):
            FishcastingRegistrar<IotaType<*>>.Entry<T> {
        val registered = register(id(name), builder)
        return registered
    }
}