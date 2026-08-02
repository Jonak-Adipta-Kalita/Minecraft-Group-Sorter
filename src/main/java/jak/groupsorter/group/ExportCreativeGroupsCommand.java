package jak.groupsorter.group;

import com.mojang.brigadier.CommandDispatcher;
import jak.groupsorter.JAKGroupSorter;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class ExportCreativeGroupsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("groupsorter_export_creative")
                .executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();

                    // Force every tab to actually build its contents first
                    CreativeModeTabs.tryRebuildTabContents(
                        mc.level.enabledFeatures(),
                        mc.player != null && mc.player.canUseGameMasterBlocks(),
                        mc.level.registryAccess()
                    );

                    Path outDir = Path.of("groupsorter_export");
                    try {
                        Files.createDirectories(outDir);
                    } catch (IOException e) {
                        ctx.getSource().sendFailure(Component.literal("Failed: " + e.getMessage()));
                        return 0;
                    }

                    int count = 0;
                    for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
                        if (tab.getType() != CreativeModeTab.Type.CATEGORY) continue;

                        Set<String> itemIds = new LinkedHashSet<>();
                        for (ItemStack stack : tab.getDisplayItems()) {
                            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                            itemIds.add("\"" + id + "\"");
                        }

                        if (itemIds.isEmpty()) continue;

                        Identifier tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
                        String fileName = tabId.getPath() + ".json";
                        String displayName = tab.getDisplayName().getString();

                        String json = "{\n"
                            + "  \"display_name\": \"" + displayName.replace("\"", "\\\"") + "\",\n"
                            + "  \"items\": [\n    " + String.join(",\n    ", itemIds) + "\n  ],\n"
                            + "  \"tags\": []\n"
                            + "}\n";

                        try (Writer writer = Files.newBufferedWriter(outDir.resolve(fileName), StandardCharsets.UTF_8)) {
                            writer.write(json);
                            count++;
                        } catch (IOException e) {
                            JAKGroupSorter.LOGGER.error("Failed writing {}", fileName, e);
                        }
                    }

                    int finalCount = count;
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "Exported " + finalCount + " creative tabs to /groupsorter_export/"
                    ), false);
                    return count;
                })
        );

        dispatcher.register(
            Commands.literal("groupsorter_debug_tabs")
                .executes(ctx -> {
                    int tabCount = 0;
                    for (CreativeModeTab tab : BuiltInRegistries.CREATIVE_MODE_TAB) {
                        tabCount++;
                        var id = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
                        int itemCount = tab.getDisplayItems().size();
                        JAKGroupSorter.LOGGER.info(
                            "Tab: {} | type={} | displayItems={}",
                            id, tab.getType(), itemCount
                        );
                    }
                    int finalTabCount = tabCount;
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "Found " + finalTabCount + " total tabs in registry (see log for details)"
                    ), false);
                    return tabCount;
                })
        );
    }
}
