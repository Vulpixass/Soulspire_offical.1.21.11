package net.vulpixass.soulspire;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.item.ModItemGroups;
import net.vulpixass.soulspire.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Soulspire implements ModInitializer {
	public static final String MOD_ID = "soulspire";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();

		ModItemGroups.registerItemGroups();

		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
			if (itemStack.isOf(ModItems.SOUL_AMULET)){list.add(Text.translatable("tooltip.soulspirit.soul_amulet.tooltip"));}
			if (itemStack.isOf(ModItems.SOUL_TOKEN)){list.add(Text.translatable("tooltip.soulspirit.soul_token.tooltip"));}
			if (itemStack.isOf(ModItems.SOUL_SHARD)){list.add(Text.translatable("tooltip.soulspirit.soul_shard.tooltip"));}
			if (itemStack.isOf(ModItems.SOUL_TOTEM)){list.add(Text.translatable("tooltip.soulspirit.soul_totem.tooltip"));}
			if (itemStack.isOf(ModItems.SOUL_JAM)){list.add(Text.translatable("tooltip.soulspirit.soul_jam.tooltip"));}
			if (itemStack.isOf(ModItems.SOUL_CATALYST)){list.add(Text.translatable("tooltip.soulspirit.soul_catalyst.tooltip"));}

		});
	}
}