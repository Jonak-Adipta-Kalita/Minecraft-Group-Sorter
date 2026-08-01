package jak.groupsorter.block;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.attachments.LinkedOutputData;
import jak.groupsorter.attachments.ModAttachments;
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
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

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
                    controller.unlinkInputChest(pos);
                }
                event.getPlayer().sendOverlayMessage(
                    Component.literal("Unlinked Input-Chest ;-;")
                );
            }
        }

        if (blockEntity != null && blockEntity.hasData(ModAttachments.OUTPUT_CHEST_LINK.get())) {
            JAKGroupSorter.LOGGER.info("meow");

            LinkedOutputData data = blockEntity.getData(ModAttachments.OUTPUT_CHEST_LINK.get());
            if (data != null && level.getBlockEntity(data.controllerPos()) instanceof ControllerBlockEntity controller) {
                controller.removeOutputChest(data.group(), pos);
            }
        }
    }
}
