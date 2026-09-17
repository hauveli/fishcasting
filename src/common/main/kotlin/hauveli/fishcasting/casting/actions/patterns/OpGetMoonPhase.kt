package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import net.minecraft.world.entity.Entity

object OpGetMoonPhase : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())

        val moonPhase = getMoonPhase(caster)

        return listOf(MoonPhaseIota(moonPhase))
    }

    @JvmStatic
    fun getMoonPhase(entity: Entity): Int {
        val level = entity.level()

        return level.moonPhase
    }
}