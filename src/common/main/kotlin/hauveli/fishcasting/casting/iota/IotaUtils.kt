package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import com.li64.tide.data.fishing.FishData
import net.minecraft.server.level.ServerLevel


fun List<Iota>.getFishEntity(level: ServerLevel, idx: Int, argc: Int = 0): FishData {
    val entity = this.getEntity(level, idx, argc)
    val possiblyFish = FishData.get(entity)
    if (possiblyFish.isPresent) {
        return possiblyFish.get()
    } else {
        throw MishapBadEntity.of(entity, "fish")
    }
}


fun List<Iota>.getEnvironment(idx: Int, argc: Int = 0): EnvironmentValue {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is RealEnvironmentIota) {
        return x.value
    } else {
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "environment")
    }
}

fun List<Iota>.getEnvironmentIota(idx: Int, argc: Int = 0): RealEnvironmentIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is RealEnvironmentIota) {
        return x
    } else {
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "environment")
    }
}


fun List<Iota>.getBiome(idx: Int, argc: Int = 0): EnvironmentValue.Biome {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Biome) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_biome")
    }
}

fun List<Iota>.getClimate(idx: Int, argc: Int = 0): EnvironmentValue.Climate {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Climate) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_climate")
    }
}

fun List<Iota>.getDaytime(idx: Int, argc: Int = 0): EnvironmentValue.Daytime {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Daytime) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_daytime")
    }
}

fun List<Iota>.getDepth(idx: Int, argc: Int = 0): EnvironmentValue.Depth {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Depth) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_depth")
    }
}

fun List<Iota>.getDimension(idx: Int, argc: Int = 0): EnvironmentValue.Dimension {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Dimension) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_dimension")
    }
}

fun List<Iota>.getMedium(idx: Int, argc: Int = 0): EnvironmentValue.Medium {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Medium) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_medium")
    }
}

fun List<Iota>.getMoonPhase(idx: Int, argc: Int = 0): EnvironmentValue.MoonPhase {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.MoonPhase) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_moon_phase")
    }
}

fun List<Iota>.getStructure(idx: Int, argc: Int = 0): EnvironmentValue.Structure {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Structure) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_structure")
    }
}

fun List<Iota>.getWeather(idx: Int, argc: Int = 0): EnvironmentValue.Weather {
    val env = this.getEnvironmentIota(idx, argc)
    if (env.value is EnvironmentValue.Weather) {
        return env.value
    } else {
        throw MishapInvalidIota.ofType(env, if (argc == 0) idx else argc - (idx + 1), "environment_weather")
    }
}



