package net.vulpixass.soulspire.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines; // Ensure it's .render, not .gl
import net.minecraft.util.Identifier;
import net.vulpixass.soulspire.SoulspireClient;
import net.vulpixass.soulspire.network.LivesStore;

import java.util.UUID;

import static net.vulpixass.soulspire.Soulspire.MOD_ID;

public class SoulHudRenderer { // Removed "extends RenderPipelines"
    public static int currentLives = 0;
    private static final Identifier LIFE_ALIVE = Identifier.of(MOD_ID, "textures/gui/life_alive.png");
    private static final Identifier LIFE_DEAD = Identifier.of(MOD_ID, "textures/gui/life_dead.png");

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();
            int centerX = width / 2;

            // Position logic
            int x = centerX - 17;
            int y = height - 50;

            // Use a loop to draw exactly the number of lives the server sent
            // Example logic for your 3-heart triangle layout:
            int lives = SoulspireClient.clientSoulCount;
            if (!client.player.isCreative()) {
                if (lives == 0) {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x + 17, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x + 9, y - 8, 0f, 0f, 16, 16, 16, 16);
                }
                if (lives == 1) {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x + 17, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x + 9, y - 8, 0f, 0f, 16, 16, 16, 16);
                }
                if (lives == 2) {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x + 17, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_DEAD, x + 9, y - 8, 0f, 0f, 16, 16, 16, 16);
                }
                if (lives == 3) {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x + 9, y - 8, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x + 17, y, 0f, 0f, 16, 16, 16, 16);
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, LIFE_ALIVE, x, y, 0f, 0f, 16, 16, 16, 16);
                }
            }
        });
    }
}
