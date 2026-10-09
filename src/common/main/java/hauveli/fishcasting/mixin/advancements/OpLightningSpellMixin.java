package hauveli.fishcasting.mixin.advancements;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpLightning;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.Fishcasting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Deprecated
@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpLightning$Spell")
public class OpLightningSpellMixin {
    @Inject(
            method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V",
            at = @At("TAIL")
    )
    private void hexcasting$captureLightning(
            CastingEnvironment env, CallbackInfo ci,
            @Local LightningBolt lightning
    ) {
        // this mixin is now deprecated, but I am leaving it in until it is merged into the dev version.
        // shouldn't matter even if they both run, because of setCuase not calling a bunch of bullshit (I think)
        if (env.getCastingEntity() instanceof ServerPlayer serverPlayer) {
            lightning.setCause(serverPlayer);
        }
    }
}