package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import hauveli.fishcasting.casting.iota.MoonPhaseIota

object OpGetMoonPhase : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val envWorld = env.world

        // this is perhaps mean, but I'm leaving this without a safety check because if there is an error, I want to know.
        val moonPhase = envWorld.moonPhase

        return listOf(MoonPhaseIota(moonPhase))
    }
}