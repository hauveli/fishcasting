package hauveli.fishcasting.mixin;

import com.li64.tide.client.gui.screens.journal.FishProfile;
import com.li64.tide.client.gui.screens.journal.ProfileComponent;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.fishing.conditions.types.AboveCondition;
import com.li64.tide.data.fishing.conditions.types.BlockNearbyCondition;
import com.li64.tide.data.fishing.conditions.types.EnchantmentsCondition;
import hauveli.fishcasting.features.fish.profile_components.BlockNearbyComponent;
import hauveli.fishcasting.features.fish.profile_components.AboveComponent;
import hauveli.fishcasting.features.fish.profile_components.HasEnchantmentsComponent;
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

        Optional<EnchantmentsCondition> enchantmentsCondition = data.conditions().stream()
                .filter(cond -> cond instanceof EnchantmentsCondition)
                .findFirst().map(cond -> (EnchantmentsCondition) cond);
        enchantmentsCondition.ifPresent(condition -> components.add(
                new HasEnchantmentsComponent(condition.getEnchantments())));

        Optional<AboveCondition> aboveCondition = data.conditions().stream()
                .filter(cond -> cond instanceof AboveCondition)
                .findFirst().map(cond -> (AboveCondition) cond);
        aboveCondition.ifPresent(condition -> {
            if (condition.getMinY() >= AboveComponent.SEA_LEVEL) {
                components.add(new AboveComponent(condition.getMinY()));
            }
        });
    }
}
