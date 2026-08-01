package jak.groupsorter.menu;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.menu.group_picker.GroupPickerMenu;
import jak.groupsorter.menu.group_picker.GroupPickerScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, JAKGroupSorter.MOD_ID);

    public static final Supplier<MenuType<GroupPickerMenu>> GROUP_PICKER =
        MENU_TYPES.register("group_picker", () ->
            IMenuTypeExtension.create((containerId, inv, buf) -> {
                var controllerPos = net.minecraft.core.BlockPos.STREAM_CODEC.decode(buf);
                var chestPos = net.minecraft.core.BlockPos.STREAM_CODEC.decode(buf);
                return new GroupPickerMenu(containerId, inv, controllerPos, chestPos);
            }));

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GROUP_PICKER.get(), GroupPickerScreen::new);
    }
}
