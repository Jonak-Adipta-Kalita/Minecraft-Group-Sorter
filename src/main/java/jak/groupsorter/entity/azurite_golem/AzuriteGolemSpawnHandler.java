package jak.groupsorter.entity.azurite_golem;

import jak.groupsorter.JAKGroupSorter;
import jak.groupsorter.block.ModBlocks;
import jak.groupsorter.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = JAKGroupSorter.MOD_ID)
public class AzuriteGolemSpawnHandler {
    private static BlockPattern azuriteGolemPattern;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public static BlockPattern getOrCreateAzuriteGolemPattern() {
        if (azuriteGolemPattern == null) {
            azuriteGolemPattern = BlockPatternBuilder.start()
                .aisle("^", "#")
                .where('^', BlockInWorld.hasState(state -> state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN)))
                .where('#', BlockInWorld.hasState(state -> state.is(ModBlocks.AZURITE_BLOCK.get())))
                .build();
        }

        return azuriteGolemPattern;
    }

    @SubscribeEvent
    public static void onPumpkinPlaced(BlockEvent.EntityPlaceEvent event) {
        BlockState placed = event.getPlacedBlock();
        if (!placed.is(Blocks.CARVED_PUMPKIN) && !placed.is(Blocks.JACK_O_LANTERN)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPattern pattern = getOrCreateAzuriteGolemPattern();
        BlockPattern.BlockPatternMatch match = pattern.find(serverLevel, event.getPos());
        if (match == null) {
            return;
        }

        for (int x = 0; x < match.getWidth(); x++) {
            for (int y = 0; y < match.getHeight(); y++) {
                BlockInWorld block = match.getBlock(x, y, 0);
                serverLevel.setBlock(block.getPos(), Blocks.AIR.defaultBlockState(), 2);
                serverLevel.levelEvent(2001, block.getPos(), Block.getId(block.getState()));
            }
        }

        CopperGolem golem = ModEntities.AZURITE_GOLEM.get().create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (golem != null) {
            BlockPos spawnPos = match.getBlock(0, 0, 0).getPos();
            golem.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
            serverLevel.addFreshEntity(golem);

            BlockInWorld copperBlock = match.getBlock(0, 1, 0);
            BlockInWorld pumpkinBlock = match.getBlock(0, 0, 0);
            Direction facing = pumpkinBlock.getState().getValue(FACING);
            BlockState blockState = CopperChestBlock.getFromCopperBlock(copperBlock.getState().getBlock(), facing, serverLevel, copperBlock.getPos());
            serverLevel.setBlock(copperBlock.getPos(), blockState, 2);
        }
    }
}
