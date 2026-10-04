package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.arithmetic.FishcastingEnvironmentArithmetic
import hauveli.fishcasting.casting.arithmetic.FishcastingFishArithmetic
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.ClimateIota
import hauveli.fishcasting.casting.iota.DaytimeIota
import hauveli.fishcasting.casting.iota.DepthIota
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.FishIota
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.casting.iota.StructureIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.Registry

// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/common/lib/hex/HexIotaTypes.java
object FishcastingArithmetic : FishcastingRegistrar<Arithmetic>(
    HexRegistries.ARITHMETIC,
    { HexArithmetics.REGISTRY }
) {

    // this mega sucks I feel like, I would prefer to have them all just be in my EnvironmentIota, but making them all behave nicely is a bit hard then...
    // Maybe I just need to cut all the additional dummy Iota types, then implement arithmetic for the EnvironmentIota? hmm....
    val FISH_ARITHMETIC = make("fish") { FishcastingFishArithmetic() }
    val ENVIRONMENT_ARITHMETIC = make("environment") { FishcastingEnvironmentArithmetic() }

    private fun <T : Arithmetic> make(name: String, builder: () -> T):
            FishcastingRegistrar<Arithmetic>.Entry<T> {
        val registered = register(id(name), builder)
        return registered
    }

    // todo: use the .arithName instead at some point in the future?
    /*
    fun registerArithmetic(arithmetic: () -> Arithmetic) {

        Registry.register(
            HexArithmetics.REGISTRY,
            Fishcasting.id(arithmeticInstance.arithName()),
            arithmeticInstance
        )
    }

     */
}