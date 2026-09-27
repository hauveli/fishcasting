package hauveli.fishcasting.mixin;

import com.li64.tide.client.gui.screens.journal.FishProfile;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.fishing.conditions.types.BlockNearbyCondition;
import hauveli.fishcasting.features.fish.edified.BlockNearbyComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Optional;

@Mixin(FishProfile.class)
public class NearbyBlockForFishProfile {

    @Inject(
            method = "buildComponents",
            at = @At("RETURN")
    )
    private static void addNearbyBlockComponent(
            FishData data,
            CallbackInfoReturnable<ArrayList<ProfileComponent>> cir
    ) {
        ArrayList<ProfileComponent> components = cir.getReturnValue();

        Optional<BlockNearbyCondition> blockNearbyCondition = data.conditions().stream()
                .filter(cond -> cond instanceof BlockNearbyCondition)
                .findFirst().map(cond -> (BlockNearbyCondition) cond);
        blockNearbyCondition.ifPresent(condition -> components.add(
                new BlockNearbyComponent(condition.getTag(), condition.getRadius())));
    }
}
