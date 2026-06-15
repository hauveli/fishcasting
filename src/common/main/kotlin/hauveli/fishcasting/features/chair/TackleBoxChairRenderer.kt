package hauveli.fishcasting.features.chair

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation


class TackleBoxChairRenderer(context: EntityRendererProvider.Context) : EntityRenderer<TackleBoxChairEntity>(context) {
    // why couldn't kotlin infer this had to be TackleBoxChairEntity and not Any?
    private val model: TackleBoxChairModel<*> = TackleBoxChairModel<TackleBoxChairEntity>(
        context.bakeLayer(TackleBoxChairModel.LAYER_LOCATION)
    )

    override fun render(
        pEntity: TackleBoxChairEntity?,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, 1.5, 0.0)
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw + 90))
        val vertexConsumer = bufferSource.getBuffer(this.model.renderType(TackleBoxChairModel.LAYER_LOCATION.model))
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)
        /*
        if (!p_entity.isUnderWater()) {
            VertexConsumer vertexconsumer1 = bufferSource.getBuffer(RenderType.waterMask());
            model.waterPatch().render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
        }

         */
        poseStack.popPose()
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(p0: TackleBoxChairEntity?): ResourceLocation {
        return TackleBoxChairModel.LAYER_LOCATION.model
    }
}
