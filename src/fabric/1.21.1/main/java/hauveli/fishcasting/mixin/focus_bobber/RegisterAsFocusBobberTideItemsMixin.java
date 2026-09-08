package hauveli.fishcasting.mixin.focus_bobber;
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/mixin/ItemsMixin.java
// holymoly what a funny thank you

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.fabric.cc.adimpl.CCIotaHolder;
import com.li64.tide.registries.TideItems;
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

import static at.petrak.hexcasting.api.HexAPI.modLoc;
import static at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER_LOOKUP;

//import static hauveli.fishcasting.registry.FishcastingItems.AMETHYST_FOCUS_BOBBER_REFERENCE;

@Mixin(TideItems.class)
public abstract class RegisterAsFocusBobberTideItemsMixin {

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=amethyst_bobber",
                            ordinal = 0
                    ),
                    to   = @At(
                            value = "CONSTANT",
                            args = "stringValue=echo_bobber"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/li64/tide/registries/TideItems;register(Ljava/lang/String;Ljava/util/function/Function;)Lnet/minecraft/world/item/Item;"
            ),
            index = 1
    )
    private static Function<Item.Properties, Item> replaceAmethystBobber(Function<Item.Properties, Item> original) {
        return props -> {
            TideyFocusItem item = new TideyFocusItem(props);
            // https://github.com/FallingColors/HexMod/blob/edba45d957b19ba9b053993f09c1bcd3cca487fd/Forge/src/main/java/at/petrak/hexcasting/forge/cap/adimpl/CapItemIotaHolder.java#L33
            IOTA_HOLDER_LOOKUP.registerForItems(
                    (stack, context) -> new CCIotaHolder() {
                        @Override
                        public @Nullable Iota readIota() {
                            return item.readIota(stack);
                        }

                        @Override
                        public boolean writeIota(@Nullable Iota iota, boolean simulate) {
                            if (!item.canWrite(stack, iota)) {
                                return false;
                            }
                            if (!simulate) {
                                item.writeDatum(stack, iota);
                            }
                            return true;
                        }

                        @Override
                        public boolean writeable() {
                            return item.writeable(stack);
                        }

                        @Override
                        public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {

                        }

                        @Override
                        public void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {

                        }
                    },
                    item
            );
            return item;
        };
    }

}