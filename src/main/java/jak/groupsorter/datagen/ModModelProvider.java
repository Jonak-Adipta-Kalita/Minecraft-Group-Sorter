package jak.groupsorter.datagen;

import com.mojang.math.Quadrant;
import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.ModBlocks;
import jak.groupsorter.block.azurite_chest.AzuriteChestRenderer;
import jak.groupsorter.items.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, JAKGroupSorter.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.AZURITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_AZURITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.AZURITE_GOLEM_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.declareCustomModelItem(ModItems.CHEST_ROOM_LINKER.get());

        blockModels.createTrivialCube(ModBlocks.AZURITE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.AZURITE_ORE.get());
        blockModels.createTrivialCube(ModBlocks.AZURITE_DEEPSLATE_ORE.get());

        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(
                ModBlocks.CONTROLLER.get(),
                BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "block/chest_room_controller"))
            ).with(
                PropertyDispatch.modify(HorizontalDirectionalBlock.FACING)
                    .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                    .select(Direction.EAST,  VariantMutator.Y_ROT.withValue(Quadrant.R90))
                    .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                    .select(Direction.WEST,  VariantMutator.Y_ROT.withValue(Quadrant.R270))
            )
        );

        blockModels.createChest(ModBlocks.AZURITE_CHEST.get(), ModBlocks.AZURITE_BLOCK.get(), AzuriteChestRenderer.AZURITE_TEXTURES, false);
    }
}
