package hauveli.fishcasting.mixin.casting_rod;

import at.petrak.hexcasting.api.mod.HexTags;
import com.li64.tide.data.TideTags;
import com.li64.tide.registries.items.TideFishingRodItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.config.FishcastingConfigs;
import hauveli.fishcasting.registry.FishcastingItems;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(TideFishingRodItem.class)
public class getDescriptionLinesTideFishingRodItemMixin {

    @Inject(
            method = "getDescriptionLines",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void fishcasting$addRodTooltip(
            ItemStack stack, CallbackInfoReturnable<List<Component>> cir
    ) {
        List<Component> components = new ArrayList<>(cir.getReturnValue());

        if ((stack.getItem() instanceof TideFishingRodItem)
                && !stack.isDamageableItem()) {
            components.add(Component.translatable("text.fishcasting.rod_tooltip.shepherds_bonus.1").withStyle(ChatFormatting.GOLD));
        }
        if (stack.is(FishcastingTags.END_FISHING_RODS)) {
            components.add(Component.translatable("text.fishcasting.rod_tooltip.shepherds_bonus.2").withStyle(ChatFormatting.GOLD));
        }
        if (stack.is(FishcastingTags.LUCK_REDUCING_RODS)) {
            components.add(Component.translatable("text.fishcasting.rod_tooltip.unluck_bonus.1").withStyle(ChatFormatting.GOLD));
        }
        if (stack.is(HexTags.Items.STAVES)) {
            if (FishcastingConfigs.INSTANCE.getCOMMON_CONFIG().castingIsMomentary()) {
                components.add(Component.translatable("text.fishcasting.rod_tooltip.casting_bonus.momentary").withStyle(ChatFormatting.LIGHT_PURPLE));
            } else if (FishcastingConfigs.INSTANCE.getCOMMON_CONFIG().castingIsOffhandOnly()) {
                components.add(Component.translatable("text.fishcasting.rod_tooltip.casting_bonus.offhand").withStyle(ChatFormatting.LIGHT_PURPLE));
            } else {
                // fallback just in case?
                components.add(Component.translatable("text.fishcasting.rod_tooltip.casting_bonus").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }

        cir.setReturnValue(List.copyOf(components));
    }


    @ModifyVariable(
            method = "castHook",
            at = @At("STORE"),
            ordinal = 1
    )
    private int fishcasting$addRodBonuses(
            int luck,
            ItemStack rod,
            Player player,
            Level level,
            float charge
    ) {
        if (rod.is(FishcastingTags.LUCK_REDUCING_RODS)) {
            // pretty sure level can't become null between when the method is invoked and here so this is fine
            if (player.level().getMaxLocalRawBrightness(player.blockPosition()) < 1)
                luck -= 2;
        }

        return luck;
    }

}
