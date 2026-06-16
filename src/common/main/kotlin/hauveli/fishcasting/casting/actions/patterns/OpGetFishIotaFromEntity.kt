package hauveli.fishcasting.casting.actions.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughMedia
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.li64.tide.data.fishing.FishData
import hauveli.fishcasting.hexcasting.iota.FishIota
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import kotlin.collections.ArrayList
import kotlin.collections.MutableList

/*
I'm doing this another time if I feel like I need or want it for anything
to consider:
bucketing fish spell by right "writing" a stored fish iota (attached to focus bobber) to your bucket
unbucketing fish spell by reading a stored fish bucket (with a focus bobber out)
*/
object OpGetFishIotaFromEntity : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...


    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        val serverLevel = caster!!.server!!.getLevel(caster.level().dimension())
        val unknownIota: Iota = args[0]

        // Not an entity
        if (unknownIota !is EntityIota) {
            throw MishapInvalidIota.ofType(unknownIota, argc, "entity")
        }
        val unknownEntity = unknownIota.getEntity(serverLevel)

        // Not a hook
        /*
        if (!(unknownEntity instanceof TideFishingHook)) {
            throw MishapBadEntity.of(unknownEntity, "tide_fishing_hook");
        }

         */

        // TideFishingHook target = (TideFishingHook) unknownEntity;
        val target = unknownEntity
        // Too far
        env.assertEntityInRange(target)

        val fishMaybe = FishData.get(target)
        if (fishMaybe.isEmpty) {
            return listOf(NullIota())
        } else {
            val fishActually = fishMaybe.get().fish().value()
            // caster.sendSystemMessage(Component.nullToEmpty(BuiltInRegistries.ITEM.getKey(fishActually).toString()))
            return listOf(FishIota(BuiltInRegistries.ITEM.getKey(fishActually)))
        }
    }
}