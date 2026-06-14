package hauveli.fishcasting.features.blessed;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import hauveli.fishcasting.Fishcasting;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static hauveli.fishcasting.features.blessed.BlessedModel.LAYER_LOCATION;

// todo: cursed spawn egg should have the purple void indicator that other tide fish have for parity
// todo: if a boss mob is detected nearby, instantly teleport out (deal damage to the mob so it triggers that way? would be funny but maybe not...)
// todo: make them have trades based on color
public class BlessedRenderer extends MobRenderer<BlessedEntity, BlessedModel<BlessedEntity>> {
    // hmm...
    private static final Map<BlessedVariant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(BlessedVariant.class), map -> {
        map.put(BlessedVariant.RED, Fishcasting.id("textures/entity/blessed/blessed_red.png"));
        map.put(BlessedVariant.GREEN, Fishcasting.id("textures/entity/blessed/blessed_green.png"));
        map.put(BlessedVariant.BLUE, Fishcasting.id("textures/entity/blessed/blessed_blue.png"));
        map.put(BlessedVariant.PURPLE, Fishcasting.id("textures/entity/blessed/blessed_purple.png"));
    });

    public BlessedRenderer(EntityRendererProvider.Context context) {
        super(context, new BlessedModel<>(context.bakeLayer(LAYER_LOCATION)), 1f);
    }

    public ResourceLocation getTextureLocation(BlessedEntity blessedEntity) {
        return LOCATION_BY_VARIANT.get(blessedEntity.getVariant());
    }


    @Override
    protected @Nullable RenderType getRenderType(BlessedEntity livingEntity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return super.getRenderType(livingEntity, bodyVisible, true, glowing); // glasses are a bit too thick otherwise.
    }

    @Override
    public void render(BlessedEntity blessedEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(blessedEntity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }
}
