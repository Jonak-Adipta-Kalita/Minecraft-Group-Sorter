package jak.groupsorter.entity.azurite_golem;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AzuriteGolem extends AbstractGolem implements ContainerUser {
    private static final Brain.Provider<AzuriteGolem> BRAIN_PROVIDER = Brain.provider(
        List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY), _ -> AzuriteGolemAI.getActivities()
    );
    private static final EntityDataAccessor<CopperGolemState> AZURITE_GOLEM_STATE = SynchedEntityData.defineId(
        AzuriteGolem.class, EntityDataSerializers.COPPER_GOLEM_STATE
    );

    private @Nullable BlockPos openedChestPos;

    private int idleAnimationStartTick = 0;
    private final AnimationState idleAnimationState = new AnimationState();
    private final AnimationState interactionGetItemAnimationState = new AnimationState();
    private final AnimationState interactionGetNoItemAnimationState = new AnimationState();
    private final AnimationState interactionDropItemAnimationState = new AnimationState();
    private final AnimationState interactionDropNoItemAnimationState = new AnimationState();

    public AzuriteGolem(EntityType<? extends AbstractGolem> type, Level level) {
        super(type, level);
          this.getNavigation().setRequiredPathLength(48.0F);
        this.getNavigation().setCanOpenDoors(true);
        this.setPersistenceRequired();
        this.setState(CopperGolemState.IDLE);
        this.setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGING_IN_NEIGHBOR, 16.0F);
        this.setPathfindingMalus(PathType.FIRE, -1.0F);
        this.getBrain().setMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, this.getRandom().nextInt(60, 100));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.MAX_HEALTH, 12.0);
    }

    public CopperGolemState getState() {
        return this.entityData.get(AZURITE_GOLEM_STATE);
    }

    public void setState(CopperGolemState state) {
        this.entityData.set(AZURITE_GOLEM_STATE, state);
    }

    public void setOpenedChestPos(@Nullable BlockPos openedChestPos) {
        this.openedChestPos = openedChestPos;
    }

    public void clearOpenedChestPos() {
        this.openedChestPos = null;
    }

    public AnimationState getIdleAnimationState() {
        return this.idleAnimationState;
    }

    public AnimationState getInteractionGetItemAnimationState() {
        return this.interactionGetItemAnimationState;
    }

    public AnimationState getInteractionGetNoItemAnimationState() {
        return this.interactionGetNoItemAnimationState;
    }

    public AnimationState getInteractionDropItemAnimationState() {
        return this.interactionDropItemAnimationState;
    }

    public AnimationState getInteractionDropNoItemAnimationState() {
        return this.interactionDropNoItemAnimationState;
    }

    @Override
    protected @NonNull Brain<AzuriteGolem> makeBrain(Brain.@NonNull Packed packedBrain) {
        return BRAIN_PROVIDER.makeBrain(this, packedBrain);
    }

    @Override
    public @NonNull Brain<AzuriteGolem> getBrain() {
        return (Brain<AzuriteGolem>) super.getBrain();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(AZURITE_GOLEM_STATE, CopperGolemState.IDLE);
    }

    @Override
    protected void customServerAiStep(@NonNull ServerLevel level) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("azuriteGolemBrain");
        this.getBrain().tick(level, this);
        profiler.pop();
        profiler.push("azuriteGolemActivityUpdate");
        AzuriteGolemAI.updateActivity(this);
        profiler.pop();
        super.customServerAiStep(level);
    }


    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (!this.isNoAi()) {
                this.setupAnimationStates();
            }
        }
    }


    private void setupAnimationStates() {
        switch (this.getState()) {
            case IDLE:
                this.interactionGetNoItemAnimationState.stop();
                this.interactionGetItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                if (this.idleAnimationStartTick == this.tickCount) {
                    this.idleAnimationState.start(this.tickCount);
                } else if (this.idleAnimationStartTick == 0) {
                    this.idleAnimationStartTick = this.tickCount + this.random.nextInt(200, 240);
                }

                if (this.tickCount == this.idleAnimationStartTick + 10.0F) {
                    this.playHeadSpinSound();
                    this.idleAnimationStartTick = 0;
                }
                break;
            case GETTING_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.interactionGetItemAnimationState.startIfStopped(this.tickCount);
                break;
            case GETTING_NO_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.startIfStopped(this.tickCount);
                break;
            case DROPPING_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.startIfStopped(this.tickCount);
                break;
            case DROPPING_NO_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.startIfStopped(this.tickCount);
        }
    }

    public void spawn() {
        this.playSpawnSound();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NonNull ServerLevelAccessor level, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData
    ) {
        this.playSpawnSound();
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    public void playSpawnSound() {
        this.playSound(SoundEvents.COPPER_GOLEM_SPAWN);
    }

    private void playHeadSpinSound() {
        if (!this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSpinHeadSound(), this.getSoundSource(), 1.0F, 1.0F, false);
        }
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.COPPER_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COPPER_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(@NonNull BlockPos pos, @NonNull BlockState blockState) {
        this.playSound(SoundEvents.COPPER_GOLEM_STEP);
    }

    private SoundEvent getSpinHeadSound() {
        return SoundEvents.COPPER_GOLEM_SPIN;
    }

    @Override
    public @NonNull Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75F * this.getEyeHeight(), 0.0);
    }

    @Override
    public boolean hasContainerOpen(@NonNull ContainerOpenersCounter container, @NonNull BlockPos blockPos) {
        if (this.openedChestPos == null) {
            return false;
        }

        BlockState blockState = this.level().getBlockState(this.openedChestPos);
        return this.openedChestPos.equals(blockPos)
            || blockState.getBlock() instanceof ChestBlock
            && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE
            && ChestBlock.getConnectedBlockPos(this.openedChestPos, blockState).equals(blockPos);
    }

    @Override
    public double getContainerInteractionRange() {
        return 3.0;
    }

    @Override
    protected void dropEquipment(@NonNull ServerLevel level) {
        super.dropEquipment(level);
        this.dropPreservedEquipment(level);
    }

    @Override
    protected void actuallyHurt(@NonNull ServerLevel level, @NonNull DamageSource source, float dmg) {
        super.actuallyHurt(level, source, dmg);
        this.setState(CopperGolemState.IDLE);
    }
}
