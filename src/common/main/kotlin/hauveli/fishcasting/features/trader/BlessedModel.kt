package hauveli.fishcasting.features.trader

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth


class BlessedModel<T : BlessedEntity?>(root: ModelPart) : HierarchicalModel<T?>() {

    private val rooot: ModelPart
    private val body: ModelPart
    private val head: ModelPart
    private val simplify_logic: ModelPart
    private val rightEar: ModelPart
    private val leftEar: ModelPart
    private val glasses: ModelPart
    private val tail: ModelPart
    private val right_arm: ModelPart
    private val right_thumb: ModelPart
    private val right_finger_back: ModelPart
    private val right_finger_front: ModelPart
    private val left_arm: ModelPart
    private val left_thumb: ModelPart
    private val left_finger_front: ModelPart
    private val left_finger_back: ModelPart
    private val left_leg: ModelPart
    private val right_leg: ModelPart
    private val hat: ModelPart
    private val simplify_logic2: ModelPart
    private val base: ModelPart
    private val middle: ModelPart
    private val tail2: ModelPart

    override fun renderToBuffer(poseStack: PoseStack, vertexConsumer: VertexConsumer, i: Int, i1: Int, i2: Int) {
        rooot.render(poseStack, vertexConsumer, i, i1, i2)
    }

    override fun root(): ModelPart {
        return this.rooot
    }


    init {
        this.rooot = root.getChild("rooot")
        this.body = this.rooot.getChild("body")
        this.head = this.rooot.getChild("head")
        this.simplify_logic = this.head.getChild("simplify_logic")
        this.rightEar = this.simplify_logic.getChild("rightEar")
        this.leftEar = this.simplify_logic.getChild("leftEar")
        this.glasses = this.simplify_logic.getChild("glasses")
        this.tail = this.rooot.getChild("tail")
        this.right_arm = this.rooot.getChild("right_arm")
        this.right_thumb = this.right_arm.getChild("right_thumb")
        this.right_finger_back = this.right_arm.getChild("right_finger_back")
        this.right_finger_front = this.right_arm.getChild("right_finger_front")
        this.left_arm = this.rooot.getChild("left_arm")
        this.left_thumb = this.left_arm.getChild("left_thumb")
        this.left_finger_front = this.left_arm.getChild("left_finger_front")
        this.left_finger_back = this.left_arm.getChild("left_finger_back")
        this.left_leg = this.rooot.getChild("left_leg")
        this.right_leg = this.rooot.getChild("right_leg")
        this.hat = this.rooot.getChild("hat")
        this.simplify_logic2 = this.hat.getChild("simplify_logic2")
        this.base = this.simplify_logic2.getChild("base")
        this.middle = this.base.getChild("middle")
        this.tail2 = this.middle.getChild("tail2")
    }

    // thank you kaupenjoe
    private fun applyHeadRotation(headYaw: Float, headPitch: Float) {
        // restrict here?
        var headYaw = headYaw
        var headPitch = headPitch
        headYaw = Mth.clamp(headYaw, -40f, 40f)
        headPitch = Mth.clamp(headPitch, -40f, 40f)

        this.head.yRot = headYaw * PI_OVER_180
        this.head.xRot = headPitch * PI_OVER_180
        this.hat.yRot = this.head.yRot
        this.hat.xRot = this.head.xRot
    }

    // yes really I don't know of a better way to do this. Please provide me with a cleaner solution if you have one, because I don't like this.
    override fun setupAnim(
        t: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        // BlessedAnimations
        //this.animate(new AnimationState(), BlessedAnimations.casting, v);
        this.root().getAllParts().forEach { obj: ModelPart? -> obj!!.resetPose() }
        applyHeadRotation(netHeadYaw, headPitch)

        // this should always be active
        this.animate(t!!.splayLimbs, BlessedAnimations.standIdle, ageInTicks, 0f)
        // these should not
        this.animate(t.sittingAnimationState, BlessedAnimations.sitting, ageInTicks, 1f)
        //this.animateWalk(BlessedAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animateWalk(BlessedAnimations.walk, limbSwing, limbSwingAmount, 3f, 4.0f)
        this.animate(t.castingAnimationState, BlessedAnimations.casting, ageInTicks, 1f)
        this.animate(t.idleAnimationState, BlessedAnimations.breathe, ageInTicks, 0.25f)
        this.animate(t.leftEarWiggleState, BlessedAnimations.ear_twitch_left, ageInTicks, 1f)
        this.animate(t.rightEarWiggleState, BlessedAnimations.ear_twitch_right, ageInTicks, 1f)
        this.animate(t.tailWiggleState, BlessedAnimations.tail_wiggle, ageInTicks, 1f)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        private val TEXTURE = id("textures/entity/blessed_purple.png")
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            TEXTURE,
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val rooot =
                partdefinition.addOrReplaceChild("rooot", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val body = rooot.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 39)
                    .addBox(-2.5f, -2.5f, -1.5f, 5.0f, 5.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(40, 37).addBox(-2.5f, -2.5f, -1.5f, 5.0f, 5.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offset(0.0f, -7.5f, 0.0f)
            )

            val head = rooot.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0f, -10.0f, 0.0f))

            val simplify_logic = head.addOrReplaceChild(
                "simplify_logic",
                CubeListBuilder.create().texOffs(24, 28)
                    .addBox(-3.0f, -3.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(32, 17).addBox(-3.0f, -3.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f)
            )

            val rightEar = simplify_logic.addOrReplaceChild(
                "rightEar",
                CubeListBuilder.create().texOffs(40, 45)
                    .addBox(-0.5f, -8.0f, -1.5f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.5f, -4.0f, -1.5f, 0.3927f, 0.7592f, 0.0f)
            )

            val leftEar = simplify_logic.addOrReplaceChild(
                "leftEar",
                CubeListBuilder.create().texOffs(16, 39)
                    .addBox(-0.5f, -8.0f, -1.5f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.5f, -4.0f, 1.5f, -0.3927f, 0.7592f, 0.0f)
            )

            val glasses = simplify_logic.addOrReplaceChild(
                "glasses",
                CubeListBuilder.create().texOffs(0, 57)
                    .addBox(-3.5f, -3.5f, -4.4f, 7.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f)
            )

            val tail = rooot.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0f, -5.5f, 1.5f))

            val body_r1 = tail.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(32, 26)
                    .addBox(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.7854f)
            )

            val right_arm = rooot.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(8, 47)
                    .addBox(-2.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(48, 45).addBox(-2.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.1f)),
                PartPose.offset(-2.5f, -9.0f, 0.0f)
            )

            val right_thumb = right_arm.addOrReplaceChild(
                "right_thumb",
                CubeListBuilder.create(),
                PartPose.offset(-0.25f, 3.325f, -0.95f)
            )

            val clawthumb_r1 = right_thumb.addOrReplaceChild(
                "clawthumb_r1",
                CubeListBuilder.create().texOffs(26, 55)
                    .addBox(-1.0f, -2.0f, -1.0f, 0.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.0f, 1.675f, 1.2f, 0.3927f, 0.0f, 0.0f)
            )

            val right_finger_back = right_arm.addOrReplaceChild(
                "right_finger_back",
                CubeListBuilder.create(),
                PartPose.offset(-2.05f, 2.955f, 0.5f)
            )

            val clawbackfingy_r1 = right_finger_back.addOrReplaceChild(
                "clawbackfingy_r1",
                CubeListBuilder.create().texOffs(42, 26)
                    .addBox(0.0f, -2.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.3f, 2.045f, 0.0f, 0.0f, 0.0f, -0.3927f)
            )

            val right_finger_front = right_arm.addOrReplaceChild(
                "right_finger_front",
                CubeListBuilder.create(),
                PartPose.offset(-2.05f, 2.955f, -0.5f)
            )

            val clawfrontfingy_r1 = right_finger_front.addOrReplaceChild(
                "clawfrontfingy_r1",
                CubeListBuilder.create().texOffs(40, 26)
                    .addBox(0.0f, -2.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.3f, 2.045f, 0.0f, 0.0f, 0.0f, -0.3927f)
            )

            val left_arm = rooot.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(48, 26)
                    .addBox(0.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(16, 50).addBox(0.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.1f)),
                PartPose.offset(2.5f, -9.0f, 0.0f)
            )

            val left_thumb = left_arm.addOrReplaceChild(
                "left_thumb",
                CubeListBuilder.create(),
                PartPose.offset(0.25f, 3.325f, -0.95f)
            )

            val clawthumb_r2 = left_thumb.addOrReplaceChild(
                "clawthumb_r2",
                CubeListBuilder.create().texOffs(24, 55)
                    .addBox(0.0f, -2.0f, -1.0f, 0.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 1.675f, 1.2f, 0.3927f, 0.0f, 0.0f)
            )

            val left_finger_front = left_arm.addOrReplaceChild(
                "left_finger_front",
                CubeListBuilder.create(),
                PartPose.offset(0.75f, 5.0f, 0.5f)
            )

            val clawfrontfingy_r2 = left_finger_front.addOrReplaceChild(
                "clawfrontfingy_r2",
                CubeListBuilder.create().texOffs(36, 26)
                    .addBox(0.0f, -2.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.3927f)
            )

            val left_finger_back = left_arm.addOrReplaceChild(
                "left_finger_back",
                CubeListBuilder.create(),
                PartPose.offset(0.75f, 5.0f, 0.5f)
            )

            val clawbackfingy_r2 = left_finger_back.addOrReplaceChild(
                "clawbackfingy_r2",
                CubeListBuilder.create().texOffs(38, 26)
                    .addBox(0.0f, -2.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3927f)
            )

            val left_leg = rooot.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 47)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(48, 52).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.1f)),
                PartPose.offset(1.25f, -5.0f, 0.0f)
            )

            val cube_r1 = left_leg.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(4, 54)
                    .addBox(1.0f, 3.0f, -1.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 54).addBox(2.0f, 3.0f, -1.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.5f, 1.0f, -2.0f, 0.3927f, 0.0f, 0.0f)
            )

            val right_leg = rooot.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(32, 46)
                    .addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(32, 53).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.1f)),
                PartPose.offset(-1.25f, -5.0f, 0.0f)
            )

            val cube_r2 = right_leg.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(12, 54)
                    .addBox(1.0f, 3.0f, -1.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(8, 54).addBox(2.0f, 3.0f, -1.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.5f, 1.0f, -2.0f, 0.3927f, 0.0f, 0.0f)
            )

            val hat = rooot.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0f, -13.0f, 0.0f))

            val simplify_logic2 = hat.addOrReplaceChild(
                "simplify_logic2",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -1.0f, -8.0f, 16.0f, 1.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 17).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f)
            )

            val base =
                simplify_logic2.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val cone1_r1 = base.addOrReplaceChild(
                "cone1_r1",
                CubeListBuilder.create().texOffs(0, 28)
                    .addBox(-3.0542f, -4.2905f, -3.0542f, 6.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f, -0.2742f, 0.0381f, 0.2742f)
            )

            val middle = base.addOrReplaceChild("middle", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val cone2_r1 = middle.addOrReplaceChild(
                "cone2_r1",
                CubeListBuilder.create().texOffs(24, 37)
                    .addBox(-1.0825f, -4.9116f, -1.0825f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.02f, -7.0f, 0.02f, -0.5299f, 0.147f, 0.5299f)
            )

            val tail2 = middle.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val cone3_r1 = tail2.addOrReplaceChild(
                "cone3_r1",
                CubeListBuilder.create().texOffs(24, 46)
                    .addBox(-0.6826f, -6.8133f, -0.6826f, 2.0f, 7.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.1387f, -9.9972f, 2.1387f, -1.0275f, 0.6165f, 1.0275f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }

        private val PI_OVER_180 = (Math.PI / 180).toFloat()
        private val FOURTY_FIVE_DEGREES_IN_RADIANS = (Math.PI / 4).toFloat()
    }
}