package hauveli.fishcasting.casting.actions.patterns.fish

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import com.li64.tide.Tide
import com.li64.tide.config.TideServerConfig
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.conditions.FishingCondition
import com.li64.tide.data.fishing.conditions.types.BiomeWhitelistCondition
import com.li64.tide.data.fishing.conditions.types.FishingMediumCondition
import com.li64.tide.data.player.FishStats
import com.li64.tide.data.player.TidePlayerData
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.iota.BiomeIota
import hauveli.fishcasting.casting.iota.MediumIota
import hauveli.fishcasting.mixin.environment_spells.BiomeWhitelistConditionAccessor
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpGetFishMedium : ConstMediaAction {
    override val argc: Int = 2
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, OpFishFoundFromBiome.argc)
        val someIota = args[1]
        if (someIota !is MediumIota) {
            throw MishapInvalidIota(
                someIota,
                1,
                MediumIota.translation("testing this")
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

        val foundInBiome = definitelyFish.conditions().stream()
            .filter { it is FishingMediumCondition }
            .map { it as FishingMediumCondition }
            .anyMatch {
                Fishcasting.LOGGER.info("thing: {}, {}", it.mediumId, someIota.SHORTNAME)
                it.mediumId == someIota.SHORTNAME
            }

        return listOf(BooleanIota(foundInBiome))
    }
}