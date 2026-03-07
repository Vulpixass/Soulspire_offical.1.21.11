package net.vulpixass.soulspire;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.vulpixass.soulspire.block.ModBlocks;
import net.vulpixass.soulspire.block.custom.SoulPowderDustBlock;
import net.vulpixass.soulspire.client.SoulHudRenderer;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.SoulDataC2SPayload;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;

public class SoulspireClient implements ClientModInitializer {
    public static int clientSoulCount = 0;
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.SOUL_POWDER, BlockRenderLayer.CUTOUT);
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.isOf(ModItems.SOUL_AMULET)){list.add(Text.translatable("tooltip.soulspirit.soul_amulet.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_FRAGMENT)){list.add(Text.translatable("tooltip.soulspirit.soul_fragment.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_SHARD)){list.add(Text.translatable("tooltip.soulspirit.soul_shard.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_TOTEM)){list.add(Text.translatable("tooltip.soulspirit.soul_totem.tooltip"));}
            if (itemStack.isOf(ModItems.SOUL_ELIXIR)){list.add(Text.translatable("tooltip.soulspirit.soul_elixir.tooltip"));}
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
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            int level = state.get(SoulPowderDustBlock.LEVEL);
            return getMagicColor(level);
        }, ModBlocks.SOUL_POWDER);


    }
    public static void sendRequest() {
        ClientPlayNetworking.send(new SoulDataC2SPayload());
    }
    private static int getMagicColor(int level) {
        float t = level / 7f;
        int r0 = 40;
        int g0 = 10;
        int b0 = 60;

        int r1 = 180;
        int g1 = 60;
        int b1 = 255;

        int r = (int)(r0 + (r1 - r0) * t);
        int g = (int)(g0 + (g1 - g0) * t);
        int b = (int)(b0 + (b1 - b0) * t);
        return ColorHelper.getArgb(255, r, g, b);
    }

}
