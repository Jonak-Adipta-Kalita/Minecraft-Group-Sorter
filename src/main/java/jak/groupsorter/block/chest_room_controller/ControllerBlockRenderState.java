package jak.groupsorter.block.chest_room_controller;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ControllerBlockRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState linkerItemRenderState = new ItemStackRenderState();
}
