package jak.groupsorter.items.chest_room_linker;

import jak.groupsorter.block.ModBlocks;
import jak.groupsorter.block.azurite_chest.AzuriteChestEntity;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import jak.groupsorter.data_components.ModDataComponents;
import jak.groupsorter.menu.group_picker.GroupPickerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.NonNull;

public class LinkerItem extends Item {
    public LinkerItem(Properties properties) {
        super(properties);
    }

    private boolean isVanillaChestLike(BlockState state) {
        return state.is(net.minecraft.world.level.block.Blocks.CHEST)
            || state.is(net.minecraft.world.level.block.Blocks.TRAPPED_CHEST)
            || state.is(net.minecraft.world.level.block.Blocks.BARREL);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos controllerPos = stack.get(ModDataComponents.BOUND_CONTROLLER_POS.get());

        if (controllerPos == null) {
            if (player != null) {
                player.sendOverlayMessage(Component.literal("This Linker isn't bounded to a Controller yet"));
            }
            return InteractionResult.FAIL;
        }

        if (!(level.getBlockEntity(controllerPos) instanceof ControllerBlockEntity controller)) {
            if (player != null) {
                player.sendOverlayMessage(Component.literal("Bound controller no longer exists!"));
            }
            return InteractionResult.FAIL;
        }

        if (state.is(ModBlocks.AZURITE_CHEST.get()) && level.getBlockEntity(pos) instanceof AzuriteChestEntity azuriteChest) {
            boolean wasLinked = controller.isInputChestLinked(pos);
            boolean isDouble = state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE;
            BlockPos otherHalfPos = isDouble ? ChestBlock.getConnectedBlockPos(pos, state) : null;

            if (wasLinked) {
                controller.unlinkInputChest(pos);
                azuriteChest.setLinkedController(null);

                if (otherHalfPos != null && level.getBlockEntity(otherHalfPos) instanceof AzuriteChestEntity otherHalf) {
                    controller.unlinkInputChest(otherHalfPos);
                    otherHalf.setLinkedController(null);
                }

                if (player != null) {
                    player.sendOverlayMessage(Component.literal("Unlinked Input-Chest " + (isDouble ? " (double chest)" : "") + " ;-;"));
                }
            } else {
                controller.linkInputChest(pos);
                azuriteChest.setLinkedController(controllerPos);

                if (otherHalfPos != null && level.getBlockEntity(otherHalfPos) instanceof AzuriteChestEntity otherHalf) {
                    controller.linkInputChest(otherHalfPos);
                    otherHalf.setLinkedController(controllerPos);
                }

                if (player != null) {
                    player.sendOverlayMessage(Component.literal("Linked Input-Chest" + (isDouble ? " (double chest)" : "") + " :D"));
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (isVanillaChestLike(state) && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public @NonNull Component getDisplayName() {
                    return Component.literal("Pick a Group");
                }

                @Override
                public AbstractContainerMenu createMenu(int i, @NonNull Inventory inventory, @NonNull Player player) {
                    return new GroupPickerMenu(i, inventory, controllerPos, pos);
                }
            }, buf -> {
                BlockPos.STREAM_CODEC.encode(buf, controllerPos);
                BlockPos.STREAM_CODEC.encode(buf, pos);
            });

            return InteractionResult.SUCCESS;
        }

        // The other handlers

        return InteractionResult.PASS;
    }
}
