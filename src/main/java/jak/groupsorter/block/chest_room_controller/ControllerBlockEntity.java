package jak.groupsorter.block.chest_room_controller;

import jak.groupsorter.block.ModBlockEntities;
import jak.groupsorter.data_components.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ControllerBlockEntity extends BlockEntity {
    private ItemStack linkerItem = ItemStack.EMPTY;
    private boolean running = false;

    private UUID controllerID = UUID.randomUUID();
    private boolean controllerClaimed = false;

    private final Set<BlockPos> linkedInputChests = new HashSet<>();

    public ControllerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.CONTROLLER.get(), worldPosition, blockState);
    }

    public UUID getControllerId() {
        return this.controllerID;
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

    public boolean hasLinkerItem() {
        return !this.linkerItem.isEmpty();
    }

    public boolean getRunning() { return this.running; }

    public void setRunning(boolean running) {
        this.running = running;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isControllerClaimed() {
        return this.controllerClaimed;
    }

    public void setControllerClaimed(boolean controllerClaimed) {
        this.controllerClaimed = controllerClaimed;
        this.setChanged();
    }

    public void linkInputChest(BlockPos pos) {
        this.linkedInputChests.add(pos);
        this.setChanged();
    }

    public void unlinkInputChest(BlockPos pos) {
        this.linkedInputChests.remove(pos);
        this.setChanged();
    }

    public boolean isInputChestLinked(BlockPos pos) {
        return this.linkedInputChests.contains(pos);
    }

    public Set<BlockPos> getLinkedInputChests() {
        return this.linkedInputChests;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.store("linker_item", ItemStack.OPTIONAL_CODEC, this.linkerItem);
        output.putBoolean("running", this.running);
        output.store("controller_id", UUIDUtil.CODEC, this.controllerID);
        output.putBoolean("controller_claimed", this.controllerClaimed);

        ValueOutput.TypedOutputList<BlockPos> inputList = output.list("linked_input_chests", BlockPos.CODEC);
        for (BlockPos chestPos : this.linkedInputChests) {
            inputList.add(chestPos);
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.linkerItem = input.read("linker_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.running = input.getBooleanOr("running", true);
        this.controllerID = input.read("controller_id", UUIDUtil.CODEC).orElseGet(UUID::randomUUID);
        this.controllerClaimed = input.getBooleanOr("controller_claimed", false);

        this.linkedInputChests.clear();
        input.list("linked_input_chests", BlockPos.CODEC).ifPresent(list -> list.forEach(this.linkedInputChests::add));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        if (this.hasLinkerItem() && this.level != null) {
            ItemStack toDrop = this.linkerItem.copy();
            toDrop.remove(ModDataComponents.BOUND_CONTROLLER.get());
            toDrop.remove(ModDataComponents.BOUND_CONTROLLER_POS.get());
            toDrop.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);

            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), toDrop);
        }
    }
}
