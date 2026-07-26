package jak.groupsorter.block.chest_room_controller;

import jak.groupsorter.block.ModBlockEntities;
import jak.groupsorter.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class ControllerBlockEntity extends BlockEntity {
    private ItemStack linkerItem;

    public ControllerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.CONTROLLER.get(), worldPosition, blockState);
        this.linkerItem = new ItemStack(ModItems.CHEST_ROOM_LINKER.get(), 1);
    }

    public ItemStack getLinkerItem() {
        return this.linkerItem;
    }

    public void setLinkerItem(ItemStack stack) {
        this.linkerItem = stack;
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("linker_item", ItemStack.OPTIONAL_CODEC, this.linkerItem);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        setLinkerItem(input.read("linker_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }
}
