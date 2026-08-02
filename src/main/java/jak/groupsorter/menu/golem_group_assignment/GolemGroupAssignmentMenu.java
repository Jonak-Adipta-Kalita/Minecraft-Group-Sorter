package jak.groupsorter.menu.golem_group_assignment;

import jak.groupsorter.menu.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;

public class GolemGroupAssignmentMenu extends AbstractContainerMenu {
    public final BlockPos controllerPos;
    public final UUID golemId;
    public final Map<Identifier, UUID> groupOwners;

    public GolemGroupAssignmentMenu(int containerId, Inventory playerInv, BlockPos controllerPos, UUID golemId, Map<Identifier, UUID> groupOwners) {
        super(ModMenus.GOLEM_GROUP_ASSIGNMENT.get(), containerId);
        this.controllerPos = controllerPos;
        this.golemId = golemId;
        this.groupOwners = groupOwners;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }
}
