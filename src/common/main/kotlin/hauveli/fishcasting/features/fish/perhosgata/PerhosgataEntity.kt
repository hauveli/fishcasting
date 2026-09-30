package hauveli.fishcasting.features.fish.perhosgata

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexParticles
import com.li64.tide.registries.entities.fish.AbstractTideFish
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3


class PerhosgataEntity(entityType: EntityType<out PerhosgataEntity?>?, level: Level?) : AbstractTideFish(entityType, level) {
    fun particlesForScrying(pos: Vec3) {
        if (!this.level().isClientSide)
            return
        val mc = Minecraft.getInstance()

        if (mc.level != null) {
            mc.level!!.addParticle(
                HexParticles.CONJURE_PARTICLE.get() as ParticleOptions,
                pos.x,
                pos.y,
                pos.z,
                0.0,
                0.0,
                0.0
            )
        }
    }

    override fun travel(movement: Vec3) {
        if (this.isEffectiveAi) {
            this.move(MoverType.SELF, this.deltaMovement)
            this.deltaMovement = this.deltaMovement.scale(0.9)
            this.deltaMovement = this.deltaMovement.add(0.0, -0.0035, 0.0)
        } else super.travel(movement)
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(0, perhosgataRandomMovementGoal(this))
    }

    internal class perhosgataRandomMovementGoal(private val perhosgataEntity: PerhosgataEntity) : Goal() {
        override fun canUse(): Boolean {
            return true
        }

        override fun tick() {
            val i = this.perhosgataEntity.getNoActionTime()
            if (i > 100) {
                this.perhosgataEntity.deltaMovement = Vec3.ZERO
            } else if ((this.perhosgataEntity.getRandom()
                    .nextInt(reducedTickDelay(25)) == 0)
            ) {
                val f = this.perhosgataEntity.getRandom().nextFloat() * (Math.PI.toFloat() * 2f)
                this.perhosgataEntity.deltaMovement = Vec3(
                    (Mth.cos(f) * 0.16f).toDouble(),
                    (this.perhosgataEntity.getRandom().nextFloat() * 0.32f).toDouble(),
                    (Mth.sin(f) * 0.16f).toDouble()
                )
                this.perhosgataEntity.particlesForScrying(this.perhosgataEntity.position())
            }
        }
    }
}