package hauveli.fishcasting.features.chair

import com.li64.tide.Tide
import com.li64.tide.data.player.TidePlayerData
import com.li64.tide.network.messages.OpenJournalMsg
import hauveli.fishcasting.registry.FishcastingItems
import hauveli.fishcasting.registry.FishcastingEntities
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.ChestBoat
import net.minecraft.world.item.Item
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
                // if player is looking at the WEST side of the chair, open up the journal
                // Uhhh get vector boat is facing, get player's look vector, (both unit vectors)
                // measure Z component? no, then it's worthless.
                // options: dot product, cross product
                // dot product gives me how parallel they are. if they are more or less parallel I don't care
                // when are they not parallel?
                // in the negative space of two opposite pyramids/cones, but I only care about one quarter
                // how to determine which quarter I am looking at?
                // I should re-evaluate my approach but whatever
                // can I just obtain the normal of the side of the boat?
                // I could take the position of the boat, the lookdir and up to get a vector orthogonal and then reduce
                // the number of pyramids/cones I care about to just two, meaning I only need to check am I on the left or the right?
                // can check if we are within 0.6+? to see if we are looking at the right spot
                // god I'm so glad I know at least a bit of math this was a for-once satisfying implementation.
                // TL;DR this code first reduces the valid region to two cone-ish shapes (defined by a scalar, in this case 0.6) relative to the boat's facing direction
                // then reduces it to a narrow cone on just one side because we only look at values greater than 0.6
                val doubleCone = this.getUpVector(0f).cross(this.getLookAngle()) // idk what to use for dt
                if (doubleCone.dot(player.getLookAngle()) > 0.6) {
                    openJournal(player)
                    return interactionresult1
                }

                this.gameEvent(GameEvent.CONTAINER_OPEN, player)
                PiglinAi.angerNearbyPiglins(player, true)
            }

            return interactionresult1
        }
    }

    private fun openJournal(player: Player) {
        // I'm a big noobert and couldnt figure out how to give these stats
        /*
        ItemStack defaultJournalItemStack = (new FishingJournalItem(new Item.Properties())).getDefaultInstance();
        CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, defaultJournalItemStack);
        serverPlayer.awardStat(Stats.ITEM_USED.get(defaultJournalItemStack.getItem()));
         */
        if (player.level().isClientSide()) {
            return
        }
        val serverPlayer = player as ServerPlayer
        TidePlayerData.getOrCreate(serverPlayer).syncTo(serverPlayer)
        Tide.NETWORK.sendToPlayer(OpenJournalMsg(), serverPlayer)
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