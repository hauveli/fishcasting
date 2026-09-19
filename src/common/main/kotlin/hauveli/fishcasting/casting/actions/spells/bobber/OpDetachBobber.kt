package hauveli.fishcasting.casting.actions.spells.bobber

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapEntityTooFarAway
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.mixin.SetHookedEntityTideFishingHookAccessor
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.projectile.FishingHook

object OpDetachBobber : SpellAction {
    override val argc = 2


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
        val serverLevel = env.world

        // I think these do the entity Iota check for me
        val maybeBobberEntity = args.getEntity(serverLevel, 0)
        // Not an entity
        /*
        if (maybeTargetEntityIota !is EntityIota) {
            throw MishapInvalidIota.ofType(maybeTargetEntityIota, OpGetBobbersCatch.argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

         */

        // Not a hook
        if (maybeBobberEntity !is TideFishingHook) {
            throw MishapBadEntity.of(maybeBobberEntity, "fishcasting.fishing_hook")
        }

        // Too far, only check if not owned by self
        if (caster is ServerPlayer && !maybeBobberEntity.playerOwner.`is`(caster)) {
            env.assertEntityInRange(maybeBobberEntity)
        }

        // These should not error, as they are expected behaviour despite returning null

        return SpellAction.Result(
            Spell(maybeBobberEntity),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.Companion.cloud(maybeBobberEntity.position().add(0.0, maybeBobberEntity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    private data class Spell(val bobber: TideFishingHook) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            (bobber as SetHookedEntityTideFishingHookAccessor).`fishcasting$setHookedEntity`(null)
        }
    }
}