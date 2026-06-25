package hauveli.fishcasting.features.chair

import com.li64.tide.Tide
import com.li64.tide.data.player.TidePlayerData
import com.li64.tide.network.messages.OpenJournalMsg
import com.li64.tide.registries.TideItems
import com.li64.tide.registries.items.FishingJournalItem
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingEntities
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.ChestBoat
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3


class TackleBoxChairEntity : ChestBoat {
    constructor(type: EntityType<out TackleBoxChairEntity>, level: Level) : super(type, level) {
        this.stuckSpeedMultiplier = Vec3.ZERO
        this.setPaddleState(false, false)
    }

    constructor(level: Level, x: Double, y: Double, z: Double) : super(level, x, y, z)

    override fun getPassengerAttachmentPoint(entity: Entity, dimensions: EntityDimensions, partialTick: Float): Vec3 {
        val f = this.singlePassengerXOffset
        return (Vec3(
            0.0,  // Left/Right ?
            (dimensions.height() * 1.1f).toDouble(),  // up/down
            f.toDouble()
        )).yRot(-this.yRot * (Math.PI.toFloat() / 180f)) // Forward/Backward + rotation?
    }

    override fun getSinglePassengerXOffset(): Float {
        return -3.0f / 16.0f
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        // super.interact(player, hand);
        if (!player.isSecondaryUseActive) {
            val interactionresult = super.interact(player, hand)
            if (interactionresult != InteractionResult.PASS) {
                return interactionresult
            }
        }

        if (this.canAddPassenger(player) && !player.isSecondaryUseActive) {
            return InteractionResult.PASS
        } else {
            val interactionresult1 = this.interactWithContainerVehicle(player)
            if (interactionresult1.consumesAction()) {
                // todo: not perfect
                val doubleCone = this.getUpVector(0f).cross(this.lookAngle) // idk what to use for dt
                if (doubleCone.dot(player.lookAngle) > 0.6) {
                    openJournal(player)
                    return interactionresult1
                }

                this.gameEvent(GameEvent.CONTAINER_OPEN, player)
                PiglinAi.angerNearbyPiglins(player, true)
            }

            return interactionresult1
        }
    }

    val journalItem: Item = TideItems.FISHING_JOURNAL
    private fun openJournal(player: Player) {
        if (player.level().isClientSide()) {
            return
        }
        val serverPlayer = player as ServerPlayer
        TidePlayerData.getOrCreate(serverPlayer).syncTo(serverPlayer)
        Tide.NETWORK.sendToPlayer(OpenJournalMsg(), serverPlayer)
        CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, journalItem.defaultInstance)
        serverPlayer.awardStat(Stats.ITEM_USED.get(journalItem))
    }

    override fun getDropItem(): Item {
        return FishcastingItems.TACKLEBOX_CHAIR as Item
    }

    override fun getType(): EntityType<*> {
        return FishcastingEntities.TACKLEBOX_CHAIR
    }

    override fun clampRotation(entityToUpdate: Entity) {
        super.clampRotation(entityToUpdate)
    }

    override fun canControlVehicle(): Boolean {
        return false
    }

    override fun dismountsUnderwater(): Boolean {
        return false
    }

    override fun makeBoundingBox(): AABB {
        return super.makeBoundingBox()
    }

    override fun getGroundFriction(): Float {
        return super.getGroundFriction() * 0.1f // super slow
    }

    override fun getBlockSpeedFactor(): Float {
        return super.getBlockSpeedFactor() * 0.1f
    }
}