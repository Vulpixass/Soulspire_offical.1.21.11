package net.vulpixass.soulspire.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vulpixass.soulspire.block.ModBlocks;

public class ReviveSequence {

    private final Entity target;
    private final BlockPos pos;

    private int tick = 0;
    private double angle = 0;
    private double radius = 0.5;
    private double height = 0;

    private boolean lightningDone = false;

    private final double baseX;
    private final double baseY;
    private final double baseZ;

    public ReviveSequence(Entity target, BlockPos blockPos) {
        this.target = target;
        this.pos = blockPos;

        this.baseX = pos.getX();
        this.baseY = pos.getY() + 5;
        this.baseZ = pos.getZ();
    }

    public boolean tick() {
        int k = 0;
        target.setPos(baseX, baseY, baseZ);
        target.setVelocity(0, 0, 0);
        target.setInvulnerable(true);
        tick++;
        ServerWorld world = (ServerWorld) target.getEntityWorld();

        int particleCount = 60;
        if (tick <= 80) {
            angle += 0.25;
            radius += 0.01;
            height += 0.03;

            for (int i = 0; i < particleCount; i++) {
                double a = angle + (2 * Math.PI * i / particleCount);
                double x = baseX + Math.cos(a) * radius;
                double y = baseY + height;
                double z = baseZ + Math.sin(a) * radius;

                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0.02);
            }
            if (tick % 10 == 0) {
                world.playSound(null, baseX, baseY, baseZ, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 0.15f, 1.4f);
            }
            return false;
        }
        if (tick == 81 && !lightningDone) {
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.refreshPositionAndAngles(baseX, baseY, baseZ, 0, 0);
            world.spawnEntity(lightning);
            world.playSound(null, baseX, baseY, baseZ, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.MASTER, 1.0f, 1.0f);
            lightningDone = true;
            return false;
        }
        if (tick > 81) {
            angle += 0.35;
            radius -= 0.015;
            height -= 0.10;

            double spiralY = baseY + height;

            for (int i = 0; i < particleCount; i++) {
                double a = angle + (2 * Math.PI * i / particleCount);
                double x = baseX + Math.cos(a) * Math.max(radius, 0.1);
                double y = spiralY;
                double z = baseZ + Math.sin(a) * Math.max(radius, 0.1);
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0.02);
            }
            if (tick % 8 == 0) {world.playSound(null, baseX, baseY, baseZ, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.MASTER, 0.2f, 1.4f);}
            if (spiralY <= baseY) {
                world.spawnParticles(ParticleTypes.END_ROD, baseX, baseY + 1, baseZ, 40, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, baseX, baseY, baseZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0f, 1.5f);
                if(target instanceof ServerPlayerEntity playerTarget) {playerTarget.setHealth(playerTarget.getMaxHealth());}
                target.setInvulnerable(false);
                return true;
            }
        }

        return false;
    }

    private static final BlockPos[] getRunes = new BlockPos[]{
            new BlockPos(6, 1, 0), //north
            new BlockPos(-6, 1, 0), //south
            new BlockPos(0, 1, 6), //east
            new BlockPos(0, 1, -6) //west
    };
}
