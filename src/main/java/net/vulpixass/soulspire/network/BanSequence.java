package net.vulpixass.soulspire.network;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.item.ModItems;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class BanSequence {
    private boolean didLightning = false;
    private boolean playSound = false;
    private int tick = 0;
    private final ServerPlayerEntity victim;
    private final ServerPlayerEntity attacker;
    double[] angle = {0};
    double[] radius = {1.0};
    double Vx;
    double Vy;
    double Vz;


    public BanSequence(ServerPlayerEntity victim, ServerPlayerEntity attacker) {
        Vx = victim.getX();
        Vy = victim.getY();
        Vz = victim.getZ();
        Scoreboard scoreboard = victim.getEntityWorld().getServer().getScoreboard();
        Team team = scoreboard.getTeam("soul_glow");
        scoreboard.addScoreHolderToTeam(victim.getGameProfile().name(), team);
        victim.setGlowing(true);

        this.victim = victim;
        this.attacker = attacker;
    }

    public boolean tick() {
        if (attacker.getMainHandStack().isOf(ModItems.SOUL_AMULET)) {attacker.getMainHandStack().decrement(1);
        } else if (attacker.getOffHandStack().isOf(ModItems.SOUL_AMULET)) {attacker.getOffHandStack().decrement(1);}
        tick++;
        int[] editableTimer = {200 - tick};
        ServerWorld world = victim.getEntityWorld().toServerWorld();
        BannedPlayerList bannedPlayerList = world.getServer().getPlayerManager().getUserBanList();
        victim.setInvulnerable(true);
        victim.setPos(Vx, Vy, Vz);
        victim.setVelocity(0, 0, 0);
        int particleCount = 60;
        if (editableTimer[0] >= 170) {
            for (int i = 0; i < particleCount; i++) {
                double ringAngle = 2 * Math.PI * i / particleCount;
                double x = victim.getX() + radius[0] * cos(ringAngle);
                double z = victim.getZ() + radius[0] * sin(ringAngle);
                double y = victim.getY();
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), x, y, z, 1, 0, 0, 0, 0.02);
                if (playSound) {
                    world.playSound(null, Vx, Vy, Vz, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 0.1f, 1.0f);
                } else {
                    playSound = !playSound;
                }
            }
        }
        if(editableTimer[0] <= 169 && editableTimer[0] >= 110) {
            for (int i = 0; i < particleCount; i++) {
                angle[0] += 0.3;
                radius[0] += 0.03;
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), victim.getX() + cos(angle[0])* radius[0], victim.getY(),
                        victim.getZ() + sin(angle[0])* radius[0], 1, 0, 0, 0, 0.02);
                if(playSound) {
                    world.playSound(null, Vx, Vy, Vz, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.MASTER, 0.2f, 1.4f);
                } else {
                    playSound = !playSound;
                }
            }
        }
        if(editableTimer[0] <= 109 && editableTimer[0] >= 5) {
            for (int i = 0; i < particleCount; i++) {
                angle[0] += 0.4;
                radius[0] -= 0.03;
                world.spawnParticles(DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1.0f), victim.getX() + cos(angle[0])* radius[0], victim.getY(), victim.getZ() + sin(angle[0])* radius[0], 1, 0, 0, 0, 0.02);
                if (playSound) {
                    world.playSound(null, Vx, Vy, Vz, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.MASTER, 0.2f, 1.4f);
                } else {
                    playSound = !playSound;
                }
            }
            world.playSound(null, Vx, Vy, Vz, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.MASTER, 0.075f, 1, 1);
        }
        if(editableTimer[0] == 0) {
            if (!didLightning) {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                lightning.refreshPositionAndAngles(victim.getX(), victim.getY(), victim.getZ(), 0.0f, 0.0f);
                world.spawnEntity(lightning);
                didLightning = true;
            }
            world.playSound(null, Vx, Vy, Vz, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 2.0f, 1.75f);
            world.playSound(null, Vx, Vy, Vz, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 2.0f, 1.0f);
            world.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            victim.setInvulnerable(false);
            victim.setGlowing(false);
            Scoreboard scoreboard = world.getServer().getScoreboard();
            Team team = scoreboard.getTeam("soul_glow");
            if (team != null) {scoreboard.removeScoreHolderFromTeam(victim.getGameProfile().name(), team);}
            for (int i = 0; i < 150; i++) {
                TntEntity tntEntity = new TntEntity(EntityType.TNT, world);
                tntEntity.setPosition(Vx, Vy - i - 2, Vz);
                tntEntity.setFuse(i+1);
                world.spawnEntity(tntEntity);
            }
            bannedPlayerList.add(new BannedPlayerEntry(victim.getPlayerConfigEntry(), null, "Soul Amulet", null, "Your Soul got Overloaded"));
            victim.getEntityWorld().getServer().getPlayerManager().broadcast(Text.literal("§5§kkhj§5" + victim.getGameProfile().name() + "'s soul couldn't handle the Overload §kkhj"), false);
            victim.networkHandler.disconnect(Text.literal("§5§kkhj§5Your Soul got Overloaded§kkhj"));
            return true;
        }
        return false;
    }
}
