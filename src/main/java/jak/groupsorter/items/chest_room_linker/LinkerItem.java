package jak.groupsorter.items.chest_room_linker;

import jak.groupsorter.block.ModBlocks;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import jak.groupsorter.data_components.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

        if (state.is(ModBlocks.AZURITE_CHEST.get())) {
            boolean wasLinked = controller.isInputChestLinked(pos);

            if (wasLinked) {
                controller.unlinkInputChest(pos);
                if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                    BlockPos otherHalf = ChestBlock.getConnectedBlockPos(pos, state);
                    controller.unlinkInputChest(otherHalf);
                }
                if (player != null) {
                    player.sendOverlayMessage(Component.literal("Unlinked Input-Chest at ;-;"));
                }
            } else {
                controller.linkInputChest(pos);
                if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                    BlockPos otherHalf = ChestBlock.getConnectedBlockPos(pos, state);
                    controller.linkInputChest(otherHalf);
                }
                if (player != null) {
                    player.sendOverlayMessage(Component.literal("Linked Input-Chest :D"));
                }
            }

            return InteractionResult.SUCCESS;
        }

        // The other handlers

        return InteractionResult.PASS;
    }
}
