package hauveli.fishcasting.mixin.casting_rod;


import com.li64.tide.client.gui.overlays.CastBarOverlay;
import com.llamalad7.mixinextras.sugar.Local;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.features.paraphernalia.HexyRodItem;
import hauveli.fishcasting.config.FishcastingConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CastBarOverlay.class)
public class WindUpCastBarOverlayMixin {
    @Unique
    private static ResourceLocation BAR_HEXY_TEX = Fishcasting.id("textures/gui/fishing/cast_bar_hexy.png");

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V",
                    shift = At.Shift.BEFORE // scary
            )
    )
    private static void beforeDisableBlend(
            GuiGraphics graphics,
            float deltaTicks,
            CallbackInfo ci,
            @Local(name = "x") int x,
            @Local(name = "y") int y,
            @Local(name = "texWidth") int texWidth,
            @Local(name = "texHeight") int texHeight
    ) {
        if (!FishcastingConfigs.INSTANCE.getCOMMON_CONFIG().castingIsMomentary())
            return;
        Player player = Minecraft.getInstance().player;
        if (!(player.getItemInHand(
                player.getUsedItemHand()).getItem() instanceof HexyRodItem hexyRodItem)) {
            // all kinds of messed up otherwise, I think this fixes all the bugs with it...
            HexyRodItem.Companion.setHexyDischargePercent(0f);
            // Letting go of the hook and switching around here causes a visual bug...
            return;
        }
        if (player.isUsingItem()
                || HexyRodItem.Companion.getHexyDischargePercent() > 0f) { // this gt0 check could be a casting grid active check, I *think*
            int antiFillWidth = (int) Math.ceil((1.0f - HexyRodItem.Companion.getHexyDischargePercent()) * texWidth);
            if (antiFillWidth > 0) {
                graphics.blit(BAR_HEXY_TEX,
                        x,
                        y,
                        0, 0,
                        antiFillWidth, texHeight, texWidth, texHeight);
            }
        }
    }
}