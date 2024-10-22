package dev.lemonnik.moss.entity.custom;

import dev.lemonnik.moss.entity.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

//? if <1.21 {
/*import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;*/
//?} else
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;

import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

import static net.minecraft.block.FlowerbedBlock.FLOWER_AMOUNT;

public class MoomossEntity extends AnimalEntity implements GeoEntity {
    private static final TrackedData<Boolean> SHEARED = DataTracker.registerData(MoomossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLOWERED = DataTracker.registerData(MoomossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BLOSSOMING = DataTracker.registerData(MoomossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockState> FLOWERSTATE = DataTracker.registerData(MoomossEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
    private static final TrackedData<Boolean> PINK = DataTracker.registerData(MoomossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int timeSinceLastGrowth = 0;
    private int growthInterval;
    private static final int MIN_GROWTH_INTERVAL = 3600; // 3min 3600
    private static final int MAX_GROWTH_INTERVAL = 6000; // 5min 6000
    private final Random random = new Random();

    public MoomossEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f, 3));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.MOOMOSS.create(world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<MoomossEntity> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.moomoss.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.moomoss.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        timeSinceLastGrowth++;

        if (growthInterval == 0) {
            growthInterval = MIN_GROWTH_INTERVAL + random.nextInt(MAX_GROWTH_INTERVAL - MIN_GROWTH_INTERVAL + 1);
        }

        if (timeSinceLastGrowth >= growthInterval) {
            setSheared(false);
            timeSinceLastGrowth = 0;
            if (this.isBlossoming()) {
                setPink(true);
            }
            growthInterval = MIN_GROWTH_INTERVAL + random.nextInt(MAX_GROWTH_INTERVAL - MIN_GROWTH_INTERVAL + 1);
        }

        BlockState blockState = Blocks.MOSS_CARPET.getDefaultState();

        for (int i = 0; i < 4; ++i) {
            int j = MathHelper.floor(this.getX() + (double) ((float) (i % 2 * 2 - 1) * 0.25F));
            int k = MathHelper.floor(this.getY());
            int l = MathHelper.floor(this.getZ() + (double) ((float) (i / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockPos = new BlockPos(j, k, l);
            BlockPos blockBelow = blockPos.down();

            if (this.getWorld().getBlockState(blockBelow).isFullCube(this.getWorld(), blockBelow) &&
                this.getWorld().getBlockState(blockPos).isAir()) {

                this.getWorld().setBlockState(blockPos, blockState);
                this.getWorld().emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this, blockState));
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient) {
            this.dropStack(new ItemStack(Items.GLOW_BERRIES, 1), 1);

            if (this.isFlowered()) {
                this.dropStack(new ItemStack(this.getFlowerState().getBlock(), 1), 1);
            }
        }
    }


    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.SHEARS)) {
            if (!this.getWorld().isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                this.emitGameEvent(GameEvent.SHEAR, player);
                //? if <1.21 {
                /*itemStack.damage(1, player, (playerx) -> {
                    playerx.sendToolBreakStatus(hand);
                });*/
                //?} else
                itemStack.damage(1, player, getSlotForHand(hand));
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else if (itemStack.isIn(ItemTags.FLOWERS)) {
            if (!this.getWorld().isClient && this.isFlowerable()) {
                BlockState flowerState = null;
                if (itemStack.isOf(Items.PINK_PETALS)) {
                    flowerState = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState().with(FLOWER_AMOUNT, 4);
                    setBlossoming(true);
                } else {
                    flowerState = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
                }
                this.flowered(SoundCategory.PLAYERS, flowerState);
                this.emitGameEvent(GameEvent.BLOCK_PLACE, player);
                itemStack.decrement(1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    public boolean isShearable() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    private void sheared(SoundCategory soundCategory) {
        this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, soundCategory, 1.0F, 1.0F);
        this.setSheared(true);
        this.setFlowered(false);
        this.dropStack(Items.MOSS_BLOCK.getDefaultStack(), 1);
        BlockState flowerState = this.getFlowerState();
        if (flowerState != null && !this.isBlossoming()) {
            this.dropStack(new ItemStack(flowerState.getBlock()), 1);
        }
        if (this.isBlossoming()) {
            this.dropStack(new ItemStack(Blocks.CHERRY_LEAVES, 1), 1);
        }
        setBlossoming(false);
        setPink(false);
        this.dataTracker.set(FLOWERSTATE, Blocks.AIR.getDefaultState());
        this.emitGameEvent(GameEvent.SHEAR);
    }

    public boolean isSheared() {
        return this.dataTracker.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.dataTracker.set(SHEARED, sheared);
    }

    public boolean isFlowerable() {
        return this.isAlive() && !this.isFlowered() && !this.isBaby();
    }

    private void flowered(SoundCategory soundCategory, BlockState flower) {
        this.getWorld().playSoundFromEntity(null, this, SoundEvents.BLOCK_MOSS_PLACE, soundCategory, 1.0F, 1.0F);
        this.setFlowered(true);
        this.dataTracker.set(FLOWERSTATE, flower);
    }

    public boolean isFlowered() {
        return this.dataTracker.get(FLOWERED);
    }

    public void setFlowered(boolean flowered) {
        this.dataTracker.set(FLOWERED, flowered);
    }

    //? if <1.21 {
    /*@Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHEARED, false);
        this.dataTracker.startTracking(FLOWERED, false);
        this.dataTracker.startTracking(FLOWERSTATE, Blocks.AIR.getDefaultState());
        this.dataTracker.startTracking(BLOSSOMING, false);
        this.dataTracker.startTracking(PINK, false);
    }*/
    //?} else
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SHEARED, false);
        builder.add(FLOWERED, false);
        builder.add(FLOWERSTATE, Blocks.AIR.getDefaultState());
        builder.add(BLOSSOMING, false);
        builder.add(PINK, false);
    }


    public BlockState getFlowerState() {
        return this.dataTracker.get(FLOWERSTATE);
    }

    public boolean isBlossoming() {
        return this.dataTracker.get(BLOSSOMING);
    }

    public void setBlossoming(boolean blossoming) {
        this.dataTracker.set(BLOSSOMING, blossoming);
    }

    public boolean isPink() {
        return this.dataTracker.get(PINK);
    }

    public void setPink(boolean pink) {
        this.dataTracker.set(PINK, pink);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("Sheared", this.isSheared());
        nbt.putBoolean("Flowered", this.isFlowered());
        nbt.putInt("FlowerState", Block.getRawIdFromState(this.getFlowerState()));
        nbt.putInt("LastGrowth", timeSinceLastGrowth);
        nbt.putBoolean("Blossoming", this.isBlossoming());
        nbt.putBoolean("Pink", this.isPink());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        this.setSheared(nbt.getBoolean("Sheared"));
        this.setFlowered(nbt.getBoolean("Flowered"));
        BlockState flowerState = Block.STATE_IDS.get(nbt.getInt("FlowerState"));
        if (flowerState != null) {this.dataTracker.set(FLOWERSTATE, flowerState);}
        timeSinceLastGrowth = nbt.getInt("LastGrowth");
        this.setBlossoming(nbt.getBoolean("Blossoming"));
        this.setPink(nbt.getBoolean("Pink"));
    }

    //? if >=1.21 {
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

}
