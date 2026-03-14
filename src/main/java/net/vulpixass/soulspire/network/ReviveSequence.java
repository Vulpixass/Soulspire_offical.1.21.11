package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.vulpixass.soulspire.block.ModBlocks;
import net.vulpixass.soulspire.block.custom.RitualAltarBlock;

import java.util.Collections;

public class ReviveSequence {

    private final ServerPlayerEntity target;
    private final ServerPlayerEntity sender;

    private final String typedName;

    private final BlockPos pos;

    private boolean playSound = false;

    private int tick = 0;
    private int timer = 0;

    private float playerYaw;
    private float playerPitch;

    private int burstStep = 0;
    private final int burstSteps = 20;

    private final double baseX;
    private final double baseY;
    private final double baseZ;

    public ReviveSequence(ServerPlayerEntity target, BlockPos blockPos, String typedName, ServerPlayerEntity sender) {
        this.target = target;
        this.sender = sender;

        this.typedName = typedName;

        this.pos = blockPos;

        this.baseX = pos.getX() + 0.5;
        this.baseY = pos.getY() + 3.5;
        this.baseZ = pos.getZ() + 0.5;
    }

    public boolean tick() {
        // Setup
        if (tick == 3) {
            Scoreboard scoreboard = target.getEntityWorld().getServer().getScoreboard();
            Team team = scoreboard.getTeam("soul_glow");
            scoreboard.addScoreHolderToTeam(target.getGameProfile().name(), team);
            target.setGlowing(true);
            target.setInvulnerable(true);
            target.setNoGravity(true);
        }
        ServerWorld world = target.getEntityWorld();

        BlockPos west  = pos.add(-7, 1, 0);
        BlockPos north = pos.add(0, 1, -7);
        BlockPos south = pos.add(0, 1, 7);
        BlockPos east  = pos.add(7, 1, 0);

        // Constant Updates
        timer++;
        tick++;
        target.setVelocity(0, 0, 0);
        playerPitch = target.getPitch();
        playerYaw = target.getYaw();
        target.fallDistance = 0;
        target.teleport(world, baseX, baseY, baseZ, Collections.emptySet(), playerYaw, playerPitch, false);

        if (timer == 12) {timer = 0;}

        // Sequence Start
        playSound = !playSound;

        if (timer == 0) {RitualAltarBlock.activateDust(world, pos);}

        if (tick >= 20 && tick < 20 + burstSteps) {
            burstStep = tick - 20;
            spawnBurstBeam(world, pos, north);
            spawnBurstBeam(world, pos, south);
            spawnBurstBeam(world, pos, east);
            spawnBurstBeam(world, pos, west);
        }
        if (tick >= 30 && tick <= 60) {
            world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f),north.getX() + 0.5,
                    north.getY() + 0.5, north.getZ() + 0.5, 1, 0, 0, 0, 0);
            world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f),south.getX() + 0.5,
                    south.getY() + 0.5, south.getZ() + 0.5, 1, 0, 0, 0, 0);
            world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f),east.getX() + 0.5,
                    east.getY() + 0.5, east.getZ() + 0.5, 1, 0, 0, 0, 0);
            world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f),west.getX() + 0.5,
                    west.getY() + 0.5, west.getZ() + 0.5, 1, 0, 0, 0, 0);
            if (!playSound) {
                world.playSound(null, north, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.MASTER);
                world.playSound(null, south, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.MASTER);
                world.playSound(null, east, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.MASTER);
                world.playSound(null, west, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.MASTER);
            }
        }
        if (tick >= 60 && tick <= 120) {
            spawnBeam(world, west, target.getBlockPos().up());
            spawnBeam(world, north, target.getBlockPos().up());
            spawnBeam(world, south, target.getBlockPos().up());
            spawnBeam(world, east, target.getBlockPos().up());
            if (playSound) {
                world.playSound(null, north, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER);
                world.playSound(null, south, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER);
                world.playSound(null, east, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER);
                world.playSound(null, west, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER);
            }
        }
        if (tick == 121) {
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, null);
            if (lightning != null) {
                lightning.refreshPositionAndAngles(baseX, baseY, baseZ, 0, 0);
                world.spawnEntity(lightning);
                world.playSound(null, baseX, baseY, baseZ, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                        SoundCategory.MASTER, 2.0f, 1.0f);
            }
            world.playSound(null, baseX, baseY, baseZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0f, 1.5f);
            LivesStore.get().revive(target.getUuid(), typedName, sender);
            LivesStore.get().updatePlayerDisplayName(target);

            target.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, target));
            int updated = LivesStore.get().outputLives(target.getUuid());
            ServerPlayNetworking.send(target, new SoulDataS2CPayload(updated));

            target.setGlowing(false);
            target.getEntityWorld().getServer().getScoreboard().removeScoreHolderFromTeam(target.getGameProfile().name(),
                    target.getEntityWorld().getServer().getScoreboard().getTeam("soul_glow"));

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 60, 1, false, false));
            target.setHealth(target.getMaxHealth());
            target.setInvulnerable(false);
            target.setNoGravity(false);
            world.setBlockState(west, ModBlocks.RUNE_WEST.getDefaultState());
            world.setBlockState(north, ModBlocks.RUNE_NORTH.getDefaultState());
            world.setBlockState(south, ModBlocks.RUNE_SOUTH.getDefaultState());
            world.setBlockState(east, ModBlocks.RUNE_EAST.getDefaultState());

            world.playSound(null, north, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER);
            world.playSound(null, south, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER);
            world.playSound(null, east, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER);
            world.playSound(null, west, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER);

            return true;
        }
        return false;
    }
    private void spawnBurstBeam(ServerWorld world, BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();

        double t = burstStep / (double) burstSteps;

        double x = from.getX() + dx * t + 0.5;
        double y = from.getY() + dy * t + 0.5;
        double z = from.getZ() + dz * t + 0.5;

        world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0);
        if (playSound) {
            world.playSound(null, x, y, z, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER);
        }
    }
    public static void spawnBeam(ServerWorld world, BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();

        int steps = 20;
        for (int i = 0; i < steps; i++) {
            double t = i / (double) steps;
            double x = from.getX() + dx * t + 0.5;
            double y = from.getY() + dy * t + 0.5;
            double z = from.getZ() + dz * t + 0.5;
            world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0);
        }
    }

}
