package hauveli.fishcasting.features.fish.perhosgata

import at.petrak.hexcasting.api.item.PigmentItem
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.client.render.ScryingLensOverlays
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import com.mojang.blaze3d.vertex.PoseStack
import hauveli.fishcasting.Fishcasting
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3
import java.util.UUID


class PerhosgataRenderer(
    context: EntityRendererProvider.Context
) : MobRenderer<Mob, PerhosgataModel>(
    context,
    PerhosgataModel(context),
    0.3f // I forget what this is todo: remember it/check tide
) {
    override fun shouldRender(
        entity: Mob,
        frustum: Frustum,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val maxDistance = (3 * 3 * 16 * 16) // 3 chunks of blocks

        if (entity.distanceToSqr(camX, camY, camZ) > maxDistance) {
            return false
        }

        return frustum.isVisible(entity.boundingBoxForCulling)
    }
    override fun shouldShowName(p0: Mob): Boolean {
        return super.shouldShowName(p0)
    }

    fun localPlayerHasScryingLens(): Boolean {
        val player = Minecraft.getInstance().player ?: return false
        return !(player.getAttributeValue(HexAttributes.SCRY_SIGHT) <= 0.0
                || player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0)
    }

    val frozenPigment = FrozenPigment(
        HexItems.DYE_PIGMENTS[DyeColor.ORANGE]!!.get().defaultInstance,
        UUID.fromString("00000000-0000-0000-0000-000000000000")
    )
    val orangePigment = frozenPigment.colorProvider.getColor(0f, Vec3.ZERO)

    override fun render(p0: Mob, p1: Float, p2: Float, poseStack: PoseStack, p4: MultiBufferSource, p5: Int) {
        // super.render(p0, p1, p2, p3, p4, p5)
        val level = p0.level() ?: return // just in case it can be null...
        val offsetFromUUID = p0.uuid.leastSignificantBits
        val gameTime = level.gameTime + offsetFromUUID
        val p = (p0 as PerhosgataEntity)
        if (gameTime % 17L != 0L // todo: make the 17 here be variable in some clever way, maybe depending on media?
            || p.renderingOnThisTickIsDone == gameTime)
            return
        if (!localPlayerHasScryingLens())
            return
        p.renderingOnThisTickIsDone = gameTime

        val positionToSpawnParticleAt = p0.position()
        var x = positionToSpawnParticleAt.x
        var y = positionToSpawnParticleAt.y
        var z = positionToSpawnParticleAt.z
        if (positionToSpawnParticleAt.equals(Vec3.ZERO)) {
            // fuuuuck I can't just do .position() if the entity is in a FishDisplay
            val pose = poseStack.last().pose()
            val cameraPos = Minecraft.getInstance().gameRenderer.mainCamera.position

            x = cameraPos.x + pose.m30().toDouble()
            y = cameraPos.y + pose.m31().toDouble()
            z = cameraPos.z + pose.m32().toDouble()
        }


        level.addParticle(
            ConjureParticleOptions(orangePigment),
            true,
            x,y,z,
            0.0, 0.0, 0.0
        )
    }

    override fun getTextureLocation(p0: Mob): ResourceLocation {
        return PerhosgataModel.LAYER_LOCATION.model
    }
}
