package jak.groupsorter.menu.group_picker;

import jak.groupsorter.group.Group;
import jak.groupsorter.group.GroupReloadListener;
import jak.groupsorter.networking.AssignGroupPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class GroupPickerScreen extends AbstractContainerScreen<GroupPickerMenu> {
    public GroupPickerScreen(GroupPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        int y = 20;
        for (Group group : GroupReloadListener.getLoadedGroups().values()) {
            this.addRenderableWidget(Button.builder(
                Component.literal(group.displayName()),
                _ -> {
                    ClientPacketDistributor.sendToServer(new AssignGroupPayload(menu.controllerPos, menu.chestPos, group.id()));
                    this.onClose();
                }
            ).bounds(this.width / 2 - 100, y, 200, 20).build());
            y += 24;
        }
    }
}
