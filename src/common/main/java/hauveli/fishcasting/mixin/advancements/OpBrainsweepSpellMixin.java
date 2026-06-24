package hauveli.fishcasting.mixin.advancements;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.features.trader.BlessedEntity;
import hauveli.fishcasting.registry.FishcastingAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep$Spell")
public class OpBrainsweepSpellMixin {

    @Final
    @Shadow
    private Mob sacrifice; // cool shadowing inner class works fine

    @Inject(
            method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V",
            at = @At("TAIL")
    )
    private void hexcasting$captureBrainsweep(
            CastingEnvironment env, CallbackInfo ci
    ) {
        // TODO:? something smarter than this maybe
        // pros of this: it's easy and not horribly inefficient
        // cons: I have to do a mixin for it
        if (sacrifice instanceof BlessedEntity
                && env.getCastingEntity() instanceof ServerPlayer serverPlayer) {
            Fishcasting.tryGrantingAdvancement(serverPlayer, FishcastingAdvancements.BLESSED_BRAINSWEPT);
        }
    }
}