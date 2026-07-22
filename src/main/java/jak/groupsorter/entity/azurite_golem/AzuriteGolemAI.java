package jak.groupsorter.entity.azurite_golem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import jak.groupsorter.block.ModBlocks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AzuriteGolemAI {
    private static final Predicate<BlockState> TRANSPORT_ITEM_SOURCE_BLOCK = block -> block.is(ModBlocks.AZURITE_CHEST);
    private static final Predicate<BlockState> TRANSPORT_ITEM_DESTINATION_BLOCK = block -> block.is(Blocks.CHEST) || block.is(Blocks.TRAPPED_CHEST);

    public static List<ActivityData<AzuriteGolem>> getActivities() {
        return List.of(initCoreActivity(), initIdleActivity());
    }

    public static void updateActivity(AzuriteGolem body) {
        body.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }


    private static ActivityData<AzuriteGolem> initCoreActivity() {
        return ActivityData.create(
            Activity.CORE,
            0,
            ImmutableList.of(
                new AnimalPanic<>(1.5F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                InteractWithDoor.create(),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS)
            )
        );
    }

    private static ActivityData<AzuriteGolem> initIdleActivity() {
        return ActivityData.create(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of(
                    0,
                    new TransportItemsBetweenContainers(
                        1.0F,
                        TRANSPORT_ITEM_SOURCE_BLOCK,
                        TRANSPORT_ITEM_DESTINATION_BLOCK,
                        32,
                        8,
                        getTargetReachedInteractions(),
                        onTravelling(),
                        shouldQueueForTarget()
                    )
                ),
                Pair.of(1, SetEntityLookTargetSometimes.create(EntityTypes.PLAYER, 6.0F, UniformInt.of(40, 80))),
                Pair.of(
                    2,
                    new RunOne<>(
                        ImmutableMap.of(
                            MemoryModuleType.WALK_TARGET,
                            MemoryStatus.VALUE_ABSENT,
                            MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS,
                            MemoryStatus.VALUE_PRESENT
                        ),
                        ImmutableList.of(Pair.of(RandomStroll.stroll(1.0F, 2, 2), 1), Pair.of(new DoNothing(30, 60), 1))
                    )
                )
            )
        );
    }


    private static Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenContainers.OnTargetReachedInteraction> getTargetReachedInteractions() {
        return Map.of(
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_ITEM, SoundEvents.COPPER_GOLEM_ITEM_GET),
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_NO_ITEM, SoundEvents.COPPER_GOLEM_ITEM_NO_GET),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_ITEM, SoundEvents.COPPER_GOLEM_ITEM_DROP),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_NO_ITEM, SoundEvents.COPPER_GOLEM_ITEM_NO_DROP)
        );
    }

    private static TransportItemsBetweenContainers.OnTargetReachedInteraction onReachedTargetInteraction(CopperGolemState state, @Nullable SoundEvent sound) {
        return (body, target, ticksSinceReachingTarget) -> {
            if (body instanceof AzuriteGolem azuriteGolem) {
                Container container = target.container();
                if (ticksSinceReachingTarget == 1) {
                    container.startOpen(azuriteGolem);
                    azuriteGolem.setOpenedChestPos(target.pos());
                    azuriteGolem.setState(state);
                }

                if (ticksSinceReachingTarget == 9 && sound != null) {
                    azuriteGolem.playSound(sound);
                }

                if (ticksSinceReachingTarget == 60) {
                    if (container.getEntitiesWithContainerOpen().contains(body)) {
                        container.stopOpen(azuriteGolem);
                    }

                    azuriteGolem.clearOpenedChestPos();
                }
            }
        };
    }

    private static Consumer<PathfinderMob> onTravelling() {
        return body -> {
            if (body instanceof AzuriteGolem azuriteGolem) {
                azuriteGolem.clearOpenedChestPos();
                azuriteGolem.setState(CopperGolemState.IDLE);
            }
        };
    }

    private static Predicate<TransportItemsBetweenContainers.TransportItemTarget> shouldQueueForTarget() {
        return transportTarget -> transportTarget.blockEntity() instanceof ChestBlockEntity chestBlockEntity && !chestBlockEntity.getEntitiesWithContainerOpen().isEmpty();
    }
}
