package jak.groupsorter.block;

import jak.groupsorter.block.azurite_chest.AzuriteChestEntity;
import jak.groupsorter.block.azurite_chest.AzuriteChestRenderer;
import jak.groupsorter.block.chest_room_controller.ControllerBlockEntity;
import jak.groupsorter.block.chest_room_controller.ControllerBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import jak.groupsorter.JAKGroupSorter;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JAKGroupSorter.MOD_ID);

    public static final Supplier<BlockEntityType<ChestBlockEntity>> AZURITE_CHEST =
        BLOCK_ENTITIES.register("azurite_chest",
            () -> new BlockEntityType<>(AzuriteChestEntity::new, ModBlocks.AZURITE_CHEST.get()));

    public static final Supplier<BlockEntityType<ControllerBlockEntity>> CONTROLLER =
        BLOCK_ENTITIES.register("chest_room_controller",
            () -> new BlockEntityType<>(ControllerBlockEntity::new, ModBlocks.CONTROLLER.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.AZURITE_CHEST.get(), AzuriteChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CONTROLLER.get(), ControllerBlockRenderer::new);
    }
}
