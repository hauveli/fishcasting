package hauveli.fishcasting.features.trader

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext

class BlessedArmorLayer(
    renderLayerParent: RenderLayerParent<BlessedEntity, BlessedModel<BlessedEntity>>
) : RenderLayer<BlessedEntity, BlessedModel<BlessedEntity>>(renderLayerParent) {

    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        entity: BlessedEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        /*
        renderArmor(
            entity,
            EquipmentSlot.BODY,
            poseStack,
            buffer,
            packedLight
        )
         */
    }


    // todo: make it render at more than just the body
    private fun renderArmor(
        entity: BlessedEntity,
        slot: EquipmentSlot,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val stack = entity.getItemBySlot(slot)

        if (stack.isEmpty) {
            return
        }

        poseStack.pushPose()

        this.parentModel.body.translateAndRotate(poseStack)
        this.parentModel.body.xScale = 5f
        this.parentModel.body.yScale = 5f
        this.parentModel.body.zScale = 5f

        // I couldn't figure this out...
        TODO()

        poseStack.popPose()
    }
}