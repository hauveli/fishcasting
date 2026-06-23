package hauveli.fishcasting.casting.actions.spells

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
import com.li64.tide.registries.TideEntityTypes.FISH_ENTITIES
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity


object OpItemifyFish : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(env.world, 0, argc)
        env.assertEntityInRange(target)
        if (target.type !in FISH_ENTITIES) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish.entity")
        }
        val maybeTideFish = FishData.get(target)
        if (maybeTideFish.isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish") // does this one even make sense?
        }

        return SpellAction.Result(
            Spell(target, maybeTideFish.get()),
            MediaConstants.SHARD_UNIT,
            listOf(ParticleSpray.cloud(target.position().add(0.0, target.eyeHeight / 2.0, 0.0), 1.0))
        )
    }

    private data class Spell(val target: Entity, val fishData: FishData) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {

            val itemEntity = ItemEntity(EntityType.ITEM, target.level())
            itemEntity.item = fishData.fish().value().defaultInstance // todo: dont just use default instance
            itemEntity.setPos(target.position())
            itemEntity.deltaMovement = target.deltaMovement

            if (target is FishLengthHolder) {
                TideItemData.FISH_LENGTH.set(itemEntity.item, target.`tide$getLength`())
            }
            target.level().addFreshEntity(itemEntity)
            target.discard()
        }
    }
}
