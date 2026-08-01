package jak.groupsorter.networking;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class RegisterPayloadEvent {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JAKGroupSorter.MOD_ID);

        registrar.playToServer(
            AssignGroupPayload.TYPE,
            AssignGroupPayload.STREAM_CODEC,
            ((payload, context) -> context.enqueueWork(() -> {
                if (context.player().level().getBlockEntity(payload.controllerPos()) instanceof ControllerBlockEntity controller) {
                    controller.addOutputChest(payload.group(), payload.chestPos());
                }
            }))
        );
    }
}
