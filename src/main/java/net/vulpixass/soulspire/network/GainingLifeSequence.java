package net.vulpixass.soulspire.network;

import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GainingLifeSequence {
    private int tick = 0;
    private final ServerPlayerEntity target;
    double Vx;
    double Vy;
    double Vz;
    double angle = 0;
    double radius = 0;
    boolean playSound = false;
    int particleCount = 10;

    public GainingLifeSequence(ServerPlayerEntity target) {
        this.target = target;
        this.Vx = target.getX();
        this.Vy = target.getY();
        this.Vz = target.getZ();
    }
    public boolean tick() {
        ServerWorld world = target.getEntityWorld().toServerWorld();
        tick++;
        if(tick <= 60)  {
            for (int i = 0; i < particleCount; i++) {
                double ringAngle = 2 * Math.PI * i / particleCount;
                double x = target.getX() + radius * cos(ringAngle);
                double z = target.getZ() + radius * sin(ringAngle);
                double y = target.getY();
                angle += 0.3;
                radius += 0.03;
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), Vx + cos(angle) * radius, Vy,
                        Vz + sin(angle) * radius, 1, 0, 0, 0, 0.02);
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0.02);

                if (playSound) {
                    world.playSound(null, Vx, Vy, Vz, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 0.1f, 1.0f);
                    world.playSound(null, Vx, Vy, Vz, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.MASTER, 0.2f, 1.4f);
                } else {playSound = !playSound;}
            }
            return true;
        }
        return false;
    }
}
