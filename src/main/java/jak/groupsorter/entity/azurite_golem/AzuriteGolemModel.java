package jak.groupsorter.entity.azurite_golem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import org.jspecify.annotations.NonNull;

public class AzuriteGolemModel extends EntityModel<AzuriteGolemRenderState> implements ArmedModel<AzuriteGolemRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation walkWithItemAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation interactionGetItem;
    private final KeyframeAnimation interactionGetNoItem;
    private final KeyframeAnimation interactionDropItem;
    private final KeyframeAnimation interactionDropNoItem;

    public AzuriteGolemModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.walkAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK.bake(root);
        this.walkWithItemAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK_ITEM.bake(root);
        this.idleAnimation = CopperGolemAnimation.COPPER_GOLEM_IDLE.bake(root);
        this.interactionGetItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_GET.bake(root);
        this.interactionGetNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_NOGET.bake(root);
        this.interactionDropItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_DROP.bake(root);
        this.interactionDropNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_NODROP.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition().transformed(p -> p.translated(0.0F, 24.0F, 0.0F));
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition body = root.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, CubeDeformation.NONE),
            PartPose.offset(0.0F, -5.0F, 0.0F)
        );
        body.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.015F))
                .texOffs(56, 0)
                .addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(37, 8)
                .addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0)
                .addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
            PartPose.offset(0.0F, -6.0F, 0.0F)
        );
        body.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(36, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(-4.0F, -6.0F, 0.0F)
        );
        body.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(50, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(4.0F, -6.0F, 0.0F)
        );
        root.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(0.0F, -5.0F, 0.0F)
        );
        root.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(0.0F, -5.0F, 0.0F)
        );
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void setupAnim(@NonNull AzuriteGolemRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        if (state.rightHandItemState.isEmpty() && state.leftHandItemState.isEmpty()) {
            this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
        } else {
            this.walkWithItemAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
            this.poseHeldItemArmsIfStill();
        }

        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
        this.interactionGetItem.apply(state.interactionGetItem, state.ageInTicks);
        this.interactionGetNoItem.apply(state.interactionGetNoItem, state.ageInTicks);
        this.interactionDropItem.apply(state.interactionDropItem, state.ageInTicks);
        this.interactionDropNoItem.apply(state.interactionDropNoItem, state.ageInTicks);
    }

    public void translateToHand(AzuriteGolemRenderState state, @NonNull HumanoidArm arm, @NonNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        ModelPart activeArm = arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
        activeArm.translateAndRotate(poseStack);
        if (state.azuriteGolemState.equals(CopperGolemState.IDLE)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(arm == HumanoidArm.RIGHT ? -90.0F : 90.0F));
            poseStack.translate(0.0F, 0.0F, 0.125F);
        } else {
            poseStack.scale(0.55F, 0.55F, 0.55F);
            poseStack.translate(-0.125F, 0.3125F, -0.1875F);
        }
    }

    private void poseHeldItemArmsIfStill() {
        this.rightArm.xRot = Math.min(this.rightArm.xRot, -0.87266463F);
        this.leftArm.xRot = Math.min(this.leftArm.xRot, -0.87266463F);
        this.rightArm.yRot = Math.min(this.rightArm.yRot, -0.1134464F);
        this.leftArm.yRot = Math.max(this.leftArm.yRot, 0.1134464F);
        this.rightArm.zRot = Math.min(this.rightArm.zRot, -0.064577185F);
        this.leftArm.zRot = Math.max(this.leftArm.zRot, 0.064577185F);
    }

}
