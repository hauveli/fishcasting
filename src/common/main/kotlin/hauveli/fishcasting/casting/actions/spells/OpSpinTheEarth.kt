package hauveli.fishcasting.casting.actions.spells

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingConfigs
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

object OpSpinTheEarth : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        // Mishap immediately if Nature disallows it
        // need: spell to get Nature's wrath level (timer based on how close to a multiple of pi the last tick was, to avoid annoying flickering, but allow smooth panning)

        // Else continue as normal

        val ticksToSkip = args.getInt(0, argc)
        val caster = env.castingEntity
        val eye = caster?.eyePosition ?: Vec3.ZERO

        val dayTime = env.world.dayTime

        // if the sun is up, do this
        var angle = env.world.getSunAngle((dayTime % 20) / 20f ).toDouble()
        // Fishcasting.LOGGER.info("Angle: {}", angle)
        // else: do it to the moon
        if (dayTime % 24000 > 12750) { // about 750 ish of 24000, so 23250, is when they are at the same level.
            angle -= Mth.PI
            // Fishcasting.LOGGER.info("Angle after change: {}", angle)
        }

        val sunDirection = Vec3(
            -sin(angle),
            cos(angle),
            0.0
        ).normalize()

        val distanceFromCaster = caster?.eyeHeight?.times(5)?.toDouble() ?: 8.0

        val finalTarget = eye.add(sunDirection.scale(distanceFromCaster))
        // just checking and showing that they are different
        /*
        if (caster is ServerPlayer) {
            caster.sendSystemMessage(Component.nullToEmpty("Current gameTime: " + env.world.gameTime))
            caster.sendSystemMessage(Component.nullToEmpty("Current dayTime: " + env.world.dayTime))
        }
         */

        // todo: think of cooler particles, should it block the view of the sun for a moment? I think so...

        // should it cost more? this is (20 * x / 10) => 2x per second, (in this case 2*1=2, so 2 per second)
        return SpellAction.Result(
            Spell(ticksToSkip),
            (MediaConstants.CRYSTAL_UNIT + ticksToSkip * MediaConstants.DUST_UNIT / 10L), // 1 charged plus 2 dust per second skipped
            listOf(ParticleSpray.cloud(finalTarget, 2.0))
        )
    }

    private data class Spell(val ticksToSkip: Int) : RenderedSpell {
        // IMPORTANT: do not throw mishaps in this method! mishaps should ONLY be thrown in SpellAction.execute
        override fun cast(env: CastingEnvironment) {
            env.world.dayTime += ticksToSkip
        }
    }
}
