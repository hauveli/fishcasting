package hauveli.fishcasting.casting.actions.patterns.bobber

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.casting.iota.MediumIota
import net.minecraft.server.level.ServerPlayer

object OpGetMedium : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = env.world

        val unknownEntity = args.getEntity(serverLevel, 0, argc)

        // Not a player
        if (unknownEntity !is TideFishingHook) {
            throw MishapBadEntity.of(unknownEntity, "fishcasting.fishing_hook")
        }

        val target = unknownEntity
        // Too far, only check if not owned by self
        if (caster is ServerPlayer && !target.playerOwner.`is`(caster)) {
            env.assertEntityInRange(target)
        }

        val medium = target.currentMedium
        if (medium == null) {
            throw MishapBadLocation(target.position())
        }

        return listOf(MediumIota(MediumIota.Medium.of(medium).ordinal))
    }
}