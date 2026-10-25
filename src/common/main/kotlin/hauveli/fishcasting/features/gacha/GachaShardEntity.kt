package hauveli.fishcasting.features.gacha

import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.core.Direction
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3


class GachaShardEntity : ThrowableItemProjectile {

    companion object {
        val LANDED = SynchedEntityData.defineId(
            GachaShardEntity::class.java, // is this ok...
            EntityDataSerializers.BOOLEAN
        )

        private const val MAX_LIFETIME = 20 * 60 * 5 // I think that it's long enough to be kind of worth considering where you toss them, but short enough to be a massive headache
        private const val DAMAGE_INTERVAL = 10
        private const val DAMAGE_AMOUNT = 1.0f
    }

    private var lifetime = 0
    private var damageCooldown = 0

    val landedRotX: Float
    val landedRotY: Float
    init {
        if (this.level().isClientSide) {
            landedRotX = (this.uuid.mostSignificantBits % 360) * 1f
            landedRotY = (this.uuid.leastSignificantBits % 360) * 1f
        } else {
            landedRotX = 0f
            landedRotY = 0f
            // It won't sync hasLanded otherwise, but it means I have to set it to false after adding it to the world...
            // todo: figure out a less jank way to do this...
            if (this.deltaMovement == Vec3.ZERO) {
                entityData.set(LANDED, true)
            }
        }
    }

    constructor(entityType: EntityType<out GachaShardEntity>, level: Level) : super(entityType, level)

    constructor(entityType: EntityType<out GachaShardEntity>, x: Double, y: Double, z: Double, level: Level) : super(entityType, x, y, z, level)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)

        builder.define(LANDED, false)
    }

    // I'm so confused as to why the ThrowableItemProjectile orients itself to face the same direction...
    val hasLanded: Boolean get() = entityData.get(LANDED)

    fun shatter() {
        val level = this.level()
        if (level.isClientSide) return
        if (level !is ServerLevel) return
        level.sendParticles(
            ItemParticleOption(
                ParticleTypes.ITEM,
                FishcastingItems.GLASS_SHARD.value.defaultInstance
            ),
            x, y, z,
            15,
            0.3, 0.3, 0.3,
            0.2
        )
        level.playSound(
            null,
            this.blockPosition(),
            SoundEvents.GLASS_BREAK,
            SoundSource.BLOCKS,
            0.2f, // volume
            0.8f + random.nextFloat() * 0.4f
        )
        discard()

    }

    override fun onExplosionHit(p0: Entity?) {
        super.onExplosionHit(p0)
        shatter()
    }

    //  I thought this would be on player hitting it but of course it's on projectile (itself) hitting something...
    override fun onHit(p0: HitResult) {
        super.onHit(p0)
        if (random.nextBoolean())
            shatter()
    }

    override fun isAttackable(): Boolean {
        return true // super.isAttackable()
    }

    override fun skipAttackInteraction(p0: Entity): Boolean {
        return false // super.skipAttackInteraction(p0)
    }

    override fun hurt(p0: DamageSource, p1: Float): Boolean {
        shatter()
        return super.hurt(p0, p1)
    }

    override fun tick() {
        if (!hasLanded) {
            super.tick() // does the client have to call this too? I'm kind of unsure but it is really janky if it doesn't...
        }
        val level = this.level()
        if (level.isClientSide) return
        if (level !is ServerLevel) return

        lifetime++

        if (lifetime >= MAX_LIFETIME) {
            shatter()
            return
        }

        if (hasLanded) {
            if (!level.getBlockState(this.blockPosition().below()).isSolid) {
                isNoGravity = false
                entityData.set(LANDED, false)
            }
        }

        maybeDamageNearbyMovingEntities(level)
    }

    private fun damageEntity(target: Entity) {
        if (target is LivingEntity
            && damageCooldown == 0) { // I could use lte here...
            target.hurt(level().damageSources().cactus(), DAMAGE_AMOUNT) // todo: custom damage source? "pricked to death" I think is accurate, but it could be something else...
            this.deltaMovement = this.deltaMovement.scale(0.1)

            lifetime += 500
            damageCooldown = DAMAGE_INTERVAL
        }
    }

    private fun maybeDamageNearbyMovingEntities(level: ServerLevel) {
        if (damageCooldown > 0) {
            damageCooldown--
            return
        }

        val box = boundingBox.inflate(0.15, 0.1, 0.15)

        val targets = level.getEntitiesOfClass(
            LivingEntity::class.java,
            box
        )

        for (target in targets) {
            if (!target.isAlive) continue

            // ugh I can't figure out a good way to check if the entity is moving or not...
            if (target.deltaMovement.lengthSqr() > 0.001f) {
                damageEntity(target)
                // I kind of want these to go away quite easily...
                if (random.nextBoolean())
                    shatter()
                break
            }
        }
    }

    override fun onHitBlock(result: BlockHitResult) {
        // if (level().isClientSide) return
        if (this.hasLanded) return

        if (result.direction != Direction.UP) {
            shatter()
            return
        }

        setPos(
            x,
            result.blockPos.y + 1.01,
            z
        )

        deltaMovement = Vec3.ZERO
        isNoGravity = true

        yRot = random.nextFloat() * 360f
        xRot = random.nextFloat() * 360f

        entityData.set(LANDED, true)
    }

    override fun onHitEntity(result: EntityHitResult) {
        if (level().isClientSide) return

        damageEntity(result.entity)
    }

    // I really don't get what this is for... todo: figure it out and maybe get rid of the GLASS_SHARD item for good...
    override fun getDefaultItem(): Item = FishcastingItems.GLASS_SHARD.value
}