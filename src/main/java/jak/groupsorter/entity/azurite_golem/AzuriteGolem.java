package jak.groupsorter.entity.azurite_golem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jspecify.annotations.NonNull;

public class AzuriteGolem extends CopperGolem {
    public AzuriteGolem(EntityType<? extends CopperGolem> type, Level level) {
        super(type, level);
        this.setWeatherState(WeatheringCopper.WeatherState.UNAFFECTED);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.getWeatherState() != WeatheringCopper.WeatherState.UNAFFECTED) {
            this.setWeatherState(WeatheringCopper.WeatherState.UNAFFECTED);
        }
    }

    @Override
    public void thunderHit(@NonNull ServerLevel level, @NonNull LightningBolt lightningBolt) {}
}
