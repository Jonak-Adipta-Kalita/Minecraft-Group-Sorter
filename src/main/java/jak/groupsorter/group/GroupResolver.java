package jak.groupsorter.group;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;

import java.util.Optional;

public class GroupResolver {
    public static Optional<Group> resolveGroup(ItemStack stack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (Group group : GroupReloadListener.getLoadedGroups().values()) {
            if (group.items().contains(itemId)) {
                return Optional.of(group);
            }
            for (Identifier tagId : group.tags()) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
                if (stack.is(tagKey)) {
                    return Optional.of(group);
                }
            }
        }
        return Optional.empty();
    }
}
