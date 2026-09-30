package hauveli.fishcasting.features.fish.perhosgata

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob


class PerhosgataRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<Mob, PerhosgataModel>(
    context,
    PerhosgataModel(context),
    0.3f // I forget what this is todo: remember it/check tide
) {
    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return PerhosgataModel.LAYER_LOCATION.model
    }
}
