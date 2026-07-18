package jak.groupsorter.entity.azurite_golem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.Level;

public class AzuriteGolem extends CopperGolem {
    public AzuriteGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
    }
}
