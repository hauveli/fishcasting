package hauveli.fishcasting.common.cursed;

import hauveli.fishcasting.Fishcasting;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LerpingModel;

public class CursedModel<T extends CursedEntity & LerpingModel> extends AxolotlModel<T> {

    public CursedModel(ModelPart root) {
        super(root);
    }

    // So that I can re-remember that this is what the first argument in "model layer location" is meant to be
    private static final ResourceLocation TEXTURE = Fishcasting.id("textures/entity/cursed.png");

    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    //@Override
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            TEXTURE,
            "main");

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition top_gills = head.addOrReplaceChild("top_gills", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -5.0F));

        PartDefinition left_gills = head.addOrReplaceChild("left_gills", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -5.0F));

        PartDefinition right_gills = head.addOrReplaceChild("right_gills", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -5.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(12, 8).addBox(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 2.0F, -1.0F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(10, 12).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 2.0F, -1.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 2.5F, 3.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 2.5F, 3.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 3.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
