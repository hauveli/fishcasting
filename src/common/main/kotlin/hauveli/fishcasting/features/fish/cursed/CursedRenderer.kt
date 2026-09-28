package hauveli.fishcasting.features.fish.cursed

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
    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return CursedModel.LAYER_LOCATION.model
    }
}
