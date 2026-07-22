package jak.groupsorter.group;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class DebugGroupsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("groupsorter_debug")
                .then(Commands.literal("list_groups")
                    .executes(ctx -> {
                        for (Group g : GroupReloadListener.getLoadedGroups().values()) {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                g.id() + " -> " + g.displayName() + " items=" + g.items() + " tags=" + g.tags()
                            ), false);
                        }
                        return 1;
                    }))
                .then(Commands.literal("resolve_held")
                    .executes(ctx -> {
                        Player player = ctx.getSource().getPlayerOrException();
                        ItemStack held = player.getMainHandItem();
                        Optional<Group> resolved = GroupResolver.resolveGroup(held);
                        ctx.getSource().sendSuccess(() ->
                            Component.literal("Held item resolves to: " + resolved.map(g -> g.id().toString()).orElse("NO GROUP")), false);
                        return 1;
                    }))
        );
    }
}
