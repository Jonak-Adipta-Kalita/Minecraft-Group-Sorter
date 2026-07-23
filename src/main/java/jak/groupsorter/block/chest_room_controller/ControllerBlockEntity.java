package jak.groupsorter.block.chest_room_controller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import jak.groupsorter.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ControllerBlockEntity extends BlockEntity {
    private final Map<Identifier, BlockPos> groupToOutputChest = new HashMap<>();
    private final Set<BlockPos> linkedInputChests = new HashSet<>();
    private final Map<Identifier, UUID> groupAssignments = new HashMap<>();

    public ControllerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.CONTROLLER.get(), worldPosition, blockState);
    }

    // --- Output chest mapping ---

    public void setOutputChest(Identifier group, BlockPos pos) {
        this.groupToOutputChest.put(group, pos);
        this.setChanged();
    }

    public void removeOutputChest(Identifier group) {
        this.groupToOutputChest.remove(group);
        this.setChanged();
    }

    public Optional<BlockPos> getOutputChest(Identifier group) {
        return Optional.ofNullable(this.groupToOutputChest.get(group));
    }

    public Map<Identifier, BlockPos> getAllOutputChests() {
        return Map.copyOf(this.groupToOutputChest);
    }

    // --- Input chest linking ---

    public void linkInputChest(BlockPos pos) {
        this.linkedInputChests.add(pos);
        this.setChanged();
    }

    public void unlinkInputChest(BlockPos pos) {
        this.linkedInputChests.remove(pos);
        this.setChanged();
    }

    public Set<BlockPos> getLinkedInputChests() {
        return Set.copyOf(this.linkedInputChests);
    }

    // --- Golem <-> group assignment ---

    public boolean isGroupClaimed(Identifier group) {
        return this.groupAssignments.containsKey(group);
    }

    public Optional<UUID> getGroupOwner(Identifier group) {
        return Optional.ofNullable(this.groupAssignments.get(group));
    }

    public boolean isGroupClaimedByOther(Identifier group, UUID golemId) {
        UUID owner = this.groupAssignments.get(group);
        return owner != null && !owner.equals(golemId);
    }

    public void assignGroupToGolem(Identifier group, UUID golemId) {
        this.groupAssignments.put(group, golemId);
        this.setChanged();
    }

    public void unassignGroup(Identifier group) {
        this.groupAssignments.remove(group);
        this.setChanged();
    }

    public Set<Identifier> getGroupsAssignedTo(UUID golemId) {
        Set<Identifier> result = new HashSet<>();
        for (Map.Entry<Identifier, UUID> entry : this.groupAssignments.entrySet()) {
            if (entry.getValue().equals(golemId)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void clearAllAssignmentsForGolem(UUID golemId) {
        this.groupAssignments.entrySet().removeIf(e -> e.getValue().equals(golemId));
        this.setChanged();
    }

    // --- Accessories ---

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        ValueOutput.TypedOutputList<OutputChestEntry> outputList =
            output.list("output_chests", OutputChestEntry.CODEC);
        for (Map.Entry<Identifier, BlockPos> entry : this.groupToOutputChest.entrySet()) {
            outputList.add(new OutputChestEntry(entry.getKey(), entry.getValue()));
        }

        ValueOutput.TypedOutputList<BlockPos> inputList = output.list("input_chests", BlockPos.CODEC);
        for (BlockPos pos : this.linkedInputChests) {
            inputList.add(pos);
        }

        ValueOutput.TypedOutputList<GroupAssignmentEntry> assignmentList =
            output.list("group_assignments", GroupAssignmentEntry.CODEC);
        for (Map.Entry<Identifier, UUID> entry : this.groupAssignments.entrySet()) {
            assignmentList.add(new GroupAssignmentEntry(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.groupToOutputChest.clear();
        input.list("output_chests", OutputChestEntry.CODEC).ifPresent(list ->
            list.forEach(e -> this.groupToOutputChest.put(e.group(), e.pos())));

        this.linkedInputChests.clear();
        input.list("input_chests", BlockPos.CODEC).ifPresent(list ->
            list.forEach(this.linkedInputChests::add));

        this.groupAssignments.clear();
        input.list("group_assignments", GroupAssignmentEntry.CODEC).ifPresent(list ->
            list.forEach(e -> this.groupAssignments.put(e.group(), e.golemId())));
    }
}

record OutputChestEntry(Identifier group, BlockPos pos) {
    public static final Codec<OutputChestEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("group").forGetter(OutputChestEntry::group),
        BlockPos.CODEC.fieldOf("pos").forGetter(OutputChestEntry::pos)
    ).apply(instance, OutputChestEntry::new));
}

record GroupAssignmentEntry(Identifier group, UUID golemId) {
    public static final Codec<GroupAssignmentEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("group").forGetter(GroupAssignmentEntry::group),
        UUIDUtil.CODEC.fieldOf("golem_id").forGetter(GroupAssignmentEntry::golemId)
    ).apply(instance, GroupAssignmentEntry::new));
}
