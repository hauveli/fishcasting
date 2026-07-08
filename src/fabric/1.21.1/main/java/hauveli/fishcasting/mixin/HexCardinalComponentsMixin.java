package hauveli.fishcasting.mixin;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.registry.FishcastingItems;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.fabric.cc.HexCardinalComponents")
public class HexCardinalComponentsMixin {

    // Yes, I could do this via my own entrypoint using cardinal components. or I could use a mixin.
    @Inject(
            method = "registerEntityComponentFactories",
            at = @At("TAIL")
    )
    private void registerTideFishingHook(EntityComponentFactoryRegistry registry, CallbackInfo ci) {
        registry.registerFor(
                TideFishingHook.class,
                HexCardinalComponents.IOTA_HOLDER,
                hook -> new CCEntityIotaHolder.Wrapper(
                        new FishcastingItems.ToTideFishingHookEntity(hook)
                )
        );
    }
}