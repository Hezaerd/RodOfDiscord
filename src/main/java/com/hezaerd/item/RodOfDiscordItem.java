package com.hezaerd.item;

import com.hezaerd.registry.ModDataComponents;
import com.hezaerd.registry.ModEnchantmentEffects;
import com.hezaerd.registry.ModStatusEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RodOfDiscordItem extends Item {
    private static final int MAX_USE_TICKS = 20; // 1 second
    private static final int MAX_SNEAK_USE_TICKS = 40; // 2 seconds
    private static final int COOLDOWN_TICKS = 100; // 5 seconds
    private static final int MIN_TELEPORT_DISTANCE = 4; // 4 blocks
    private static final int MAX_TELEPORT_DISTANCE = 16; // 16 blocks
    private static final int MAX_SNEAK_TELEPORT_DISTANCE = 24; // 24 blocks when sneaking

    private boolean isUsing = false;
    private boolean isHarmony = false;
    private int currentUseTicks = 0;
    private boolean isSneakingUse = false;

    public RodOfDiscordItem(Settings settings) {
        super(settings
                .maxCount(1)
                .maxDamage(64)
                .repairable(Items.NETHER_STAR)
        );
    }

    @Override
    public Text getName(ItemStack stack) {
        if (isHarmony) {
            return Text.translatable("item.rodofdiscord.rod_of_discord.harmonized")
                    .formatted(Rarity.EPIC.getFormatting());
        } else {
            return Text.translatable("item.rodofdiscord.rod_of_discord")
                    .formatted(Rarity.UNCOMMON.getFormatting());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.isClient) return;

        try {
            RegistryEntry.Reference<Enchantment> harmonyEnchant = world.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOrThrow(ModEnchantmentEffects.HARMONY);
            this.isHarmony = EnchantmentHelper.getLevel(harmonyEnchant, stack) > 0;
        } catch (Exception e) {
            this.isHarmony = false;
        }

        stack.set(ModDataComponents.HARMONIZED_COMPONENT, this.isHarmony);
    }
    
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        // Show bar when charging or when damaged
        return stack.isDamaged() || isUsing;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        // If charging, show charge progress; otherwise show durability
        if (isUsing && currentUseTicks > 0) {
            int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;
            return MathHelper.clamp(Math.round(currentUseTicks * 13.0F / maxTicks), 0, 13);
        }

        // Default durability display when not charging
        return MathHelper.clamp(Math.round(13.0F - stack.getDamage() * 13.0F / stack.getMaxDamage()), 0, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        // If charging, show charging color; otherwise show durability color
        if (isUsing && currentUseTicks > 0) {
            int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;
            float progress = (float)currentUseTicks / maxTicks;
            // Red to green: hue from 0 to 1/3
            return MathHelper.hsvToRgb(progress / 3.0F, 1.0F, 1.0F);
        }

        // Default durability color when not charging
        int maxDamage = stack.getMaxDamage();
        float durability = Math.max(0.0F, ((float)maxDamage - stack.getDamage()) / maxDamage);
        return MathHelper.hsvToRgb(durability / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        if (user instanceof PlayerEntity player && player.isSneaking())
            return MAX_SNEAK_USE_TICKS;
        else
            return MAX_USE_TICKS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        this.isUsing = true;
        this.currentUseTicks = 0;
        this.isSneakingUse = user.isSneaking();
        return ActionResult.SUCCESS;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        // Update use progress for the charging bar
        int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;
        this.currentUseTicks = maxTicks - remainingUseTicks;

        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        if (world.isClient) return false;

        int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;
        int usedTicks = maxTicks - remainingUseTicks;

        this.isUsing = false;
        this.currentUseTicks = 0;

        tryTeleport(player, stack, world, usedTicks);
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) return stack;
        if (world.isClient) return stack;

        int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;

        this.isUsing = false;
        this.currentUseTicks = 0;

        tryTeleport(player, stack, world, maxTicks);
        return stack;
    }

    private void tryTeleport(PlayerEntity player, ItemStack stack, World world, int usedTicks) {
        // Determine max values based on sneaking state
        int maxTicks = isSneakingUse ? MAX_SNEAK_USE_TICKS : MAX_USE_TICKS;
        int maxDistance = isSneakingUse ? MAX_SNEAK_TELEPORT_DISTANCE : MAX_TELEPORT_DISTANCE;

        boolean fullUse = usedTicks >= maxTicks;
        float t = Math.min(1.0f, (float) usedTicks / maxTicks);
        double distance = MIN_TELEPORT_DISTANCE + t * (maxDistance - MIN_TELEPORT_DISTANCE);

        if (!fullUse && distance > maxDistance - 0.01) {
            distance = maxDistance - 0.01;
        }

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
        } else if (hit.getType() == HitResult.Type.MISS) {
            teleportTo = targetPos;
        }

        if (teleportTo == null) {
            player.sendMessage(Text.translatable("item.rodofdiscord.rod_of_discord.not_safe"), true);
            return;
        }

        player.requestTeleport(teleportTo.x, teleportTo.y + 1, teleportTo.z);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        spawnTeleportParticles(world, player.getPos());

        player.incrementStat(Stats.USED.getOrCreateStat(this));

        if (!player.isCreative()) {
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
            if (isHarmony) {
                player.getItemCooldownManager().set(stack, 7);
            } else {
                player.getItemCooldownManager().set(stack, 20); // prevent accidental spamming
                if (player.hasStatusEffect(ModStatusEffects.CHAOS)) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1, false, true, false));
                    player.removeStatusEffect(ModStatusEffects.CHAOS);
                }
                player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.CHAOS, COOLDOWN_TICKS));
            }
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
                    pos.getZ(),
                    world.random.nextGaussian(),
                    0.0,
                    world.random.nextGaussian()
            );
        }
    }
}