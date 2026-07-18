package jak.groupsorter.entity.azurite_golem;

import jak.groupsorter.JAKGroupSorter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.CopperGolemRenderer; // or whatever base you're extending
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class AzuriteGolemRenderer extends CopperGolemRenderer {
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "textures/entity/azurite_golem.png");

    public AzuriteGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull CopperGolemRenderState state) {
        return TEXTURE;
    }
}
