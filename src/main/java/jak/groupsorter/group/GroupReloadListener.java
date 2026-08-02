package jak.groupsorter.group;

import jak.groupsorter.JAKGroupSorter;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class GroupReloadListener extends SimpleJsonResourceReloadListener<GroupDefinition> {
    private static Map<Identifier, Group> LOADED_GROUPS = Map.of();

    public GroupReloadListener() {
        super(GroupDefinition.CODEC, FileToIdConverter.json("sorter_groups"));
    }

    @Override
    protected void apply(Map<Identifier, GroupDefinition> parsed, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        Map<Identifier, Group> result = new HashMap<>();
        for (Map.Entry<Identifier, GroupDefinition> entry : parsed.entrySet()) {
            Identifier id = entry.getKey();
            GroupDefinition def = entry.getValue();
            result.put(id, new Group(id, def.displayName(), def.items()));
        }
        LOADED_GROUPS = Map.copyOf(result);
        JAKGroupSorter.LOGGER.info("Loaded {} sorter groups", LOADED_GROUPS.size());
    }

    public static Map<Identifier, Group> getLoadedGroups() {
        return LOADED_GROUPS;
    }
}
