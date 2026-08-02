package jak.groupsorter.menu.chest_group_picker;

import jak.groupsorter.group.Group;
import jak.groupsorter.group.GroupReloadListener;
import jak.groupsorter.menu.GroupGridLayout;
import jak.groupsorter.networking.AssignChestGroupPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChestGroupPickerScreen extends Screen implements MenuAccess<ChestGroupPickerMenu> {
    private final ChestGroupPickerMenu menu;

    public ChestGroupPickerScreen(ChestGroupPickerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public @NonNull ChestGroupPickerMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        List<Group> groups = new ArrayList<>(GroupReloadListener.getLoadedGroups().values());
        int columns = GroupGridLayout.computeColumns(this.width);

        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            boolean isCurrent = group.id().equals(this.menu.currentGroup);
            String label = group.displayName() + (isCurrent ? " ✓" : "");

            Button button = Button.builder(
                Component.literal(label),
                _ -> {
                    ClientPacketDistributor.sendToServer(
                        new AssignChestGroupPayload(this.menu.controllerPos, this.menu.chestPos, group.id())
                    );
                    this.onClose();
                }
            ).bounds(0, 0, GroupGridLayout.buttonWidth(), GroupGridLayout.buttonHeight()).build();

            GroupGridLayout.place(button, i, columns, this.width);
            this.addRenderableWidget(button);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.closeContainer();
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
