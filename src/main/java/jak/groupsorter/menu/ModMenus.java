package jak.groupsorter.menu;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.menu.chest_group_picker.ChestGroupPickerMenu;
import jak.groupsorter.menu.chest_group_picker.ChestGroupPickerScreen;
import jak.groupsorter.menu.golem_group_assignment.GolemGroupAssignmentMenu;
import jak.groupsorter.menu.golem_group_assignment.GolemGroupAssignmentScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, JAKGroupSorter.MOD_ID);

    public static final Supplier<MenuType<ChestGroupPickerMenu>> CHEST_GROUP_PICKER =
        MENU_TYPES.register("group_picker", () ->
            IMenuTypeExtension.create((containerId, inv, buf) -> {
                var controllerPos = net.minecraft.core.BlockPos.STREAM_CODEC.decode(buf);
                var chestPos = net.minecraft.core.BlockPos.STREAM_CODEC.decode(buf);
                boolean hasGroup = buf.readBoolean();
                Identifier currentGroup = hasGroup ? Identifier.STREAM_CODEC.decode(buf) : null;
                return new ChestGroupPickerMenu(containerId, inv, controllerPos, chestPos, currentGroup);
            }));

    public static final Supplier<MenuType<GolemGroupAssignmentMenu>> GOLEM_GROUP_ASSIGNMENT =
        MENU_TYPES.register("group_assignment", () ->
            IMenuTypeExtension.create((containerId, inv, buf) -> {
                BlockPos controllerPos = BlockPos.STREAM_CODEC.decode(buf);
                UUID golemId = UUIDUtil.STREAM_CODEC.decode(buf);
                int count = buf.readVarInt();
                Map<Identifier, UUID> owners = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    Identifier group = Identifier.STREAM_CODEC.decode(buf);
                    UUID owner = UUIDUtil.STREAM_CODEC.decode(buf);
                    owners.put(group, owner);
                }
                return new GolemGroupAssignmentMenu(containerId, inv, controllerPos, golemId, owners);
            })
        );

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CHEST_GROUP_PICKER.get(), ChestGroupPickerScreen::new);
        event.register(GOLEM_GROUP_ASSIGNMENT.get(), GolemGroupAssignmentScreen::new);
    }
}
