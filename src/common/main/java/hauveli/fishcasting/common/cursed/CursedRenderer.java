package hauveli.fishcasting.common.cursed;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static hauveli.fishcasting.common.cursed.CursedModel.LAYER_LOCATION;

public class CursedRenderer extends MobRenderer<CursedEntity, CursedModel<CursedEntity>> {

    private final CursedModel model;

    public CursedRenderer(EntityRendererProvider.Context context) {
        super(context,
                new CursedModel(
                        context.bakeLayer(LAYER_LOCATION)
                ),
                0.3f);
        this.model = super.getModel();
    }

    @Override
    public void render(CursedEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CursedEntity cursedEntity) {
        return LAYER_LOCATION.getModel();
    }
}
