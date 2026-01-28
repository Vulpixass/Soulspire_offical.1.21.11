package net.vulpixass.soulspire;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.client.SoulHudRenderer;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.SoulDataC2SPayload;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;

public class SoulspireClient implements ClientModInitializer {
    public static int clientSoulCount = 0;
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.isOf(ModItems.SOUL_AMULET)){list.add(Text.translatable("tooltip.soulspirit.soul_amulet.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_TOKEN)){list.add(Text.translatable("tooltip.soulspirit.soul_token.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_SHARD)){list.add(Text.translatable("tooltip.soulspirit.soul_shard.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_TOTEM)){list.add(Text.translatable("tooltip.soulspirit.soul_totem.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_JAM)){list.add(Text.translatable("tooltip.soulspirit.soul_jam.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_CATALYST)){list.add(Text.translatable("tooltip.soulspirit.soul_catalyst.tooltip"));}
        });
        SoulHudRenderer.register();
        ClientPlayNetworking.registerGlobalReceiver(SoulDataS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                clientSoulCount = payload.souls();
            });
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SoulspireClient.sendRequest();
        });
    }
    public static void sendRequest() {
        ClientPlayNetworking.send(new SoulDataC2SPayload());
    }

}
