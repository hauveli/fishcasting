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
        // TODO:? I really don't want to potentially screw over another mods detection methods for some obscure mechanic
        // But also, this is so simple.... And it would only affect lightning bolts summoned via hex....
        // please open a PR if you find any mod that this may conflict with so that I can get an idea of what I need to do
        // to make things work, my best current idea is some cursed kind of "make a temporary array for a player, store the lightning there,
        // when cursed is hit check if the source is the lightning bolt/if the lightning bnolt is close enough to it to trigger transformation...
        // and then removing the lightning bolt upon its discard/kill, somehow.
        // other suggestions welcomed.
        if (env.getCastingEntity() instanceof ServerPlayer serverPlayer) {
            lightning.setCause(serverPlayer);
        }
    }
}