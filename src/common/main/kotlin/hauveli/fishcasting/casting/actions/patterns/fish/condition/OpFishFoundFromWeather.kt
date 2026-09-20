package hauveli.fishcasting.casting.actions.patterns.fish.condition

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.conditions.types.WeatherCondition
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.casting.iota.WeatherIota
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromWeather : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        val someIota = args[1]
        if (someIota !is WeatherIota) {
            throw MishapInvalidIota(
                someIota,
                1,
                WeatherIota.translation("testing this")
            )
        }

        env.assertEntityInRange(target)
        val maybeFishData = FishData.get(target)
        if (target is ItemEntity && FishData.get(target.item.item).isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish")
        }
        if (maybeFishData.isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish")
        }
        val definitelyFish = maybeFishData.get()

        val relevantConditions = definitelyFish.conditions()
            .filter {
                it is WeatherCondition
            }
        if (relevantConditions.isEmpty())
            return listOf(NullIota()) // if it has no moonphase, it can be found... should it maybe return null if it doesn't care?

        val weather = WeatherType.valueOf(WeatherIota.getName(someIota.weather))

        val foundInMoonPhase = relevantConditions.all { condition ->
            when (condition) {
                is WeatherCondition -> {
                    condition.weatherTypes.contains(weather)
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInMoonPhase))
    }
}