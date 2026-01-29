package net.vulpixass.soulspire;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.vulpixass.soulspire.network.CombatTracker;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.command.LivesCommands;
import net.vulpixass.soulspire.config.LivesConfig;
import net.vulpixass.soulspire.item.ModItemGroups;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.ReviveInputHandler;
import net.vulpixass.soulspire.network.SoulDataC2SPayload;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class Soulspire implements ModInitializer {
	public static final String MOD_ID = "soulspire";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LivesCommands.register(dispatcher);
		});

		ModItems.registerModItems();
		ReviveInputHandler.register();
		ModItemGroups.registerItemGroups();
		CombatTracker.register();
		LivesStore.INSTANCE.register();

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			var source = oldPlayer.getRecentDamageSource();
			if (source != null && source.isOf(DamageTypes.OUT_OF_WORLD)) {ReviveInputHandler.voidDeaths.add(oldPlayer.getUuid());}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			UUID id = newPlayer.getUuid();
			if (ReviveInputHandler.voidDeaths.contains(id) && LivesStore.get().playerLives.get(oldPlayer.getUuid()).hasCatalyst != true) {
				ReviveInputHandler.voidDeaths.remove(id);
				newPlayer.getInventory().insertStack(new ItemStack(ModItems.SOUL_CATALYST));
				newPlayer.sendMessage(Text.literal("§5Your sacrifice has been acknowledged."), false);
				newPlayer.getEntityWorld().playSound(null, newPlayer.getX(), newPlayer.getY(), newPlayer.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.0f, 1.0f);
				LivesStore.get().sacrificeSoul(id);
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {LivesConfig.save(LivesStore.get().playerLives);});
		PayloadTypeRegistry.playC2S().register(SoulDataC2SPayload.ID, SoulDataC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SoulDataS2CPayload.ID, SoulDataS2CPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SoulDataC2SPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			int souls = LivesStore.get().outputLives(player.getUuid());
			context.responseSender().sendPacket(new SoulDataS2CPayload(souls));
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			LivesStore.get().updatePlayerDisplayName(player);
		});
	}
}