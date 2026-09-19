package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import hauveli.fishcasting.features.natures_wrath.NaturesWrathSavedData
import net.minecraft.server.level.ServerPlayer

object OpGetNaturesWrath : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        if (caster !is ServerPlayer) {
            throw MishapBadCaster()
        }

        val ticksRemaining = NaturesWrathSavedData.ticksRemaining(caster).toDouble()

        return listOf(DoubleIota(ticksRemaining))
    }
}