package hauveli.fishcasting.features.chair.secret

import com.li64.tide.Tide
import com.li64.tide.config.TideServerConfig
import com.li64.tide.data.player.TidePlayerData
import com.li64.tide.network.messages.OpenJournalMsg
import com.li64.tide.registries.TideItems
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.features.chair.TackleBoxChairVariant
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.ChestBoat
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3


class TackleBoxChairSecretEntity : ChestBoat {
    constructor(type: EntityType<out TackleBoxChairSecretEntity>, level: Level) : super(type, level) {
        this.stuckSpeedMultiplier = Vec3.ZERO
        this.setPaddleState(false, false)
    }

    override fun getPassengerAttachmentPoint(entity: Entity, dimensions: EntityDimensions, partialTick: Float): Vec3 {
        val f = this.singlePassengerXOffset
        return (Vec3(
            0.0,  // Left/Right ?
            (dimensions.height() * 1.6f).toDouble(),  // up/down
            f.toDouble()
        )).yRot(-this.yRot * (Math.PI.toFloat() / 180f)) // Forward/Backward + rotation?
    }

    override fun getSinglePassengerXOffset(): Float {
        return -3.0f / 16.0f
    }

    override fun getDropItem(): Item {
        return FishcastingItems.TACKLEBOX_CHAIR.value // FishcastingItems.TACKLEBOX_CHAIR_SECRET.value
    }

    override fun getType(): EntityType<*> {
        return FishcastingEntities.TACKLEBOX_CHAIR.value
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

    /*
    override fun canBeCollidedWith(): Boolean {
        return super.canBeCollidedWith()
    }
     */

    override fun hurt(p0: DamageSource, p1: Float): Boolean {
        if (p0.equals(DamageTypes.GENERIC)) { // I was trying to fix the weird boat -> planks+sticks bug with this...
            return false
        }
        return super.hurt(p0, p1)
    }

    override fun causeFallDamage(p0: Float, p1: Float, p2: DamageSource): Boolean {
        Fishcasting.LOGGER.info("Report to developer please; Somehow took fall damage: ${p2.msgId} ${p2.type()}")
        //return super.causeFallDamage(p0, p1, p2)
        return false
    }

    override fun checkFallDamage(p0: Double, p1: Boolean, p2: BlockState, p3: BlockPos) {
        // super.checkFallDamage(p0, p1, p2, p3)
    }

    fun hover(player: Player?) {
        if (this.onGround() || this.isInLiquid) {
            return
        }
        val level = this.level()
        val dim = level.dimension().location().toString()
        val entry = Tide.SERVER_CONFIG.general.fishableVoidHeights.find { it.dimension == dim }
        val hoverOffsetY = this.y - HOVER_OFFSET
        if (entry != null) {
            val actualY = when (entry.type) {
                TideServerConfig.General.VoidHeightEntry.Type.ABSOLUTE -> {
                    entry.height
                }

                TideServerConfig.General.VoidHeightEntry.Type.RELATIVE_TO_BOTTOM -> {
                    level.minBuildHeight + entry.height
                }

                TideServerConfig.General.VoidHeightEntry.Type.RELATIVE_TO_TOP -> {
                    level.maxBuildHeight + entry.height
                }
            }

            if (actualY > hoverOffsetY) {
                val diff = actualY - hoverOffsetY
                this.addDeltaMovement(Vec3(0.0, 0.002 * (diff * diff), 0.0))
                return
            }
        }
    }

    override fun baseTick() {
        super.baseTick()
        //hover()
    }

    override fun rideTick() {
        super.rideTick()
        //hover()
    }

    override fun playerTouch(player: Player) {
        super.playerTouch(player)
        hover(player)
    }

    override fun tick() {
        super.tick()
        hover(null)
    }

    override fun makeBoundingBox(): AABB {
        // dims are 11.0f / 16.0f, 8.0f / 16.0f
        // 22 / 16 and 9/16
        return super.makeBoundingBox().deflate(32.0 / 32.0, 1.0 / 32.0, 11.0 / 32.0)
        //return AABB.ofSize(Vec3.ZERO, 0.4, 0.4, 0.4)
    }

    override fun getGroundFriction(): Float {
        return super.getGroundFriction() * 2.1f // super slow
    }

    override fun getBlockSpeedFactor(): Float {
        return super.getBlockSpeedFactor() * 2.1f
    }

    companion object {

        private const val PLAYER_REPULSION = 0.5
        private const val HOVER_OFFSET = 2
    }
}