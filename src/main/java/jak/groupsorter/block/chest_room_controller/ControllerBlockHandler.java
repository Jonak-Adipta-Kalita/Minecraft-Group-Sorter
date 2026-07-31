package jak.groupsorter.block.chest_room_controller;

import jak.groupsorter.JAKGroupSorter;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class ControllerBlockHandler {
    @SubscribeEvent
    public static void onBreak(BreakBlockEvent event) {
        if (event.getLevel().getBlockEntity(event.getPos()) instanceof ControllerBlockEntity controller) {
            if (!controller.hasLinkerItem()) {
                event.setCanceled(true);
                event.getPlayer().sendOverlayMessage(
                    Component.literal("Insert this controller's key before breaking it!")
                );
            }
        }
    }
}
