package hauveli.fishcasting.features.chair.secret

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import hauveli.fishcasting.Fishcasting
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
import kotlin.math.sqrt


class TackleBoxChairSecretModel<T : Entity>(root: ModelPart) : EntityModel<T>() {

    override fun setupAnim(t: T, v: Float, v1: Float, v2: Float, v3: Float, v4: Float) {
        // todo: 60 degree increments

        /*
        wheel.zRot += horizontalVelocity.toFloat()
        wheel2.zRot += v2
         */
    }

    override fun renderToBuffer(
        @Nonnull poseStack: PoseStack,
        @Nonnull buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bike.render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    private val bike: ModelPart
    private val backhex: ModelPart
    private val wheel2: ModelPart
    private val upperhalf2: ModelPart
    private val lowerhalf2: ModelPart
    private val fronthex: ModelPart
    private val guard: ModelPart
    private val wheel: ModelPart
    private val upperhalf: ModelPart
    private val lowerhalf: ModelPart
    private val fronthexcover: ModelPart
    private val spikelower: ModelPart
    private val spikeupper: ModelPart

    init {

        this.bike = root.getChild("bike");
        this.backhex = this.bike.getChild("backhex");
        this.wheel2 = this.backhex.getChild("wheel2");
        this.upperhalf2 = this.wheel2.getChild("upperhalf2");
        this.lowerhalf2 = this.wheel2.getChild("lowerhalf2");
        this.fronthex = this.bike.getChild("fronthex");
        this.guard = this.fronthex.getChild("guard");
        this.wheel = this.fronthex.getChild("wheel");
        this.upperhalf = this.wheel.getChild("upperhalf");
        this.lowerhalf = this.wheel.getChild("lowerhalf");
        this.fronthexcover = this.bike.getChild("fronthexcover");
        this.spikelower = this.fronthexcover.getChild("spikelower");
        this.spikeupper = this.fronthexcover.getChild("spikeupper");
    }


    companion object {
        // So that I can re-remember that this is what the first argument in "model layer location" is meant to be
        private val TEXTURE = id("textures/entity/tacklebox_chair/tacklebox_chair_secret.png")

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        // TODO: add custom bodylayer for the other variant HERE I think
        fun createBodyLayer(): LayerDefinition {

            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bike = partdefinition.addOrReplaceChild(
                "bike",
                CubeListBuilder.create().texOffs(33, 19)
                    .addBox(-3.0f, -6.0f, -5.0f, 1.0f, 0.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-5.0f, -13.5f, -2.0f, 12.0f, 9.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val back_r1 = bike.addOrReplaceChild(
                "back_r1",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-4.0f, -4.0f, -3.0f, 10.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.5f, -13.5f, 0.0f, 0.0f, 0.0f, -0.829f)
            )

            val exhausts_left_r1 = bike.addOrReplaceChild(
                "exhausts_left_r1",
                CubeListBuilder.create().texOffs(29, 55)
                    .addBox(0.0f, -1.5f, 0.0f, 6.0f, 6.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -9.0f, -2.0f, 0.0f, 0.3927f, 0.0f)
            )

            val back_r2 = bike.addOrReplaceChild(
                "back_r2",
                CubeListBuilder.create().texOffs(29, 55)
                    .addBox(0.0f, -1.5f, 0.0f, 6.0f, 6.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, -9.0f, 2.0f, 0.0f, -0.3927f, 0.0f)
            )

            val frontlight_r1 = bike.addOrReplaceChild(
                "frontlight_r1",
                CubeListBuilder.create().texOffs(48, 42)
                    .addBox(-1.0f, -1.5f, -1.5f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-13.0f, -16.25f, 0.0f, -0.7854f, 0.0f, 0.0f)
            )

            val frontlight_r2 = bike.addOrReplaceChild(
                "frontlight_r2",
                CubeListBuilder.create().texOffs(0, 45)
                    .addBox(-2.5f, -2.5f, -1.5f, 3.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-9.4413f, -16.9619f, 0.0f, 0.7854f, 0.0f, -0.7854f)
            )

            val handles_r1 = bike.addOrReplaceChild(
                "handles_r1",
                CubeListBuilder.create().texOffs(32, 18)
                    .addBox(-0.5f, -0.5f, -5.5f, 1.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-11.5f, -16.0f, 0.0f, 0.0f, 0.0f, -0.7854f)
            )

            val hex_machine_r1 = bike.addOrReplaceChild(
                "hex machine_r1",
                CubeListBuilder.create().texOffs(32, 4)
                    .addBox(0.7299f, -4.0941f, -3.5f, 10.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-13.019f, -12.6543f, 1.0f, 0.0f, 0.0f, 0.1745f)
            )

            val body_r1 = bike.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 25)
                    .addBox(-6.5f, -2.0f, -2.0f, 13.0f, 5.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.8524f, -9.185f, 0.5f, 0.0f, 0.0f, -0.0567f)
            )

            val body_r2 = bike.addOrReplaceChild(
                "body_r2",
                CubeListBuilder.create().texOffs(0, 33)
                    .addBox(-6.5f, -1.0f, -1.0f, 13.0f, 4.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(32, 30).addBox(-6.5f, -1.0f, -4.5f, 13.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.8524f, -8.785f, 2.75f, 0.0f, 0.0f, -0.0567f)
            )

            val slope_to_wheel_r1 = bike.addOrReplaceChild(
                "slope_to_wheel_r1",
                CubeListBuilder.create().texOffs(32, 0)
                    .addBox(-6.0f, -3.0f, 2.5f, 18.0f, 4.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(32, 0).addBox(-6.0f, -3.0f, -0.55f, 18.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-21.799f, -10.0f, -0.975f, 0.0f, 0.0f, -0.5236f)
            )

            val backhex =
                bike.addOrReplaceChild("backhex", CubeListBuilder.create(), PartPose.offset(9.0f, -8.0f, 0.0f))

            val wheel2 =
                backhex.addOrReplaceChild("wheel2", CubeListBuilder.create(), PartPose.offset(-10.8583f, 0.2158f, 0.0f))

            val cover_r1 = wheel2.addOrReplaceChild(
                "cover_r1",
                CubeListBuilder.create().texOffs(0, 53)
                    .addBox(-5.5f, -6.0f, -1.0f, 11.0f, 12.0f, 2.0f, CubeDeformation(0.05f)),
                PartPose.offsetAndRotation(10.3208f, 0.5292f, 0.0f, 0.0f, 0.0f, -1.5708f)
            )

            val upperhalf2 = wheel2.addOrReplaceChild(
                "upperhalf2",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-41.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(48.3208f, -0.2958f, 0.0f)
            )

            val cube_r1 = upperhalf2.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-4.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-36.9625f, 0.5975f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r2 = upperhalf2.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-3.0f, 4.3f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-36.9525f, 0.07f, 0.0f, 0.0f, 0.0f, 2.0944f)
            )

            val lowerhalf2 = wheel2.addOrReplaceChild(
                "lowerhalf2",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-39.0375f, 4.2225f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(46.3583f, -0.2333f, 0.0f)
            )

            val cube_r3 = lowerhalf2.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-4.0f, 4.275f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-35.01f, 0.54f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r4 = lowerhalf2.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-3.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-35.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0944f)
            )

            val fronthex =
                bike.addOrReplaceChild("fronthex", CubeListBuilder.create(), PartPose.offset(-23.5f, -3.5f, 0.0f))

            val guard = fronthex.addOrReplaceChild(
                "guard",
                CubeListBuilder.create().texOffs(47, 50)
                    .addBox(-6.0f, -5.625f, -2.0f, 7.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.0375f, -5.58f, 0.5f)
            )

            val cube_r5 = guard.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(29, 50)
                    .addBox(-4.0f, -5.625f, -2.0f, 7.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.9625f, 0.5975f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r6 = guard.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(29, 50)
                    .addBox(-3.0f, 4.3f, -2.0f, 7.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.9525f, 0.07f, 0.0f, 0.0f, 0.0f, 2.0944f)
            )

            val wheel =
                fronthex.addOrReplaceChild("wheel", CubeListBuilder.create(), PartPose.offset(-2.5f, -4.5f, 0.0f))

            val cover_r2 = wheel.addOrReplaceChild(
                "cover_r2",
                CubeListBuilder.create().texOffs(0, 53)
                    .addBox(-5.5f, -6.0f, -1.0f, 11.0f, 12.0f, 2.0f, CubeDeformation(0.05f)),
                PartPose.offsetAndRotation(-0.5375f, 0.745f, 0.0f, 0.0f, 0.0f, -1.5708f)
            )

            val upperhalf = wheel.addOrReplaceChild(
                "upperhalf",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-6.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.4625f, -0.08f, 0.0f)
            )

            val cube_r7 = upperhalf.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-4.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.9625f, 0.5975f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r8 = upperhalf.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-3.0f, 4.3f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.9525f, 0.07f, 0.0f, 0.0f, 0.0f, 2.0944f)
            )

            val lowerhalf = wheel.addOrReplaceChild(
                "lowerhalf",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-4.0375f, 4.2225f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, -0.0175f, 0.0f)
            )

            val cube_r9 = lowerhalf.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-4.0f, 4.275f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.01f, 0.54f, 0.0f, 0.0f, 0.0f, 1.0472f)
            )

            val cube_r10 = lowerhalf.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(0, 38)
                    .addBox(-3.0f, -5.625f, -1.0f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0944f)
            )

            val fronthexcover =
                bike.addOrReplaceChild("fronthexcover", CubeListBuilder.create(), PartPose.offset(-23.5f, -4.5f, 0.0f))

            val spikelower = fronthexcover.addOrReplaceChild(
                "spikelower",
                CubeListBuilder.create(),
                PartPose.offset(-9.5535f, -6.0096f, 0.0f)
            )

            val spike_r1 = spikelower.addOrReplaceChild(
                "spike_r1",
                CubeListBuilder.create().texOffs(51, 48)
                    .addBox(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 2.0944f)
            )

            val spike_r2 = spikelower.addOrReplaceChild(
                "spike_r2",
                CubeListBuilder.create().texOffs(51, 48)
                    .addBox(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 2.0944f)
            )

            val spikeupper = fronthexcover.addOrReplaceChild(
                "spikeupper",
                CubeListBuilder.create(),
                PartPose.offset(-7.5535f, -9.5096f, 0.0f)
            )
            val spike_r3 = spikeupper.addOrReplaceChild(
                "spike_r3",
                CubeListBuilder.create().texOffs(51, 48)
                    .addBox(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 2.0944f)
            )
            val spike_r4 = spikeupper.addOrReplaceChild(
                "spike_r4",
                CubeListBuilder.create().texOffs(51, 48)
                    .addBox(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 2.0944f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
    
}

