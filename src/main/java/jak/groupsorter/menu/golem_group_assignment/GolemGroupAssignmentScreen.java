package jak.groupsorter.menu.golem_group_assignment;

import jak.groupsorter.group.Group;
import jak.groupsorter.group.GroupReloadListener;
import jak.groupsorter.menu.GroupGridLayout;
import jak.groupsorter.networking.AssignGolemGroupPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GolemGroupAssignmentScreen extends Screen implements MenuAccess<GolemGroupAssignmentMenu> {
    private final GolemGroupAssignmentMenu menu;

    public GolemGroupAssignmentScreen(GolemGroupAssignmentMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public @NonNull GolemGroupAssignmentMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        List<Group> groups = new ArrayList<>(GroupReloadListener.getLoadedGroups().values());
        int columns = GroupGridLayout.computeColumns(this.width);

        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            UUID owner = this.menu.groupOwners.get(group.id());
            boolean assignedToMe = owner != null && owner.equals(this.menu.golemId);
            boolean claimedByOther = owner != null && !owner.equals(this.menu.golemId);

            String label = group.displayName() + (assignedToMe ? " ✓" : "");

            Button button = Button.builder(
                Component.literal(label),
                _ -> {
                    boolean assign = !assignedToMe;
                    ClientPacketDistributor.sendToServer(
                        new AssignGolemGroupPayload(this.menu.controllerPos, this.menu.golemId, group.id(), assign)
                    );
                    this.onClose();
                }
            ).bounds(0, 0, GroupGridLayout.buttonWidth(), GroupGridLayout.buttonHeight()).build();

            button.active = !claimedByOther;
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
