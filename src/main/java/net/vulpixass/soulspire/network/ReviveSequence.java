package net.vulpixass.soulspire.network;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ReviveSequence {

    private final ServerPlayerEntity target;

    private int tick = 0;
    private double angle = 0;
    private double radius = 0.5;
    private double height = 0;

    private boolean lightningDone = false;

    private final double baseX;
    private final double baseY;
    private final double baseZ;

    public ReviveSequence(ServerPlayerEntity target) {
        this.target = target;

        this.baseX = target.getX();
        this.baseY = target.getY();
        this.baseZ = target.getZ();
    }

    public boolean tick() {
        target.setPos(baseX, baseY, baseZ);
        target.setVelocity(0, 0, 0);
        target.setInvulnerable(true);
        tick++;
        ServerWorld world = target.getEntityWorld().toServerWorld();

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
                target.setHealth(target.getMaxHealth());
                target.setInvulnerable(false);
                return true;
            }
        }

        return false;
    }
}
