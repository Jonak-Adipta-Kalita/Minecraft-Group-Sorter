package jak.groupsorter.block.chest_room_controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ControllerBlockRenderer implements BlockEntityRenderer<ControllerBlockEntity, ControllerBlockRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ControllerBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull ControllerBlockRenderState createRenderState() {
        return new ControllerBlockRenderState();
    }

    @Override
    public void extractRenderState(
        @NonNull ControllerBlockEntity blockEntity,
        @NonNull ControllerBlockRenderState renderState,
        float partialTick,
        @NonNull Vec3 cameraPos,
        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = blockEntity.getLinkerItem();
        this.itemModelResolver.updateForTopItem(
            renderState.linkerItemRenderState,
            stack,
            ItemDisplayContext.FIXED,
            blockEntity.getLevel(),
            null,
            0
        );
    }

    @Override
    public void submit(
        @NonNull ControllerBlockRenderState renderState,
        @NonNull PoseStack poseStack,
        @NonNull SubmitNodeCollector collector,
        @NonNull CameraRenderState cameraState
    ) {
        if (renderState.linkerItemRenderState.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(1.001, 0.8125, 0.501);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        poseStack.scale(0.4F, 0.4F, 0.4F);
        poseStack.scale(1.0F, 1.0F, 0.0625F);

        renderState.linkerItemRenderState.submit(
            poseStack,
            collector,
            renderState.lightCoords,
            net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
            0
        );

        poseStack.popPose();
    }
}
