package jak.groupsorter.block.chest_room_controller;

import com.mojang.serialization.MapCodec;
import jak.groupsorter.block.ModDataComponents;
import jak.groupsorter.items.chest_room_linker.LinkerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;


public class ControllerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public ControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static final MapCodec<ControllerBlock> CODEC = simpleCodec(ControllerBlock::new);

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new ControllerBlockEntity(worldPosition, blockState);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                        @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        return handleInteraction(level, pos, player, ItemStack.EMPTY);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level,
                                                   @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        return handleInteraction(level, pos, player, stack);
    }

    private InteractionResult handleInteraction(Level level, BlockPos pos, Player player, ItemStack heldStack) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof ControllerBlockEntity controller)) {
            return InteractionResult.PASS;
        }

        if (heldStack.isEmpty()) {
            if (controller.hasLinkerItem()) {
                ItemStack toGive = controller.getLinkerItem().copy();
                controller.setLinkerItem(ItemStack.EMPTY);
                controller.setRunning(false);

                if (!player.getInventory().add(toGive)) {
                    player.drop(toGive, false);
                }

                player.sendOverlayMessage(Component.literal("Chest Room paused ;-;"));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (controller.hasLinkerItem()) {
            return InteractionResult.PASS;
        }

        if (!(heldStack.getItem() instanceof LinkerItem)) {
            return InteractionResult.PASS;
        }

        UUID heldID = heldStack.get(ModDataComponents.BOUND_CONTROLLER.get());

        if (!controller.isControllerClaimed()) {
            if (heldID != null) {
                player.sendOverlayMessage(Component.literal("Already bounded components can't be rebounded ;-;"));
                return InteractionResult.FAIL;
            }

            ItemStack toStore = heldStack.copyWithCount(1);
            toStore.set(ModDataComponents.BOUND_CONTROLLER.get(), controller.getControllerId());
            toStore.set(ModDataComponents.BOUND_CONTROLLER_POS.get(), pos);
            toStore.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            controller.setLinkerItem(toStore);
            controller.setControllerClaimed(true);
            controller.setRunning(true);
            heldStack.shrink(1);

            player.sendOverlayMessage(Component.literal("Linker bound to this controller and Chest Room started :D"));
            return InteractionResult.SUCCESS;
        }

        if (heldID != null && heldID.equals(controller.getControllerId())) {
            ItemStack toStore = heldStack.copyWithCount(1);
            controller.setLinkerItem(toStore);
            controller.setRunning(true);
            heldStack.shrink(1);

            player.sendOverlayMessage(Component.literal("Chest Room running :D"));
            return InteractionResult.SUCCESS;
        }

        player.sendOverlayMessage(Component.literal("This linker is already bound to another controller ;-;"));
        return InteractionResult.FAIL;
    }
}
