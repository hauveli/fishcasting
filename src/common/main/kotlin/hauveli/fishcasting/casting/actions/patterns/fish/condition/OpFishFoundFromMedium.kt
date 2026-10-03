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
import com.li64.tide.data.fishing.conditions.types.FishingMediumCondition
import com.li64.tide.data.fishing.mediums.FishingMedium
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.casting.iota.getMedium
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromMedium : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        val someMedium = args.getMedium(1, argc)

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
                it is FishingMediumCondition
            }
        if (relevantConditions.isEmpty())
            return listOf(NullIota())

        val medium = someMedium.mediumIdFromOrdinal()

        val foundInMedium = relevantConditions.all { condition ->
            when (condition) {
                is FishingMediumCondition -> {
                    condition == medium
                }

                else -> true // ugh
            }
        }

        return listOf(BooleanIota(foundInMedium))
    }
}