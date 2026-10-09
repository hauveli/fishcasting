package hauveli.fishcasting.features.fish.perhosgata

import at.petrak.hexcasting.common.lib.HexParticles
import com.li64.tide.registries.entities.fish.AbstractTideFish
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3


class PerhosgataEntity(
    entityType: EntityType<out PerhosgataEntity?>?,
    level: Level?
) : AbstractTideFish(entityType, level) {
    var renderingOnThisTickIsDone: Long = 0L

    // mechanics things as I find them
    override fun getAirSupply(): Int {
        return 10 // super.getAirSupply()
    }

    override fun calculateFallDamage(p0: Float, p1: Float): Int {
        return 0 // super.calculateFallDamage(p0, p1)
    }

    override fun isInLava(): Boolean {
        return false // super.isInLava()
    }

    override fun fireImmune(): Boolean {
        return true // super.fireImmune()
    }

    override fun onGround(): Boolean {
        return false //super.onGround()
    }

    override fun isInWater(): Boolean {
        return true // super.isInWater()
    }

    override fun isUnderWater(): Boolean {
        return true // super.isUnderWater()
    }

    override fun die(p0: DamageSource) {
        if (this.tickCount == 0 // this might seem weird but,
            && this.position().equals(Vec3.ZERO)) { // FishDisplays have the entity's tick count set to 0 and pos to Vec3.ZERO
            return
        }
        this.level().explode(this, this.x, this.y, this.z, 1.0f, Level.ExplosionInteraction.MOB)
        super.die(p0) // unsure if I wanna do this still...
    }

    override fun canBeCollidedWith(): Boolean {
        return false // super.canBeCollidedWith()
    }

    override fun isPushable(): Boolean {
        return false // super.isPushable()
    }

    override fun doPush(p0: Entity) {
        // super.doPush(p0)
    }

    // visual/sound stuff as I find them
    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.GENERIC_EXPLODE.value() // SoundEvents.EMPTY // super.getDeathSound()
    }

    override fun getHurtSound(p0: DamageSource): SoundEvent? {
        return SoundEvents.EMPTY // super.getHurtSound(p0)
    }

    override fun playHurtSound(p0: DamageSource) {
        // super.playHurtSound(p0)
    }

    override fun playStepSound(p0: BlockPos, p1: BlockState) {
        // super.playStepSound(p0, p1)
    }

    override fun doWaterSplashEffect() {
        // super.doWaterSplashEffect()
    }

    override fun canSpawnSprintParticle(): Boolean {
        return false // super.canSpawnSprintParticle()
    }

    override fun spawnSprintParticle() {
        // super.spawnSprintParticle()
    }

    override fun getFlopSound(): SoundEvent? {
        return SoundEvents.EMPTY // super.getFlopSound()
    }

    override fun getSwimSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimSound()
    }

    override fun getSwimSplashSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimSplashSound()
    }

    override fun getSwimHighSpeedSplashSound(): SoundEvent {
        return SoundEvents.EMPTY // super.getSwimHighSpeedSplashSound()
    }

    override fun travel(movement: Vec3) {
        if (this.isEffectiveAi) {
            this.move(MoverType.SELF, this.deltaMovement)
            this.deltaMovement = this.deltaMovement.scale(0.9)
            this.deltaMovement = this.deltaMovement.add(0.0, -0.0035, 0.0)
        } else super.travel(movement)
    }

    // todo: add goal to attract it to specific blocks (light sources)
    override fun registerGoals() {
        this.goalSelector.addGoal(0, PerhosgataRandomMovementGoal(this))
    }

    // this goal is from Tide's jelly fish
    internal class PerhosgataRandomMovementGoal(private val perhosgataEntity: PerhosgataEntity) : Goal() {
        override fun canUse(): Boolean {
            return true
        }

        override fun tick() {
            val rand = this.perhosgataEntity.getRandom()
            val timeSinceLastAction = this.perhosgataEntity.getNoActionTime()
            if (timeSinceLastAction > 100) {
                this.perhosgataEntity.deltaMovement = Vec3.ZERO
            } else if (rand.nextInt(reducedTickDelay(25)) == 0) {
                val floatBetweenZeroAndTwoPi = rand.nextFloat() * (Math.PI.toFloat() * 2f)
                this.perhosgataEntity.deltaMovement = Vec3(
                    (Mth.cos(floatBetweenZeroAndTwoPi) * 0.16f).toDouble(),
                    (rand.nextFloat() * 0.32f).toDouble(),
                    (Mth.sin(floatBetweenZeroAndTwoPi) * 0.16f).toDouble()
                )
            }
        }
    }
}