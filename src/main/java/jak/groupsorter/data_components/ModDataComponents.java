package jak.groupsorter.data_components;

import jak.groupsorter.JAKGroupSorter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JAKGroupSorter.MOD_ID);

    public static final Supplier<DataComponentType<UUID>> BOUND_CONTROLLER = DATA_COMPONENT_TYPES.register(
        "bound_controller",
        () -> DataComponentType.<UUID>builder()
            .persistent(UUIDUtil.CODEC)
            .networkSynchronized(UUIDUtil.STREAM_CODEC)
            .build());

    public static final Supplier<DataComponentType<BlockPos>> BOUND_CONTROLLER_POS = DATA_COMPONENT_TYPES.register(
        "bound_controller_pos",
        () -> DataComponentType.<BlockPos>builder()
            .persistent(BlockPos.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodec(BlockPos.CODEC))
            .build());

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
