package jak.groupsorter.entity.azurite_golem;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AzuriteGolemRenderState extends ArmedEntityRenderState {
    public CopperGolemState azuriteGolemState = CopperGolemState.IDLE;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState interactionGetItem = new AnimationState();
    public final AnimationState interactionGetNoItem = new AnimationState();
    public final AnimationState interactionDropItem = new AnimationState();
    public final AnimationState interactionDropNoItem = new AnimationState();
}
