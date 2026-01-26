package net.vulpixass.soulspire.network;

public class PlayerSoulData {
    public int lives;
    public boolean hasCatalyst;
    public boolean hasUsedRevive;

    public PlayerSoulData(int lives) {
        this.lives = lives;
        this.hasCatalyst = false;
        this.hasUsedRevive = false;
    }
}
