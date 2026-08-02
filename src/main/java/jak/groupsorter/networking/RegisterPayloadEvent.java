package jak.groupsorter.networking;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.attachments.LinkedChestOutputData;
import jak.groupsorter.attachments.ModAttachments;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class RegisterPayloadEvent {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JAKGroupSorter.MOD_ID);

        registrar.playToServer(
            AssignChestGroupPayload.TYPE,
            AssignChestGroupPayload.STREAM_CODEC,
            ((payload, context) -> context.enqueueWork(() -> {
                var level = context.player().level();
                if (level.getBlockEntity(payload.controllerPos()) instanceof ControllerBlockEntity controller
                    && level.getBlockEntity(payload.chestPos()) instanceof BlockEntity chestEntity) {
                    controller.addOutputChest(payload.group(), payload.chestPos());

                    chestEntity.setData(ModAttachments.OUTPUT_CHEST_LINK.get(), new LinkedChestOutputData(payload.controllerPos(), payload.group()));
                    chestEntity.setChanged();

                    context.player().sendOverlayMessage(
                        Component.literal("Mapped \"" + payload.group() + "\" to this chest")
                    );
                }
            }))
        );

        registrar.playToServer(
            AssignGolemGroupPayload.TYPE,
            AssignGolemGroupPayload.STREAM_CODEC,
            ((payload, context) -> context.enqueueWork(() -> {
                var level = context.player().level();
                if (level.getBlockEntity(payload.controllerPos()) instanceof  ControllerBlockEntity controller) {
                    if (payload.assign()) {
                        if (!controller.isGroupClaimedByOtherGolem(payload.group(), payload.golemId())) {
                            controller.assignGolemGroup(payload.group(), payload.golemId());
                            context.player().sendOverlayMessage(
                                Component.literal("Assigned " + payload.group() + " to this golem")
                            );
                        } else {
                            context.player().sendOverlayMessage(
                                Component.literal("That group is already claimed by another golem!")
                            );
                        }
                    } else {
                        controller.unassignGolemGroup(payload.group());
                        context.player().sendOverlayMessage(
                            Component.literal("Unassigned " + payload.group() + " from this golem")
                        );
                    }
                }
            }))
        );
    }
}
