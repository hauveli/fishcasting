package hauveli.fishcasting.mixin;

import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.registry.FishcastingItems;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.item.ItemComponentMigrationRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER_LOOKUP;
import static at.petrak.paucal.api.PaucalAPI.modLoc;

@Mixin(targets = "at.petrak.hexcasting.fabric.cc.HexCardinalComponents")
public class HexCardinalComponentsMixin {

    // todo: do it properly later instead of being lazy...?
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


    /*
    @Inject(
            method = "registerItemComponentMigrations",
            at = @At("TAIL")
    )
    private void registerTideAmethystBobber(ItemComponentMigrationRegistry registry, CallbackInfo ci) {
        // IOTA_HOLDER_LOOKUP
        // registry.registerMigration(modLoc("iota_holder"), HexDataComponents.IOTA_HOLDER_IOTA.get());
    }

     */
}