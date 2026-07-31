package jak.groupsorter.block.azurite_chest;

import jak.groupsorter.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AzuriteChestEntity extends ChestBlockEntity {
    private @Nullable BlockPos linkedController;

    public AzuriteChestEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.AZURITE_CHEST.get(), worldPosition, blockState);
    }

    public @Nullable BlockPos getLinkedController() {
        return this.linkedController;
    }

    public void setLinkedController(@Nullable BlockPos pos) {
        this.linkedController = pos;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        if (this.linkedController != null) {
            output.store("linked_controller", BlockPos.CODEC, this.linkedController);
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.linkedController = input.read("linked_controller", BlockPos.CODEC).orElse(null);
    }
}
