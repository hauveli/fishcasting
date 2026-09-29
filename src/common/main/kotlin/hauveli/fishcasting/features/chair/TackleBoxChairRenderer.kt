package hauveli.fishcasting.features.chair

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.features.chair.secret.TackleBoxChairSecretModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import kotlin.collections.get


class TackleBoxChairRenderer(context: EntityRendererProvider.Context) : EntityRenderer<TackleBoxChairEntity>(context) {
    // why couldn't kotlin infer this had to be TackleBoxChairEntity and not Any?
    private val model: TackleBoxChairModel<*> = TackleBoxChairModel<TackleBoxChairEntity>(
        context.bakeLayer(TackleBoxChairModel.LAYER_LOCATION)
    )

    private val secretModel: TackleBoxChairSecretModel<*> = TackleBoxChairSecretModel<TackleBoxChairEntity>(
        context.bakeLayer(TackleBoxChairSecretModel.LAYER_LOCATION)
    )

    override fun render(
        pEntity: TackleBoxChairEntity,
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
        doTheRenderThing(poseStack, packedLight, bufferSource, pEntity)
        poseStack.popPose()
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(tackleBoxChairEntity: TackleBoxChairEntity): ResourceLocation {
        return LOCATION_BY_VARIANT[tackleBoxChairEntity.variant]!!
    }

    fun doTheRenderThing(poseStack: PoseStack, packedLight: Int,
        bufferSource: MultiBufferSource, tackleBoxChairEntity: TackleBoxChairEntity) {
        val texLocation = getTextureLocation(tackleBoxChairEntity)
        val modelToUse = if (tackleBoxChairEntity.variant == TackleBoxChairVariant.SECRET)
            this.secretModel
        else
            this.model
        val vertexConsumer = bufferSource.getBuffer(modelToUse.renderType(texLocation))
        modelToUse.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)
    }

    companion object {
        private val LOCATION_BY_VARIANT: Map<TackleBoxChairVariant, ResourceLocation> =
            mapOf(
                TackleBoxChairVariant.FACTORY to id("textures/entity/tacklebox_chair/tacklebox_chair.png"),
                TackleBoxChairVariant.FLOATY to id("textures/entity/tacklebox_chair/tacklebox_chair_floaty.png"),
                TackleBoxChairVariant.SECRET to id("textures/entity/tacklebox_chair/tacklebox_chair_secret.png")
            )
    }
}
