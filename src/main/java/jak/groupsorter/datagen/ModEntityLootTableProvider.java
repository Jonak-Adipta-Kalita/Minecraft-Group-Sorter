package jak.groupsorter.datagen;

import jak.groupsorter.entity.ModEntities;
import jak.groupsorter.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jspecify.annotations.NonNull;
import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {
    public ModEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        add(ModEntities.AZURITE_GOLEM.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ModItems.AZURITE.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 9))))));
    }

    @Override
    protected @NonNull Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(ModEntities.AZURITE_GOLEM.get());
    }
}
