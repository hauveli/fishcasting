package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import hauveli.fishcasting.casting.iota.DaytimeIota
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import net.minecraft.world.level.Level

object OpGetDayTime : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        var envWorld: Level = env.world
        val caster = env.castingEntity
        if (caster != null) {
            env.assertEntityInRange(caster)
            envWorld = caster.level()
        }

        // this is perhaps mean, but I'm leaving this without a safety check because if there is an error, I want to know.
        val dayTime = envWorld.dayTime

        val dayTimeEnv = EnvironmentValue.Daytime(dayTime)
        return listOf(RealEnvironmentIota(dayTimeEnv))
        // return listOf(DaytimeIota(dayTime))
        //return listOf(DoubleIota(dayTime.toDouble()))
    }
}