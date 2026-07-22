package jak.groupsorter.block.azurite_chest;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import jak.groupsorter.block.ModBlockEntities;
import jak.groupsorter.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.NonNull;

public class AzuriteChestBlock extends ChestBlock {
    public AzuriteChestBlock(SoundEvent openSound, SoundEvent closeSound, BlockBehaviour.Properties properties) {
        super(ModBlockEntities.AZURITE_CHEST::get, openSound, closeSound, properties);
    }

    public static final MapCodec<AzuriteChestBlock> CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
                BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound),
                BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound),
                propertiesCodec()
            )
            .apply(i, AzuriteChestBlock::new));

    @Override
    public @NonNull MapCodec<? extends ChestBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean chestCanConnectTo(BlockState blockState) {
        return blockState.is(ModBlocks.AZURITE_CHEST.get()) && blockState.hasProperty(ChestBlock.TYPE);
    }

    @Override
    public @NonNull BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ChestBlockEntity(ModBlockEntities.AZURITE_CHEST.get(), pos, state);
    }

    public static BlockState getFromAzuriteBlock(Direction facing, Level level, BlockPos pos) {
        ChestType chestType = ModBlocks.AZURITE_CHEST.get() instanceof AzuriteChestBlock azuriteChestBlock
            ? azuriteChestBlock.getChestType(level, pos, facing)
            : ChestType.SINGLE;
        return ModBlocks.AZURITE_CHEST.get().defaultBlockState()
            .setValue(FACING, facing)
            .setValue(TYPE, chestType);
    }
}
