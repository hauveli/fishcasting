package hauveli.fishcasting.casting.actions.patterns.bobber

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants

object OpGetMedium : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val envWorld = env.world

        // this is perhaps mean, but I'm leaving this without a safety check because if there is an error, I want to know.
        val dayTime = envWorld.dayTime

        return listOf(DoubleIota(dayTime.toDouble()))
    }
}