package jak.groupsorter.entity;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.entity.azurite_golem.AzuriteGolem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(Registries.ENTITY_TYPE, JAKGroupSorter.MOD_ID);

    public static final ResourceKey<EntityType<?>> AZURITE_GOLEM_RK = createRK("azurite_golem");
    public static final Supplier<EntityType<AzuriteGolem>> AZURITE_GOLEM =
        ENTITIES.register("azurite_golem", () ->
            EntityType.Builder.of(AzuriteGolem::new, MobCategory.MISC)
                .sized(0.49F, 0.98F)
                .eyeHeight(0.8125F)
                .clientTrackingRange(10)
                .build(AZURITE_GOLEM_RK)
        );

    private static ResourceKey<EntityType<?>> createRK(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, name));
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
