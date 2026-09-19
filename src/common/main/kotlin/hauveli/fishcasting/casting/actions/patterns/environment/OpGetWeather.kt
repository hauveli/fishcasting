package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object OpGetWeather : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val blockPos = args.getBlockPos(0, argc)
        env.assertPosInRange(blockPos)


        return listOf(WeatherIota(getWeather(env.world, blockPos).ordinal))
    }

    fun getWeather(level: ServerLevel, blockPos: BlockPos): WeatherType {
        if (!level.isRaining && !level.isThundering)
            return WeatherType.CLEAR

        // this is perhaps mean, but I'm leaving this without a safety check because if there is an error, I want to know.
        val isRainingAtPos = level.isRainingAt(blockPos)
        // hmmm I was considering something with blockPos but that would have to be precipitation, and if I implement
        // Biome check, then by using Biome+Weather it is possible to determine precipitation... So no BlockPos is needed...
        // val precipitation = level.getBiome(blockPos).value().getPrecipitationAt(blockPos)
        // can determine biome temperature too, probably, using the temperature spell...
        if (isRainingAtPos) {
            if (level.isRaining && !level.isThundering)
                return WeatherType.RAIN
            return WeatherType.STORM
        }
        return WeatherType.CLEAR
    }
}