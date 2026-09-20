package hauveli.fishcasting.casting.actions.spells.fish

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.item.TideItemData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity

object OpClobberFishToDeath : SpellAction {
    override val argc = 1


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val serverLevel = env.world

        // I think these do the entity Iota check for me
        val maybeFishEntity = args.getEntity(serverLevel, 0)
        env.assertEntityInRange(maybeFishEntity)
        // Not an entity
        /*
        if (maybeTargetEntityIota !is EntityIota) {
            throw MishapInvalidIota.ofType(maybeTargetEntityIota, OpGetBobbersCatch.argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

         */

        if (maybeFishEntity is ItemEntity) {
            // val isAlive = TideItemData.IS_BUCKETABLE.getOptional(maybeFishEntity.item)
            val length = TideItemData.FISH_LENGTH.getOptional(maybeFishEntity.item)
            if (length.isEmpty) {
                throw MishapBadEntity.of(maybeFishEntity, "fishcasting.not_alive")
            }
        } else if (maybeFishEntity !is FishLengthHolder) {
            throw MishapBadEntity.of(maybeFishEntity, "fishcasting.not_a_fish")
        }

        return SpellAction.Result(
            Spell(maybeFishEntity),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.Companion.cloud(maybeFishEntity.position().add(0.0, maybeFishEntity.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    fun killFishItemEntity(fishEntity: Entity) {
        if (fishEntity is ItemEntity) {
            // val length = TideItemData.FISH_LENGTH.getOptional(fishEntity.item)
            TideItemData.FISH_LENGTH.set(fishEntity.item, null)
            TideItemData.IS_BUCKETABLE.set(fishEntity.item, null)
        }
    }

    private data class Spell(val fishEntity: Entity) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            if (fishEntity is ItemEntity) {
                killFishItemEntity(fishEntity)
            } else {
                fishEntity.kill()
            }
        }
    }
}