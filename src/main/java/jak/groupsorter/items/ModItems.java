package jak.groupsorter.items;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.entity.ModEntities;
import jak.groupsorter.items.chest_room_linker.LinkerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JAKGroupSorter.MOD_ID);

    public static final DeferredItem<Item> AZURITE = ITEMS.registerSimpleItem("azurite");
    public static final DeferredItem<Item> RAW_AZURITE = ITEMS.registerSimpleItem("raw_azurite");
    public static final DeferredItem<Item> AZURITE_GOLEM_SPAWN_EGG = ITEMS.registerItem("azurite_golem_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.AZURITE_GOLEM.get())));

    public static final DeferredItem<Item> CHEST_ROOM_LINKER = ITEMS.registerItem("chest_room_linker", properties -> new LinkerItem(properties.stacksTo(1)));

    public static ResourceKey<Item> getRK(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).get();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
