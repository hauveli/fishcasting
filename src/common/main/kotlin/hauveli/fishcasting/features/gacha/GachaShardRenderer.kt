package hauveli.fishcasting.features.gacha

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext

class GachaShardRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<GachaShardEntity>(context) {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: GachaShardEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()


        // flat on ground aaaaaaa but it doesn't line up with stairs... hmm...
        if (entity.hasLanded) {
            poseStack.mulPose(
                Axis.YP.rotationDegrees(entity.landedRotY)
            )
            poseStack.mulPose(
                Axis.ZP.rotationDegrees(entity.landedRotX)
            )
            poseStack.mulPose(
                Axis.XP.rotationDegrees(90.0f)
            )
        } else {
            poseStack.mulPose(
                Axis.YP.rotationDegrees(entity.yRot)
            )
            poseStack.mulPose(
                Axis.ZP.rotationDegrees(entity.xRot)
            )
        }

        // todo: figure out how to draw something similar enough to this without needing to register an item
        itemRenderer.renderStatic(
            FishcastingItems.GLASS_SHARD.value.defaultInstance,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            entity.level(),
            entity.id
        )

        poseStack.popPose()

        super.render(
            entity,
            entityYaw,
            partialTick,
            poseStack,
            buffer,
            packedLight
        )
    }

    override fun getTextureLocation(
        entity: GachaShardEntity
    ): ResourceLocation {
        return id("textures/item/glass_shard.png")
    }
}