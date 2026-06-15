package hauveli.fishcasting.features.fish

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation


class CursedRenderer(context: EntityRendererProvider.Context) : MobRenderer<CursedEntity?, CursedModel<CursedEntity?>>(
    context,
    CursedModel(
        context.bakeLayer(CursedModel.LAYER_LOCATION)
    ),
    0.3f
) {
    private val model: CursedModel<*> = super.getModel()

    override fun render(
        pEntity: CursedEntity?,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(cursedEntity: CursedEntity?): ResourceLocation {
        return CursedModel.Companion.LAYER_LOCATION.model
    }
}
