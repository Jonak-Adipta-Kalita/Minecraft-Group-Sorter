package jak.groupsorter.menu.chest_group_picker;

import jak.groupsorter.menu.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ChestGroupPickerMenu extends AbstractContainerMenu {
    public final BlockPos controllerPos;
    public final BlockPos chestPos;
    public final @Nullable Identifier currentGroup;

    public ChestGroupPickerMenu(int containerId, Inventory playerInv, BlockPos controllerPos,
                                BlockPos chestPos, @Nullable Identifier currentGroup) {
        super(ModMenus.CHEST_GROUP_PICKER.get(), containerId);
        this.controllerPos = controllerPos;
        this.chestPos = chestPos;
        this.currentGroup = currentGroup;
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
