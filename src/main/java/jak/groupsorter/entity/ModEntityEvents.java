package jak.groupsorter.entity;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.entity.azurite_golem.AzuriteGolem;
import jak.groupsorter.entity.azurite_golem.AzuriteGolemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.AZURITE_GOLEM.get(), AzuriteGolem.createAttributes().build());
    }

    public static void registerRenderers() {
        EntityRenderers.register(ModEntities.AZURITE_GOLEM.get(), AzuriteGolemRenderer::new);
    }
}
