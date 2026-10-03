package hauveli.fishcasting.casting.actions.patterns.fish.condition

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.modifiers.types.TemperatureModifier
import com.li64.tide.util.TideUtils
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.getClimate
import hauveli.fishcasting.casting.iota.getEnvironment
import net.minecraft.world.entity.item.ItemEntity
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.sqrt


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromClimate : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think

    // todo: I kind of would prefer to use the ClimateIota because it abstracts away this nonsense a little...


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        val someEnvironmentValue = args.getClimate(1, argc)

        env.assertEntityInRange(target)
        val maybeFishData = FishData.get(target)
        if (target is ItemEntity && FishData.get(target.item.item).isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish")
        }
        if (maybeFishData.isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish")
        }
        val definitelyFish = maybeFishData.get()

        val relevantModifiers = definitelyFish.modifiers()
            .filter {
                it is TemperatureModifier
            }
        if (relevantModifiers.isEmpty())
            return listOf(NullIota()) // if it has no moonphase, it can be found... should it maybe return null if it doesn't care?

        val foundInClimates = relevantModifiers.all { modifier ->
            when (modifier) {
                is TemperatureModifier -> {
                    abs(modifier.preferred - someEnvironmentValue.value) < modifier.tolerance
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInClimates))
    }
}