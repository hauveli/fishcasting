package hauveli.fishcasting.mixin.focus_bobber;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CastingEnvironment.class)
public abstract class BobberAmbitCastingEnvironmentMixin {

    @Shadow
    public abstract @Nullable LivingEntity getCastingEntity();
    /*

        // A little bit ugly when nested but whatever
        // should ONLY apply to the bobber entity, and entities hooked to the caster's bobber
        // or if true name would allow them access to a player (who is NOT out of the world
        // (unless that's possible in base hex? hmm...)

        NO TRUE NAME:
            TARGET: PLAYER
            POSITION: IN WORLD
            SPELL: GET BOBBER -> OK

            POSITION: OUTSIDE WORLD
            SPELL: GET BOBBER -> MISHAP

       TRUE NAME:
            SAME AS IF I WERE THEM

       SELF:
            TARGET: SELF || OWN BOBBER || OWN CATCH
            SPELL -> OK

    */
    @Inject(method = "assertEntityInRange", at = @At("HEAD"), cancellable = true)
    private void fishcasting$vectorInRange(Entity e, CallbackInfo ci) {
        if (targetIsWithinAmbit(e, (CastingEnvironment) (Object) this)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean targetIsWithinAmbit(Entity e, CastingEnvironment env) {
        if (e == env.getCastingEntity()) {
            return false; // strange, sure, but we don't want to cancel...
        } else if (e instanceof TideFishingHook fishingHook) {
            // check if true name here? on the owner
            Player hookOwner = fishingHook.getPlayerOwner();
            boolean hasTrueName = false;
            if (hookOwner == env.getCastingEntity()) {
                return true;
            } else if (hasTrueName) {
                return true;
            }
        }
        return false;
    }

    // todo: this is to hacky imo, figure out a better approach somehow...
    // should BobberBasedCastEnv extend CastingEnvironment instead? I think that would allow me to get rid of this mixin...
    // ideally, I never want to mixin into anything hexcasting....
}
