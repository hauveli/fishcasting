package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.FishData
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.Fishcasting
import net.minecraft.world.entity.item.ItemEntity


object OpGetBobbersCatch : ConstMediaAction {
    override val argc = 1
    override val mediaCost: Long = MediaConstants.SHARD_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())

        val unknownIota: Iota = args[0] // first? I think args must have 1 argument so this is safe?
        // Not an entity
        if (unknownIota !is EntityIota) {
            throw MishapInvalidIota.ofType(unknownIota, argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

        // Not a hook
        if (unknownEntity !is TideFishingHook) {
            throw MishapBadEntity.of(unknownEntity, "fishcasting.fishing_hook")
        }

        val target = unknownEntity
        // Too far, only check if not owned by self
        if (!target.playerOwner.`is`(caster)) {
            env.assertEntityInRange(target)
        }

        // These should not error, as they are expected behaviour despite returning null
        val justReeled = getFishOnHook(target)
        val haul = target.hookedIn
        if (justReeled != null) {
            return listOf<Iota>(EntityIota(justReeled))
        } else if (haul != null) {
            return listOf<Iota>(EntityIota(haul))
        }
        return listOf<Iota>(NullIota()) // no catch
    }

    // This is specifically for checking if a fish was just reeled in this exact same tick
    // and if the bobber is on top of it
    // I imagine if the bobber is inside an ItemEntity of a Fish that was reeled up the same tick
    // that the player calls "check what is on the hook" the player expects the returned value to be
    // the ItemEntity of the fish they just reeled in...
    // it will only do this if:
    // Fish was reeled (pulled from water/generated) that same tick
    // ex. throwing a fish onto the bobber and calling "get whats on bobber" will not actually get it
    // unless it is properly caught on it.
    // I am making this possibly opinionated exception because I think it makes sense
    // The undefined behaviour here (?) is if you somehow get your bobber to be in a minigame
    // also be hooked on an entity
    // then reel in
    // I do not know if this is possible though...
    fun getFishOnHook(hook: TideFishingHook): ItemEntity? {
        // certain identify the correct entity?
        for (fishbert in hook.level().getEntitiesOfClass(
            ItemEntity::class.java,
            hook.boundingBox.inflate(10.0)
        )) {
            // TODO: somehow obtain when player began fishing minigame?
            val marginOfError = 500
            val opData = FishData.get(fishbert.item)
            if (fishbert.tickCount == 0
                && fishbert.tags
                    .containsAll(listOf<String>(Fishcasting.FISHBERT_TAG, hook.playerOwner.getStringUUID()))
            ) {
                return fishbert
            }
        }
        return null
    }
}