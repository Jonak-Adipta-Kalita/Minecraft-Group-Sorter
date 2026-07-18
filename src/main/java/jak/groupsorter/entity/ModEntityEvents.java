package jak.groupsorter.entity;

import jak.groupsorter.JAKGroupSorter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.minecraft.world.entity.animal.golem.CopperGolem;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.AZURITE_GOLEM.get(), CopperGolem.createAttributes().build());
    }
}
