package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.SpellCircleContext
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedSpiralPatternCastEnv
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

object OpGetDimension : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    // todo: re-review in dev-54 or whatever version makes player references behave nicer
    // todo: HELP I CANT FIGURE OUT HOW TO MAKE IT GRAB THE BOUND PLAYER (and mishap if the player is not in the same level (is this reasonable?))
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        // return listOf(DimensionIota(env.world.dimension()))
        var dimension: ResourceKey<Level> = env.world.dimension()
        if (env is CircleCastEnv) {
            val caster = env.castingEntity
            // env.impetus.level.dimension()
            // why the fuck is caster null in this scope? it is bound to me...
            if (caster != null) {
                env.assertEntityInRange(caster)
                dimension = caster.level().dimension()
            }
        }
        return listOf(RealEnvironmentIota(EnvironmentValue.Dimension(dimension)))
    }
}