package jak.groupsorter.block.chest_room_controller;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class ControllerBlockRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState linkerItemRenderState = new ItemStackRenderState();
    public Direction facing = Direction.NORTH;
}
