package hauveli.fishcasting.casting.actions.patterns.fish

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.Tide
import com.li64.tide.config.TideServerConfig
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.player.FishStats
import com.li64.tide.data.player.TidePlayerData
import com.li64.tide.data.player.TidePlayerData.FishPlayerData
import hauveli.fishcasting.Fishcasting
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity


/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpGetFishRarity : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = 0 // MediaConstants.DUST_UNIT // free is ok I think


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        // no circle without a player because it makes no sense
        if (env !is PlayerBasedCastEnv) {
            throw MishapBadCaster()
        }
        // is there a cleaner way to combine this and the above...
        val caster = env.castingEntity
        if (caster !is ServerPlayer) {
            throw MishapBadCaster()
        }

        val target = args.getEntity(env.world, 0, argc)
        env.assertEntityInRange(target)
        var maybeFishData = FishData.get(target)
        if (target is ItemEntity) {
            maybeFishData = FishData.get(target.item.item)
        }
        if (maybeFishData.isEmpty) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish")
        }

        val definitelyFish = maybeFishData.get()
        if (!definitelyFish.hasJournalEntry()) {
            throw MishapBadEntity.of(target, "fishcasting.not_a_fish.no_journal") // "No record found" or something...
        }

        return listOf(DoubleIota(definitelyFish.profile().rarity().numStars.toDouble()))
    }
}