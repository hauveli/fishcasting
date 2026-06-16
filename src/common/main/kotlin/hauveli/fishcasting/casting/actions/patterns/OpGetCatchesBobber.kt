package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import net.minecraft.world.entity.Entity

object OpGetCatchesBobber : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.CRYSTAL_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())
        val unknownIota: Iota = args[0]

        // Not an entity
        if (unknownIota !is EntityIota) {
            throw MishapInvalidIota.ofType(unknownIota, argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)
        // Too far, we do NOT check if the entity is attached to our bobber despite it being a possibility.
        // the list of things that should be considered "in range" imo:
        // Self, hook
        env.assertEntityInRange(unknownEntity)

        // Not a player
        val hook = getFishingHook(unknownEntity) ?: return listOf(NullIota())

        return listOf(EntityIota(hook))
    }

    fun getFishingHook(entity: Entity): TideFishingHook? {
        val level = entity.level()

        // certain identify the correct entity?
        for (hook in level.getEntitiesOfClass(
            TideFishingHook::class.java,
            entity.boundingBox.inflate(32.0)
        )) {
            if (hook.hookedIn === entity) {
                return hook
            }
        }

        return null
    }
}