package hauveli.fishcasting.casting.actions.patterns.fish

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import com.li64.tide.data.TideTags
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.conditions.types.BiomeWhitelistCondition
import com.li64.tide.data.fishing.conditions.types.FreshwaterCondition
import com.li64.tide.data.fishing.conditions.types.SaltwaterCondition
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromBiome
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.mixin.environment_spells.BiomeWhitelistConditionAccessor
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpFishFoundFromCondition : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        val someIota = args[1]
        if (someIota !is EnvironmentIota) {
            throw MishapInvalidIota(
                someIota,
                1,
                EnvironmentIota.translation("testing this")
            )
        }

        if (someIota is BiomeIota) {
            return OpFishFoundFromBiome.execute(args, env)
        }

        return listOf(NullIota())
    }
}