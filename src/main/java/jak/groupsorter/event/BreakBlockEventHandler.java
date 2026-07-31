package jak.groupsorter.event;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.azurite_chest.AzuriteChestEntity;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class BreakBlockEventHandler {
    @SubscribeEvent
    public static void onBreak(BreakBlockEvent event) {
        LevelAccessor level = event.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(event.getPos());

        if (blockEntity instanceof ControllerBlockEntity controller) {
            if (controller.isControllerClaimed() && !controller.hasLinkerItem()) {
                event.setCanceled(true);
                event.getPlayer().sendOverlayMessage(
                    Component.literal("Insert this controller's key before breaking it!")
                );
            }
        }

        if (blockEntity instanceof AzuriteChestEntity chest) {
            if (chest.getLinkedController() != null) {
                BlockPos controllerPos = chest.getLinkedController();
                if (level.getBlockEntity(controllerPos) instanceof ControllerBlockEntity controller) {
                    controller.unlinkInputChest(event.getPos());
                }
                event.getPlayer().sendOverlayMessage(
                    Component.literal("Unlinked Input-Chest ;-;")
                );
            }
        }
    }
}
