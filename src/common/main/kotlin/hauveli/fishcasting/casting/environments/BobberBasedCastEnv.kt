package hauveli.fishcasting.casting.environments

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import java.util.List


class BobberBasedCastEnv(caster: ServerPlayer, castingHand: InteractionHand?, private val bobber: TideFishingHook) :
    PlayerBasedCastEnv(caster, castingHand) {
    var sound: EvalSound = HexEvalSounds.NOTHING
    //    protected set
    private val distSqrToOwner: Long = bobber.position().distanceToSqr(caster.position()).toLong()

    override fun postExecution(result: CastResult) {
        super.postExecution(result)
        var maybePatternIota = result.component1()
        if (maybePatternIota is PatternIota) {
            val packet = MsgNewSpiralPatternsS2C(
                this.caster.getUUID(), listOf(maybePatternIota.pattern), 140
            )
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(this.caster, packet)
            IXplatAbstractions.INSTANCE.sendPacketTracking(this.caster, packet)
        }

        // TODO: how do we know when to actually play this sound?
        this.sound = this.sound.greaterOf(result.sound)
    }

    public override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
        var costLeft = costLeft
        if (this.caster.isCreative()) return 0

        // leaving this just in case I decide to add something

        /*
            //var casterStack = this.caster.getItemInHand(this.castingHand);
            var casterStack = this.bobber.getBobber();
            var casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(casterStack);
            if (casterHexHolder == null)
                return costLeft;
            var canCastFromInv = true; // Media is not stored in the bobbers

            var casterMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(casterStack);
            // The contracts on the AD and on this function are different.
            // ADs return the amount extracted, this wants the amount left
            if (casterMediaHolder != null) {
                long extracted = casterMediaHolder.withdrawMedia((int) costLeft, simulate);
                costLeft -= extracted;
            }
             */
        val canCastFromInv = true
        val bobberPenaltyCost = this.distSqrToOwner
        if (canCastFromInv && costLeft > 0) {
            costLeft = this.extractMediaFromInventory(costLeft + bobberPenaltyCost, this.canOvercast(), simulate)
        }

        return costLeft
    }

    override fun getCastingHand(): InteractionHand {
        return this.castingHand
    }

    override fun getPigment(): FrozenPigment {
        val casterStack = this.caster.getItemInHand(this.castingHand)
        val casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(casterStack)
            ?: return IXplatAbstractions.INSTANCE.getPigment(this.caster)
        val hexHolderPigment = casterHexHolder.getPigment()
        if (hexHolderPigment != null) return hexHolderPigment
        return IXplatAbstractions.INSTANCE.getPigment(this.caster)
    }
}
