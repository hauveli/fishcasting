package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

object OpGetDimension : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        // return listOf(DimensionIota(env.world.dimension()))
        var envWorld: Level = env.world
        val caster = env.castingEntity
        if (caster != null) {
            env.assertEntityInRange(caster)
            envWorld = caster.level()
        }
        return listOf(RealEnvironmentIota(EnvironmentValue.Dimension(envWorld.dimension())))
    }
}