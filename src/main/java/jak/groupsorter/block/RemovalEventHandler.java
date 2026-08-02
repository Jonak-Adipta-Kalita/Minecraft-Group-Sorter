package jak.groupsorter.block;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.attachments.LinkedChestOutputData;
import jak.groupsorter.attachments.ModAttachments;
import jak.groupsorter.block.azurite_chest.AzuriteChestEntity;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import jak.groupsorter.entity.azurite_golem.AzuriteGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class RemovalEventHandler {
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
                return;
            }

            for (BlockPos chestPos : controller.getLinkedInputChests()) {
                if (level.getBlockEntity(chestPos) instanceof AzuriteChestEntity chest) {
                    chest.setLinkedController(null);
                }
            }

            for (Identifier group : controller.getAllOutputChestGroups()) {
                for (BlockPos chestPos : controller.getOutputChestsForGroup(group)) {
                    if (level.getBlockEntity(chestPos) instanceof BlockEntity chestEntity
                        && chestEntity.hasData(ModAttachments.OUTPUT_CHEST_LINK.get())) {
                        chestEntity.removeData(ModAttachments.OUTPUT_CHEST_LINK.get());
                        chestEntity.setChanged();
                    }
                }
            }

            if (level instanceof ServerLevel serverLevel) {
                for (UUID golemId : controller.getAllAssignedGolemIds()) {
                    if (serverLevel.getEntity(golemId) instanceof AzuriteGolem golem) {
                        golem.setBoundControllerPos(null);
                    }
                }
            }

            event.getPlayer().sendOverlayMessage(
                Component.literal("Controller destroyed — all links and assignments cleared")
            );

            return;
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

            LinkedChestOutputData data = blockEntity.getData(ModAttachments.OUTPUT_CHEST_LINK.get());
            if (data != null && level.getBlockEntity(data.controllerPos()) instanceof ControllerBlockEntity controller) {
                controller.removeOutputChest(data.group(), pos);

                event.getPlayer().sendOverlayMessage(
                    Component.literal("Unlinked Output-Chest mapped to \"" + data.group().toString() + "\" ;-;")
                );
            }
        }
    }


    @SubscribeEvent
    public static void onGolemDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof AzuriteGolem golem) {
            var controllerPos = golem.getBoundControllerPos();
            if (controllerPos != null && golem.level().getBlockEntity(controllerPos) instanceof ControllerBlockEntity controller) {
                UUID golemId = golem.getUUID();
                Set<Identifier> owned = controller.getGroupsAssignedToGolem(golemId);
                for (Identifier group : owned) {
                    controller.unassignGolemGroup(group);
                }

                if (!owned.isEmpty() && event.getSource().getEntity() instanceof Player player) {
                    player.sendOverlayMessage(Component.literal("Azurite Golem died — its " + owned.size() + " group assignment(s) have been released"));
                }
            }
        }
    }
}
