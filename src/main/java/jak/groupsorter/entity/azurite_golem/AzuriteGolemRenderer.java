package jak.groupsorter.entity.azurite_golem;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.entity.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class AzuriteGolemRenderer extends MobRenderer<AzuriteGolem, AzuriteGolemRenderState, AzuriteGolemModel> {
    public AzuriteGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new AzuriteGolemModel(context.bakeLayer(ModModelLayers.AZURITE_GOLEM)), 0.5F);
        this.addLayer(
            new LivingEntityEmissiveLayer<>(
                this,
                getEyeTextureLocationProvider(),
                (_, _) -> 1.0F,
                new AzuriteGolemModel(context.bakeLayer(ModModelLayers.AZURITE_GOLEM)),
                RenderTypes::eyes,
                false
            )
        );
        this.addLayer(new ItemInHandLayer<>(this));
    }

    private static Function<AzuriteGolemRenderState, Identifier> getEyeTextureLocationProvider() {
        return _ ->  Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "textures/entity/azurite_golem/eyes.png");
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull AzuriteGolemRenderState state) {
        return Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "textures/entity/azurite_golem/golem.png");
    }

    @Override
    public @NonNull AzuriteGolemRenderState createRenderState() {
        return new AzuriteGolemRenderState();
    }

    public void extractRenderState(@NonNull AzuriteGolem entity, @NonNull AzuriteGolemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
        state.azuriteGolemState = entity.getState();
        state.idleAnimationState.copyFrom(entity.getIdleAnimationState());
        state.interactionGetItem.copyFrom(entity.getInteractionGetItemAnimationState());
        state.interactionGetNoItem.copyFrom(entity.getInteractionGetNoItemAnimationState());
        state.interactionDropItem.copyFrom(entity.getInteractionDropItemAnimationState());
        state.interactionDropNoItem.copyFrom(entity.getInteractionDropNoItemAnimationState());
    }
}
