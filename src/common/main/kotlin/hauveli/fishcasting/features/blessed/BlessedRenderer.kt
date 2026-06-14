package hauveli.fishcasting.features.blessed

import com.mojang.blaze3d.vertex.PoseStack
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

// todo: cursed spawn egg should have the purple void indicator that other tide fish have for parity
// todo: if a boss mob is detected nearby, instantly teleport out (deal damage to the mob so it triggers that way? would be funny but maybe not...)
// todo: make them have trades based on color
class BlessedRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<BlessedEntity, BlessedModel<BlessedEntity>>(
        context,
        BlessedModel(context.bakeLayer(BlessedModel.LAYER_LOCATION)),
        1f
    ) {
    override fun getTextureLocation(blessedEntity: BlessedEntity): ResourceLocation {
        return LOCATION_BY_VARIANT[blessedEntity.variant]!!
    }


    override fun getRenderType(
        livingEntity: BlessedEntity,
        bodyVisible: Boolean,
        translucent: Boolean,
        glowing: Boolean
    ): RenderType? {
        return super.getRenderType(livingEntity, bodyVisible, true, glowing) // glasses are a bit too thick otherwise.
    }

    override fun render(
        blessedEntity: BlessedEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(blessedEntity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight)
    }

    companion object {
        private val LOCATION_BY_VARIANT: Map<BlessedVariant, ResourceLocation> =
            mapOf(
                BlessedVariant.RED to id("textures/entity/blessed/blessed_red.png"),
                BlessedVariant.GREEN to id("textures/entity/blessed/blessed_green.png"),
                BlessedVariant.BLUE to id("textures/entity/blessed/blessed_blue.png"),
                BlessedVariant.PURPLE to id("textures/entity/blessed/blessed_purple.png")
            )
    }
}