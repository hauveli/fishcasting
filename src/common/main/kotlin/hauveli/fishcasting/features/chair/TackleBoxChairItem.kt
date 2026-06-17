package hauveli.fishcasting.features.chair

import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class TackleBoxChairItem(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        val hitResult: HitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY)
        if (hitResult.type == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack)
        } else {
            val vec3 = player.getViewVector(1.0f)
            val d0 = 5.0 // ????? what is this number
            val list = level.getEntities(
                player,
                player.boundingBox.expandTowards(vec3.scale(5.0)).inflate(1.0),
                ENTITY_PREDICATE
            )
            if (!list.isEmpty()) {
                val vec31 = player.eyePosition

                for (entity in list) {
                    val aabb = entity.boundingBox.inflate(entity.pickRadius.toDouble())
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemStack)
                    }
                }
            }

            if (hitResult.type == HitResult.Type.BLOCK) {
                val chair = this.getChair(level, hitResult, itemStack, player)
                chair.yRot = player.yRot
                if (!level.noCollision(chair, chair.boundingBox)) {
                    return InteractionResultHolder.fail(itemStack)
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(chair)
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation())
                        itemStack.consume(1, player)
                    }

                    player.awardStat(Stats.ITEM_USED.get(this))
                    return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
                }
            } else {
                return InteractionResultHolder.pass(itemStack)
            }
        }
    }

    private fun getChair(level: Level, hitResult: HitResult, stack: ItemStack, player: Player): TackleBoxChairEntity {
        val vec3 = hitResult.getLocation()
        val chair = TackleBoxChairEntity(level, vec3.x, vec3.y, vec3.z)
        if (level is ServerLevel) {
            EntityType.createDefaultStackConfig<Entity?>(level, stack, player).accept(chair)
        }

        return chair
    }

    companion object {
        private val ENTITY_PREDICATE: Predicate<Entity?> = EntitySelector.NO_SPECTATORS.and(Predicate { obj: Entity? -> obj!!.isPickable })
    }
}