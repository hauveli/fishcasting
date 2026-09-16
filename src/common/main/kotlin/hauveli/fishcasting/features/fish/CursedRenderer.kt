package hauveli.fishcasting.features.fish

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob


class CursedRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<Mob, CursedModel>(
    context,
    CursedModel(context),
    0.3f
) {
    private val model: CursedModel = super.getModel()

    fun render(
        pEntity: CursedEntity?,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    fun getTextureLocation(cursedEntity: CursedEntity?): ResourceLocation {
        return CursedModel.LAYER_LOCATION.model
    }

    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return CursedModel.LAYER_LOCATION.model
    }
}
