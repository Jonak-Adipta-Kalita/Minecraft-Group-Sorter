package jak.groupsorter.block.azurite_chest;

import jak.groupsorter.JAKGroupSorter;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AzuriteChestRenderer extends ChestRenderer<ChestBlockEntity> {
    public AzuriteChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static final MultiblockChestResources<Identifier> AZURITE_TEXTURES = new MultiblockChestResources<>(
        Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "azurite"),
        Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "azurite_left"),
        Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "azurite_right")
    );

    @Override
    @Nullable
    protected SpriteId getCustomSprite(@NonNull ChestBlockEntity blockEntity, ChestRenderState renderState) {
        return Sheets.CHEST_MAPPER.apply(AZURITE_TEXTURES.select(renderState.type));
    }
}
