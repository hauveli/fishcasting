package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.HookAccessor
import net.minecraft.world.entity.player.Player


object OpGetOwnersBobber : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT // Should cost at least something, I feel like, but not much.

    // I don't understand why java complains where kotlin is happy
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        // I dont understand why argc is used instead of getFirst/getLast but I will not break the convention
        // in favor of applying it for no reason at all and also likely incorrectly
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())
        val unknownIota: Iota = args[0]

        // Not an entity
        if (unknownIota !is EntityIota) {
            throw MishapInvalidIota.ofType(unknownIota, argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

        // Not a player
        if (unknownEntity !is Player) {
            throw MishapBadEntity.of(unknownEntity, "player")
        }

        val target = unknownEntity
        // Too far, only check if not owned by self
        if (!target.`is`(caster)) {
            env.assertEntityInRange(target)
        }

        // These should not error, as they are expected behaviour despite returning null
        val activeHook = HookAccessor.getHook(target) ?: return listOf(NullIota())

        val optEntity = activeHook.selfAndPassengers.findFirst()
        if (optEntity.isEmpty) {
            return listOf(NullIota())
        }

        return listOf(EntityIota(optEntity.get()))
    }
}