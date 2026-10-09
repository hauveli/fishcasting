package hauveli.fishcasting.features.fish.edified

import com.li64.tide.registries.entities.models.FishModel
import com.li64.tide.registries.entities.renderers.FishRenderer
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider


class EdifiedFishModel @JvmOverloads constructor(
    context: EntityRendererProvider.Context?,
    modelLocation: ModelLayerLocation = LAYER_LOCATION
) : FishModel(context, modelLocation) {
    val renderer: FishRenderer<EdifiedFishModel>

    init {
        this.addSwimAnimation("front/head", 0.6f, -0.15f)
        this.addSwimAnimation("rear", 0.6f, 0.25f)
        this.addSwimAnimation("rear/tail", 0.6f, 0.25f)

        this.renderer = FishRenderer("edified_fish", this, context)
    }

    override fun shadowRadius(): Float {
        return 0.15f
    }

    companion object {
        private val TEXTURE = id("textures/entity/edified_fish.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        //@Override
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val front =
                partdefinition.addOrReplaceChild("front", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val head = front.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-1.0f, -3.0f, -1.75f, 2.0f, 3.0f, 2.0f, CubeDeformation(-0.1f)),
                PartPose.offset(0.0f, 0.0f, -3.0f)
            )

            val middle = partdefinition.addOrReplaceChild(
                "middle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -3.0f, -3.0f, 2.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(10, 0).addBox(0.0f, -5.5f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(8, 15).addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val rear = partdefinition.addOrReplaceChild(
                "rear",
                CubeListBuilder.create().texOffs(0, 6)
                    .addBox(-1.0f, -3.0f, -0.25f, 2.0f, 3.0f, 3.0f, CubeDeformation(-0.1f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val tail = rear.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(10, 7)
                    .addBox(0.0f, -4.0f, 0.0f, 0.0f, 5.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 2.5f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}