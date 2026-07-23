package jak.groupsorter.datagen;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, JAKGroupSorter.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.getRK(ModBlocks.AZURITE_BLOCK.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_ORE.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_DEEPSLATE_ORE.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_CHEST.get()))
            .add(ModBlocks.getRK(ModBlocks.CONTROLLER.get()));

        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(ModBlocks.getRK(ModBlocks.AZURITE_BLOCK.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_ORE.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_DEEPSLATE_ORE.get()))
            .add(ModBlocks.getRK(ModBlocks.AZURITE_CHEST.get()))
            .add(ModBlocks.getRK(ModBlocks.CONTROLLER.get()));
    }
}
