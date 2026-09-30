package hauveli.fishcasting.features.chair.secret

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.features.chair.TackleBoxChairEntity
import hauveli.fishcasting.features.chair.TackleBoxChairModel
import hauveli.fishcasting.features.chair.TackleBoxChairVariant
import hauveli.fishcasting.features.chair.secret.TackleBoxChairSecretModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import kotlin.collections.get


class TackleBoxChairSecretRenderer(context: EntityRendererProvider.Context) : EntityRenderer<TackleBoxChairSecretEntity>(context) {
    // why couldn't kotlin infer this had to be TackleBoxChairEntity and not Any?
    private val model: TackleBoxChairModel<*> = TackleBoxChairModel<TackleBoxChairSecretEntity>(
        context.bakeLayer(TackleBoxChairSecretModel.LAYER_LOCATION)
    )

    private val secretModel: TackleBoxChairSecretModel<*> = TackleBoxChairSecretModel<TackleBoxChairSecretEntity>(
        context.bakeLayer(TackleBoxChairSecretModel.LAYER_LOCATION)
    )

    override fun render(
        pEntity: TackleBoxChairSecretEntity,
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

    override fun getTextureLocation(tackleBoxChairEntity: TackleBoxChairSecretEntity): ResourceLocation {
        return LOCATION_BY_VARIANT[TackleBoxChairVariant.SECRET]!!
    }

    fun doTheRenderThing(poseStack: PoseStack, packedLight: Int,
        bufferSource: MultiBufferSource, tackleBoxChairEntity: TackleBoxChairSecretEntity
    ) {
        val texLocation = getTextureLocation(tackleBoxChairEntity)
        val modelToUse = this.secretModel
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
