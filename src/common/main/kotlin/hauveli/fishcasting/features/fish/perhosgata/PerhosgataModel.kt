package hauveli.fishcasting.features.fish.perhosgata

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


class PerhosgataModel @JvmOverloads constructor(
    context: EntityRendererProvider.Context?,
    modelLocation: ModelLayerLocation = LAYER_LOCATION
) : FishModel(context, modelLocation) {

    override fun shadowRadius(): Float {
        return 0.0f
    }

    companion object {
        private val TEXTURE = id("textures/entity/perhosgata.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        //@Override
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val front = meshdefinition.root
                    .addOrReplaceChild("front", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            return LayerDefinition.create(meshdefinition, 16, 16)
        }
    }
}