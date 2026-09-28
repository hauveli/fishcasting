package hauveli.fishcasting.features.fish.edified

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob


class EdifiedFishRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<Mob, EdifiedFishModel>(
    context,
    EdifiedFishModel(context),
    0.3f // I forget what this is todo: remember it/check tide
) {
    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return EdifiedFishModel.LAYER_LOCATION.model
    }
}
