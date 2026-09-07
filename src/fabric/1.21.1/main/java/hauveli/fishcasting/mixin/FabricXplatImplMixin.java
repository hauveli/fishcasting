package hauveli.fishcasting.mixin;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem;
import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.world.item.ItemStack;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.item.ItemComponentMigrationRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "at.petrak.hexcasting.fabric.xplat.FabricXplatImpl")
public class FabricXplatImplMixin {

    // what in the bytecode hell
    @Inject(
            method = "findDataHolder(Lnet/minecraft/world/item/ItemStack;)Lat/petrak/hexcasting/api/addldata/ADIotaHolder;",
            at = @At("TAIL")
    )
    private void registerTideFishingHook(ItemStack stack, CallbackInfoReturnable<ADIotaHolder> cir) {
        Fishcasting.LOGGER.info(stack.getDescriptionId());
        Fishcasting.LOGGER.info(stack.getItem());
        if (stack.getItem() instanceof TideyFocusItem)
            Fishcasting.LOGGER.info("It should fucking work?");
        var cc = HexCardinalComponents.IOTA_HOLDER.maybeGet(stack);
        Fishcasting.LOGGER.info("value obtained: {}", cc);
        Fishcasting.LOGGER.info("value obtained: {}", cc.orElse(null));
        Fishcasting.LOGGER.info("value obtained: {}", cir.getReturnValue());

    }

}