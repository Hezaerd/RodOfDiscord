package com.hezaerd.item;

import com.hezaerd.registry.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RodOfDiscord extends Item {
    private static final int MAX_USE_TICKS = 20; // .25 seconds
    
    private static final int COOLDOWN_TICKS = 100; // 5 seconds
    
    private static final int MIN_TELEPORT_DISTANCE = 4; // 4 blocks
    private static final int MAX_TELEPORT_DISTANCE = 16; // 16 blocks
    
    public RodOfDiscord(Settings settings) {
        super(settings
                .maxCount(1)
                .rarity(Rarity.UNCOMMON)
                .maxDamage(320)
        );
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) { 
        return MAX_USE_TICKS; 
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        if (world.isClient) return false;

        int usedTicks = MAX_USE_TICKS - remainingUseTicks;
        tryTeleport(player, stack, world, usedTicks);
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) return stack;
        if (world.isClient) return stack;

        // Full use duration
        tryTeleport(player, stack, world, MAX_USE_TICKS);
        return stack;
    }

    private void tryTeleport(PlayerEntity player, ItemStack stack, World world, int usedTicks) {
        boolean fullUse = usedTicks >= MAX_USE_TICKS;
        float t = Math.min(1.0f, (float) usedTicks / MAX_USE_TICKS);
        double distance = MIN_TELEPORT_DISTANCE + t * (MAX_TELEPORT_DISTANCE - MIN_TELEPORT_DISTANCE);

        if (!fullUse && distance > MAX_TELEPORT_DISTANCE - 0.01) {
            distance = MAX_TELEPORT_DISTANCE - 0.01;
        };

        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d targetPos = eyePos.add(lookVec.multiply(distance));

        BlockHitResult hit = world.raycast(new RaycastContext(
                eyePos,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        Vec3d teleportTo = null;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();
            var face = hit.getSide();

            BlockPos tpPos = null;
            if (face == Direction.UP) {
                BlockPos above = hitPos.up();
                if (isSafe(world, above)) 
                    tpPos = above;
            } else {
                BlockPos side = hitPos.offset(face);
                BlockPos below = side.down();
                if (isSafe(world, side) && world.getBlockState(below).isSolidBlock(world, below)) 
                    tpPos = side;
            }
            if (tpPos != null) 
                teleportTo = Vec3d.ofCenter(tpPos);
        } else if (hit.getType() == HitResult.Type.MISS) 
            teleportTo = targetPos;
        
        if (teleportTo == null) {;
            player.sendMessage(Text.translatable("item.rodofdiscord.rod_of_discord.not_safe"), true);
            return;
        }
        
        player.requestTeleport(teleportTo.x, teleportTo.y + 1, teleportTo.z); // <-- Commented out

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        spawnTeleportParticles(world, player.getPos());

        player.incrementStat(Stats.USED.getOrCreateStat(this));

            
            
        if (!player.isCreative()) {
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
            player.getItemCooldownManager().set(stack, 7); // prevent accidental spamming
            
            if (player.hasStatusEffect(ModStatusEffects.CHAOS)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1, false, true, false));
                player.removeStatusEffect(ModStatusEffects.CHAOS);
            }
            player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.CHAOS, COOLDOWN_TICKS));
        }
        
    }

    // Checks if the position and the one above are air, and the block below is solid
    private boolean isSafe(World world, BlockPos pos) {
        return world.getBlockState(pos).isAir()
                && world.getBlockState(pos.up()).isAir()
                && world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }
    
    private void spawnTeleportParticles(World world, Vec3d pos) {
        for (int i = 0; i < 32; i++) {
            world.addParticleClient(
                    ParticleTypes.PORTAL, 
                    pos.getX(),
                    pos.getY() + world.random.nextDouble() * 2.0,
                    pos.getZ(), world.random.nextGaussian(), 
                    0.0, 
                    world.random.nextGaussian()
            );
        }
    }
    
}
