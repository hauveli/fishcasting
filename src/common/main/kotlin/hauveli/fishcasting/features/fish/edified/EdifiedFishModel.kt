package hauveli.fishcasting.features.fish.edified

import com.li64.tide.registries.entities.models.FishModel
import com.li64.tide.registries.entities.renderers.FishRenderer
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider

open class EdifiedFishModel @JvmOverloads constructor(
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
        val LAYER_LOCATION: ModelLayerLocation = createModelLocation("edified_fish")

        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val root = mesh.root

            val front = root.addOrReplaceChild(
                "front",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 3.0f, CubeDeformation(0.1f))
                    .texOffs(8, 0).addBox(-0.5f, -0.5f, -0.75f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(6, 4).addBox(-0.5f, -1.75f, -0.25f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 24.0f, -2.0f)
            )

            front.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 10)
                    .addBox(-0.5f, -0.5f, -0.75f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, -0.5f, -1.25f)
            )

            val rear = root.addOrReplaceChild(
                "rear",
                CubeListBuilder.create().texOffs(6, 8)
                    .addBox(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 23.5f, 0.0f)
            )
            rear.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 1.0f)
            )

            return LayerDefinition.create(mesh, 16, 16)
        }
    }
}