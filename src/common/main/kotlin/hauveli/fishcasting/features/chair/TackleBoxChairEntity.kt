package hauveli.fishcasting.features.chair

import com.li64.tide.Tide
import com.li64.tide.config.TideServerConfig
import com.li64.tide.data.player.TidePlayerData
import com.li64.tide.network.messages.OpenJournalMsg
import com.li64.tide.registries.TideItems
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.levelgen.structure.BoundingBox
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
        if (variant?.id == 1)
            return FishcastingItems.TACKLEBOX_CHAIR_AERONAUTICS.value
        else
            return FishcastingItems.TACKLEBOX_CHAIR.value
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
        this.sendSystemMessage(Component.nullToEmpty("Wtf" + p0.type().toString()))
        if (p0.equals(DamageTypes.GENERIC)) {
            return false
        }
        return super.hurt(p0, p1)
    }

    override fun causeFallDamage(p0: Float, p1: Float, p2: DamageSource): Boolean {
        this.sendSystemMessage(Component.nullToEmpty("fall damage what" + p2.type().toString()))
        //return super.causeFallDamage(p0, p1, p2)
        return false
    }

    override fun checkFallDamage(p0: Double, p1: Boolean, p2: BlockState, p3: BlockPos) {
        this.sendSystemMessage(Component.nullToEmpty("nuh uh"))
        // super.checkFallDamage(p0, p1, p2, p3)
    }

    fun hover(player: Player?) {
        if (this.variant!!.id == 0)
            return
        if (player != null) {
            // tiny weight
            // how can I check if the player is standing on it?
            if (player.y < this.y + 0.3) // whatever the height is for the boat I forget, TODO: fix this to scale
                return
            if (player.position().subtract(this.position().add(0.0, 0.3, 0.0)).lengthSqr() > 1) // unsure what this should be so I'm guessing for now...
                return
            this.addDeltaMovement(Vec3(0.0, -0.01, 0.0))
            player.addDeltaMovement(this.deltaMovement.scale(PLAYER_REPULSION))
        }
        if (this.onGround() || this.isInLiquid) {
            this.addDeltaMovement(Vec3(0.0, 0.1, 0.0))
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

        val blockPos = blockPosition()
        var groundY: Double? = null

        for (y in 1..(HOVER_OFFSET+1)) {
            if (!level.getBlockState(blockPos.below(y)).isAir) {
                groundY = hoverOffsetY - y
                break
            }
        }

        if (groundY != null) {
            val diff = HOVER_OFFSET / (hoverOffsetY - groundY)
            this.addDeltaMovement(Vec3(0.0, 0.01 * (diff * diff), 0.0))
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
        return super.makeBoundingBox().deflate(11.0 / 32.0, 1.0 / 32.0, 11.0 / 32.0)
        //return AABB.ofSize(Vec3.ZERO, 0.4, 0.4, 0.4)
    }

    override fun getGroundFriction(): Float {
        return super.getGroundFriction() * 0.1f // super slow
    }

    override fun getBlockSpeedFactor(): Float {
        return super.getBlockSpeedFactor() * 0.1f
    }


    // thank you kaupenjoe
    /* VARIANT */
    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(VARIANT, 0)
    }

    private val typeVariant: Int
        get() = this.entityData.get(VARIANT)

    var variant: TackleBoxChairVariant?
        get() = TackleBoxChairVariant.byId(this.typeVariant and 255) // why does mojang check if we have more than 255 variants? kaupenjoe does too, I assume it's something obscure or a silly choice and in either case it matters little
        private set(variant) {
            this.entityData.set(VARIANT, variant!!.id and 255)
        }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        super.addAdditionalSaveData(compoundTag)
        compoundTag.putInt("Variant", this.typeVariant)
    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        super.readAdditionalSaveData(compoundTag)
        this.entityData.set<Int>(VARIANT, compoundTag.getInt("Variant"))
    }

    companion object {

        private const val PLAYER_REPULSION = 0.5
        private const val HOVER_OFFSET = 2
        // thank you kaupenjoe
        private val VARIANT: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(TackleBoxChairEntity::class.java, EntityDataSerializers.INT)


        fun setVariant(chair: TackleBoxChairEntity, variant: TackleBoxChairVariant, level: Level) {
            if (!level.isClientSide) {
                chair.variant = variant
            }
        }
    }
}