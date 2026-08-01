package jak.groupsorter.menu.group_picker;

import jak.groupsorter.menu.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class GroupPickerMenu extends AbstractContainerMenu {
    public final BlockPos controllerPos;
    public final BlockPos chestPos;

    public GroupPickerMenu(int containerId, Inventory playerInv, BlockPos controllerPos, BlockPos chestPos) {
        super(ModMenus.GROUP_PICKER.get(), containerId);
        this.controllerPos = controllerPos;
        this.chestPos = chestPos;
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
