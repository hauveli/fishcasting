package hauveli.fishcasting.features.chair

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.Entity
import javax.annotation.Nonnull
import javax.annotation.Nullable


class TackleBoxChairModel<T : Entity?>(root: ModelPart) : EntityModel<T?>() {
    // why can't I just import a .json for this........
    private val bone: ModelPart = root.getChild("bone")

    override fun renderToBuffer(
        @Nonnull poseStack: PoseStack,
        @Nonnull buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    override fun setupAnim(t: T?, v: Float, v1: Float, v2: Float, v3: Float, v4: Float) {
    }

    companion object {
        // So that I can re-remember that this is what the first argument in "model layer location" is meant to be
        private val TEXTURE = id("textures/entity/tacklebox_chair.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-14.0f, -8.0f, 3.0f, 12.0f, 6.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(0, 34).addBox(-12.0f, -4.0f, 1.0f, 8.0f, 2.0f, 14.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-12.0f, -6.0f, 0.0f, 8.0f, 2.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(40, 43).addBox(-14.0f, -2.0f, 4.0f, 12.0f, 2.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(40, 43).addBox(-14.0f, -2.0f, 12.0f, 12.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(8.0f, 24.0f, -8.0f)
            )

            val backsupport_r1 = bone.addOrReplaceChild(
                "backsupport_r1",
                CubeListBuilder.create().texOffs(44, 27)
                    .addBox(-7.0f, -7.725f, 15.0f, 0.01f, 7.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(44, 45).addBox(-7.0f, -9.725f, 14.0f, 1.0f, 11.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(44, 45).addBox(-7.0f, -9.725f, 23.0f, 1.0f, 11.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.0f, -6.0f, -11.0f, 0.0f, 0.0f, 0.3927f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}
