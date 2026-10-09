package hauveli.fishcasting.casting.actions.patterns.fish

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.TideTags
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.conditions.types.BiomeWhitelistCondition
import com.li64.tide.data.fishing.conditions.types.FreshwaterCondition
import com.li64.tide.data.fishing.conditions.types.SaltwaterCondition
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromBiome
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromClimate
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromDepth
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromDimension
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromMedium
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromMoonPhase
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromStructure
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromTimeOfDay
import hauveli.fishcasting.casting.actions.patterns.fish.condition.OpFishFoundFromWeather
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.EnvironmentIota
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.casting.iota.getEnvironment
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
    override val mediaCost: Long = MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc) // just so I mishap on the entity being out of ambit before going deeper
        val someEnv = args.getEnvironment(1, argc)

        return when (someEnv) {
            is EnvironmentValue.Biome -> OpFishFoundFromBiome.execute(args, env)
            is EnvironmentValue.Climate -> OpFishFoundFromClimate.execute(args, env)
            is EnvironmentValue.Daytime -> OpFishFoundFromTimeOfDay.execute(args, env)
            is EnvironmentValue.Depth -> OpFishFoundFromDepth.execute(args, env)
            is EnvironmentValue.Dimension -> OpFishFoundFromDimension.execute(args, env)
            is EnvironmentValue.Medium -> OpFishFoundFromMedium.execute(args, env)
            is EnvironmentValue.MoonPhase -> OpFishFoundFromMoonPhase.execute(args, env)
            is EnvironmentValue.Structure -> OpFishFoundFromStructure.execute(args, env)
            is EnvironmentValue.Weather -> OpFishFoundFromWeather.execute(args, env)
        }
    }
}