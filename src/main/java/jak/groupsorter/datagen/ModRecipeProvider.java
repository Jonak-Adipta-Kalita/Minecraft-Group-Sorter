package jak.groupsorter.datagen;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.ModBlocks;
import jak.groupsorter.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public @NonNull String getName() {
            return "JAKGroupSorter Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModBlocks.AZURITE_BLOCK.get())
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .define('A', ModItems.AZURITE.get())
            .unlockedBy(getHasName(ModItems.AZURITE.get()), has(ModItems.AZURITE))
            .group("azurite_block")
            .save(output, "jakgroupsorter:azurite_block_from_azurite");

        shapeless(RecipeCategory.MISC, ModItems.AZURITE.get(), 9)
            .requires(ModBlocks.AZURITE_BLOCK)
            .unlockedBy(getHasName(ModBlocks.AZURITE_BLOCK.get()), has(ModBlocks.AZURITE_BLOCK))
            .group("azurite")
            .save(output, "jakgroupsorter:azurite_from_azurite_block");

        shaped(RecipeCategory.MISC, ModBlocks.CONTROLLER.get())
            .pattern("III")
            .pattern("ARA")
            .pattern("AAA")
            .define('A', ModBlocks.AZURITE_BLOCK.get())
            .define('I', Items.IRON_INGOT)
            .define('R', Blocks.REDSTONE_BLOCK)
            .unlockedBy(getHasName(ModBlocks.AZURITE_BLOCK.get()), has(ModBlocks.AZURITE_BLOCK))
            .group("chest_room_controller")
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.CHEST_ROOM_LINKER.get())
            .pattern("AAA")
            .pattern(" C ")
            .pattern("ACA")
            .define('A', ModItems.AZURITE.get())
            .define('C', Items.COPPER_INGOT)
            .unlockedBy(getHasName(ModItems.AZURITE.get()), has(ModItems.AZURITE))
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .group("chest_room_linker")
            .save(output);

        List<ItemLike> AZURITE_SMELTABLES = List.of(ModItems.RAW_AZURITE, ModBlocks.AZURITE_ORE, ModBlocks.AZURITE_DEEPSLATE_ORE);
        oreSmelting(AZURITE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.AZURITE.get(), 0.25f, 200, "azurite");
        oreBlasting(AZURITE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.AZURITE.get(), 0.25f, 100, "azurite");
    }

    @Override
    protected <T extends AbstractCookingRecipe> void oreCooking(AbstractCookingRecipe.@NonNull Factory<T> factory, List<ItemLike> smeltables,
                                                                @NonNull RecipeCategory craftingCategory, @NonNull CookingBookCategory cookingCategory,
                                                                @NonNull ItemLike result, float experience, int cookingTime, @NonNull String group,
                                                                @NonNull String fromDesc) {
        for (ItemLike itemlike : smeltables) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), craftingCategory, cookingCategory, result, experience, cookingTime, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                .save(output, JAKGroupSorter.MOD_ID + ":" + getItemName(result) + fromDesc + "_" + getItemName(itemlike));
        }
    }
}
