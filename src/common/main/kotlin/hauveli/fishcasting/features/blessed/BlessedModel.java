package hauveli.fishcasting.features.blessed;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hauveli.fishcasting.Fishcasting;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BlessedModel<T extends BlessedEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private static final ResourceLocation TEXTURE = Fishcasting.id("textures/entity/blessed_purple.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            TEXTURE,
            "main");

    /* humanoidMobModel

    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

     */

    private final ModelPart rooot;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart simplify_logic;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart glasses;
    private final ModelPart tail;
    private final ModelPart right_arm;
    private final ModelPart right_thumb;
    private final ModelPart right_finger_back;
    private final ModelPart right_finger_front;
    private final ModelPart left_arm;
    private final ModelPart left_thumb;
    private final ModelPart left_finger_front;
    private final ModelPart left_finger_back;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart hat;
    private final ModelPart simplify_logic2;
    private final ModelPart base;
    private final ModelPart middle;
    private final ModelPart tail2;

    public BlessedModel(ModelPart root) {
        this.rooot = root.getChild("rooot");
        this.body = this.rooot.getChild("body");
        this.head = this.rooot.getChild("head");
        this.simplify_logic = this.head.getChild("simplify_logic");
        this.rightEar = this.simplify_logic.getChild("rightEar");
        this.leftEar = this.simplify_logic.getChild("leftEar");
        this.glasses = this.simplify_logic.getChild("glasses");
        this.tail = this.rooot.getChild("tail");
        this.right_arm = this.rooot.getChild("right_arm");
        this.right_thumb = this.right_arm.getChild("right_thumb");
        this.right_finger_back = this.right_arm.getChild("right_finger_back");
        this.right_finger_front = this.right_arm.getChild("right_finger_front");
        this.left_arm = this.rooot.getChild("left_arm");
        this.left_thumb = this.left_arm.getChild("left_thumb");
        this.left_finger_front = this.left_arm.getChild("left_finger_front");
        this.left_finger_back = this.left_arm.getChild("left_finger_back");
        this.left_leg = this.rooot.getChild("left_leg");
        this.right_leg = this.rooot.getChild("right_leg");
        this.hat = this.rooot.getChild("hat");
        this.simplify_logic2 = this.hat.getChild("simplify_logic2");
        this.base = this.simplify_logic2.getChild("base");
        this.middle = this.base.getChild("middle");
        this.tail2 = this.middle.getChild("tail2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rooot = partdefinition.addOrReplaceChild("rooot", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = rooot.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 39).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(40, 37).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -7.5F, 0.0F));

        PartDefinition head = rooot.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition simplify_logic = head.addOrReplaceChild("simplify_logic", CubeListBuilder.create().texOffs(24, 28).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 17).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition rightEar = simplify_logic.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(40, 45).addBox(-0.5F, -8.0F, -1.5F, 1.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -4.0F, -1.5F, 0.3927F, 0.7592F, 0.0F));

        PartDefinition leftEar = simplify_logic.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(16, 39).addBox(-0.5F, -8.0F, -1.5F, 1.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -4.0F, 1.5F, -0.3927F, 0.7592F, 0.0F));

        PartDefinition glasses = simplify_logic.addOrReplaceChild("glasses", CubeListBuilder.create().texOffs(0, 57).addBox(-3.5F, -3.5F, -4.4F, 7.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition tail = rooot.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -5.5F, 1.5F));

        PartDefinition body_r1 = tail.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(32, 26).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.7854F));

        PartDefinition right_arm = rooot.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(8, 47).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 45).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(-2.5F, -9.0F, 0.0F));

        PartDefinition right_thumb = right_arm.addOrReplaceChild("right_thumb", CubeListBuilder.create(), PartPose.offset(-0.25F, 3.325F, -0.95F));

        PartDefinition clawthumb_r1 = right_thumb.addOrReplaceChild("clawthumb_r1", CubeListBuilder.create().texOffs(26, 55).addBox(-1.0F, -2.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.675F, 1.2F, 0.3927F, 0.0F, 0.0F));

        PartDefinition right_finger_back = right_arm.addOrReplaceChild("right_finger_back", CubeListBuilder.create(), PartPose.offset(-2.05F, 2.955F, 0.5F));

        PartDefinition clawbackfingy_r1 = right_finger_back.addOrReplaceChild("clawbackfingy_r1", CubeListBuilder.create().texOffs(42, 26).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 2.045F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition right_finger_front = right_arm.addOrReplaceChild("right_finger_front", CubeListBuilder.create(), PartPose.offset(-2.05F, 2.955F, -0.5F));

        PartDefinition clawfrontfingy_r1 = right_finger_front.addOrReplaceChild("clawfrontfingy_r1", CubeListBuilder.create().texOffs(40, 26).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 2.045F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition left_arm = rooot.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 26).addBox(0.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 50).addBox(0.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(2.5F, -9.0F, 0.0F));

        PartDefinition left_thumb = left_arm.addOrReplaceChild("left_thumb", CubeListBuilder.create(), PartPose.offset(0.25F, 3.325F, -0.95F));

        PartDefinition clawthumb_r2 = left_thumb.addOrReplaceChild("clawthumb_r2", CubeListBuilder.create().texOffs(24, 55).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.675F, 1.2F, 0.3927F, 0.0F, 0.0F));

        PartDefinition left_finger_front = left_arm.addOrReplaceChild("left_finger_front", CubeListBuilder.create(), PartPose.offset(0.75F, 5.0F, 0.5F));

        PartDefinition clawfrontfingy_r2 = left_finger_front.addOrReplaceChild("clawfrontfingy_r2", CubeListBuilder.create().texOffs(36, 26).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition left_finger_back = left_arm.addOrReplaceChild("left_finger_back", CubeListBuilder.create(), PartPose.offset(0.75F, 5.0F, 0.5F));

        PartDefinition clawbackfingy_r2 = left_finger_back.addOrReplaceChild("clawbackfingy_r2", CubeListBuilder.create().texOffs(38, 26).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition left_leg = rooot.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 52).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(1.25F, -5.0F, 0.0F));

        PartDefinition cube_r1 = left_leg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 54).addBox(1.0F, 3.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 54).addBox(2.0F, 3.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 1.0F, -2.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition right_leg = rooot.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 46).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 53).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(-1.25F, -5.0F, 0.0F));

        PartDefinition cube_r2 = right_leg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 54).addBox(1.0F, 3.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 54).addBox(2.0F, 3.0F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 1.0F, -2.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition hat = rooot.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition simplify_logic2 = hat.addOrReplaceChild("simplify_logic2", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition base = simplify_logic2.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cone1_r1 = base.addOrReplaceChild("cone1_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-3.0542F, -4.2905F, -3.0542F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.2742F, 0.0381F, 0.2742F));

        PartDefinition middle = base.addOrReplaceChild("middle", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cone2_r1 = middle.addOrReplaceChild("cone2_r1", CubeListBuilder.create().texOffs(24, 37).addBox(-1.0825F, -4.9116F, -1.0825F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.02F, -7.0F, 0.02F, -0.5299F, 0.147F, 0.5299F));

        PartDefinition tail2 = middle.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cone3_r1 = tail2.addOrReplaceChild("cone3_r1", CubeListBuilder.create().texOffs(24, 46).addBox(-0.6826F, -6.8133F, -0.6826F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1387F, -9.9972F, 2.1387F, -1.0275F, 0.6165F, 1.0275F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        rooot.render(poseStack, vertexConsumer, i, i1, i2);
    }

    @Override
    public ModelPart root() {
        return this.rooot;
    }



    private final static float PI_OVER_180 = (float) (Math.PI / 180);
    private final static float FOURTY_FIVE_DEGREES_IN_RADIANS = (float) (Math.PI / 4);

    // thank you kaupenjoe
    private void applyHeadRotation(float headYaw, float headPitch) {
        // restrict here?
        headYaw = Mth.clamp(headYaw, -40f, 40f);
        headPitch = Mth.clamp(headPitch, -40f, 40f);

        this.head.yRot = headYaw * PI_OVER_180;
        this.head.xRot = headPitch * PI_OVER_180;
        this.hat.yRot = this.head.yRot;
        this.hat.xRot = this.head.xRot;
    }

    // yes really I don't know of a better way to do this. Please provide me with a cleaner solution if you have one, because I don't like this.
    @Override
    public void setupAnim(T t, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // BlessedAnimations
        //this.animate(new AnimationState(), BlessedAnimations.casting, v);
        this.root().getAllParts().forEach(ModelPart::resetPose);
        applyHeadRotation(netHeadYaw, headPitch);

        // this should always be active
        this.animate(t.splayLimbs, BlessedAnimations.standIdle, ageInTicks, 0f);
        // these should not
        this.animate(t.sittingAnimationState, BlessedAnimations.sitting, ageInTicks, 1f);
        //this.animateWalk(BlessedAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animateWalk(BlessedAnimations.walk, limbSwing, limbSwingAmount, 3f, 4.0f);
        this.animate(t.castingAnimationState, BlessedAnimations.casting, ageInTicks, 1f);
        this.animate(t.idleAnimationState, BlessedAnimations.breathe, ageInTicks, 0.25f);
        this.animate(t.leftEarWiggleState, BlessedAnimations.ear_twitch_left, ageInTicks, 1f);
        this.animate(t.rightEarWiggleState, BlessedAnimations.ear_twitch_right, ageInTicks, 1f);
        this.animate(t.tailWiggleState, BlessedAnimations.tail_wiggle, ageInTicks, 1f);
    }
}
