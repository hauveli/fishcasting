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

object OpAttachBobber : SpellAction {
    override val argc = 2


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
        val serverLevel = env.world

        // I think these do the entity Iota check for me
        val maybeBobberEntity = args.getEntity(serverLevel, 0) // first? I think args must have 1 argument so this is safe?
        val maybeTargetEntity = args.getEntity(serverLevel, 1)
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

        // how do I check if a target is hookable?
        if (!maybeTargetEntity.canBeHitByProjectile()) {
            throw MishapBadEntity.of(maybeTargetEntity, "fishcasting.fishing_hookable")
        }

        // Too far, only check if not owned by self
        if (caster is ServerPlayer && !maybeBobberEntity.playerOwner.`is`(caster)) {
            env.assertEntityInRange(maybeBobberEntity)
        }

        // These should not error, as they are expected behaviour despite returning null
        val justReeled = getFishOnHook(maybeBobberEntity)
        val haul = maybeBobberEntity.hookedIn
        if (justReeled != null || haul != null) {
            // throw MishapAlreadyHooked() // "expected a place to attach an entity, found [other entity]"? "The bobber rejected the []"? "The hook
        }

        if (!maybeBobberEntity.boundingBox.intersects(maybeTargetEntity.boundingBox)) {
            throw MishapEntityTooFarAway(maybeTargetEntity)
        }

        return SpellAction.Result(
            Spell(maybeBobberEntity, maybeTargetEntity),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(maybeTargetEntity.position().add(0.0, maybeTargetEntity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }


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

    private data class Spell(val bobber: TideFishingHook, val target: Entity) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            (bobber as SetHookedEntityTideFishingHookAccessor).`fishcasting$setHookedEntity`(target)
        }
    }
}