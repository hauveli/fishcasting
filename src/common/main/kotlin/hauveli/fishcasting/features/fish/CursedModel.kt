package hauveli.fishcasting.features.fish

import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.model.AxolotlModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.LerpingModel


class CursedModel<T>(root: ModelPart) : AxolotlModel<T?>(root) where T : CursedEntity?, T : LerpingModel? {
    companion object {
        // So that I can re-remember that this is what the first argument in "model layer location" is meant to be
        private val TEXTURE = id("textures/entity/cursed.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        //@Override
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, 0.0f, -2.0f, 6.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 21.0f, 0.0f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(-2.0f, 1.0f, -2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -2.0f)
            )

            val top_gills =
                head.addOrReplaceChild("top_gills", CubeListBuilder.create(), PartPose.offset(0.0f, 3.0f, -5.0f))

            val left_gills =
                head.addOrReplaceChild("left_gills", CubeListBuilder.create(), PartPose.offset(0.0f, 3.0f, -5.0f))

            val right_gills =
                head.addOrReplaceChild("right_gills", CubeListBuilder.create(), PartPose.offset(0.0f, 3.0f, -5.0f))

            val left_front_leg = body.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create().texOffs(12, 8)
                    .addBox(0.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 2.0f, -1.0f)
            )

            val right_front_leg = body.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create().texOffs(10, 12)
                    .addBox(-2.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 2.0f, -1.0f)
            )

            val left_hind_leg = body.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create().texOffs(0, 2)
                    .addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.5f, 2.5f, 3.0f)
            )

            val right_hind_leg = body.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.5f, 2.5f, 3.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.0f, 3.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}
