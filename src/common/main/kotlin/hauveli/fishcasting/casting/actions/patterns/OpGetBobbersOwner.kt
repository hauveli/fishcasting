package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook

object OpGetBobbersOwner : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.SHARD_UNIT // Should cost at least something, I feel like, but not much.

    // I don't understand why java complains where kotlin is happy
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())
        val unknownIota: Iota = args[0]

        // Not an entity
        if (unknownIota !is EntityIota) {
            throw MishapInvalidIota.ofType(unknownIota, argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

        // Not a player
        if (unknownEntity !is TideFishingHook) {
            throw MishapBadEntity.of(unknownEntity, "fishcasting.fishing_hook")
        }

        val target = unknownEntity
        // Too far, only check if not owned by self
        if (!target.playerOwner.`is`(caster)) {
            env.assertEntityInRange(target)
        }

        // These should not error, as they are expected behaviour despite returning null
        val owner = target.owner ?: return listOf(NullIota())

        return listOf(EntityIota(owner))
    }

}
