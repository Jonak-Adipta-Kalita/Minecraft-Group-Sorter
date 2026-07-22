package jak.groupsorter.entity;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.entity.azurite_golem.AzuriteGolemModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ModModelLayers {
    public static final ModelLayerLocation AZURITE_GOLEM =
        new ModelLayerLocation(Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "azurite_golem"), "main");

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.AZURITE_GOLEM, AzuriteGolemModel::createBodyLayer);
    }
}
