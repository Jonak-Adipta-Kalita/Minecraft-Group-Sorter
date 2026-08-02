package jak.groupsorter.group;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class GroupResolver {
    public static Optional<Group> resolveGroup(ItemStack stack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (Group group : GroupReloadListener.getLoadedGroups().values()) {
            if (group.items().contains(itemId)) {
                return Optional.of(group);
            }
        }
        return Optional.empty();
    }
}
