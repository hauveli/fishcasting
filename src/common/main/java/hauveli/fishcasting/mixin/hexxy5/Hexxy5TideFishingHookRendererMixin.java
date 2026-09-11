package hauveli.fishcasting.mixin.hexxy5;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHookRenderer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import hauveli.fishcasting.config.FishcastingConfigs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TideFishingHookRenderer.class)
public class Hexxy5TideFishingHookRendererMixin {

    @WrapMethod(method = "getPlayerHandPos")
    private Vec3 tide$handleSubLevelCamera(
            Player player,
            float anim,
            float partialTick,
            Operation<Vec3> original
    ) {
        if (!FishcastingConfigs.INSTANCE.getCLIENT_CONFIG().getHexxy5KiltSableBugFix().get())
            return original.call(player, anim, partialTick);
        try {
            return original.call(player, anim, partialTick);
        } catch (NullPointerException e) {
            // this megasucks but it works, so whatever.
            // e.getCause() aaaaaaaaaaaaaaaa
            // checking if it at least is related to the bug I'm trying to fix, in the hopes I minimize the potential risks...
            // but because this is a client-sided fix, I don't think it should be a big issue?
            // I could also just return the fallback right away...
            if (e.getMessage() != null
                    && e.getMessage().contains("this.renderCamera")) {

                return tide$fallbackHandPos(player, partialTick);
            }
            // just in case it's NOT sable doing it, crash anyway so i can debug more easily...
            throw e;
        }
    }

    private Vec3 tide$fallbackHandPos(
            Player player,
            float partialTick
    ) {
        // https://github.com/Lightning-64/Tide-2/blob/876b95f31328f4e698d5150f7d840ab033d1b06d/src/main/java/com/li64/tide/registries/entities/misc/fishing/TideFishingHookRenderer.java#L117
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof FishingRodItem)) i = -i;

        // https://github.com/Lightning-64/Tide-2/blob/876b95f31328f4e698d5150f7d840ab033d1b06d/src/main/java/com/li64/tide/registries/entities/misc/fishing/TideFishingHookRenderer.java#L138
        float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
        double d0 = Mth.sin(f);
        double d1 = Mth.cos(f);
        float f1 = player.getScale();
        double d2 = (double)i * 0.35 * (double)f1;
        double d3 = 0.8 * (double)f1;
        float f2 = player.isCrouching() ? -0.1875F : 0.0F;
        return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 0.45 * (double)f1, -d0 * d2 + d1 * d3);
    }
}