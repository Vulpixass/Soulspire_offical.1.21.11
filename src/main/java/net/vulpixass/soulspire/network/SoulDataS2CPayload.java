package net.vulpixass.soulspire.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import static net.vulpixass.soulspire.Soulspire.MOD_ID;

// The Record: 'int souls' is the data we are carrying
public record SoulDataS2CPayload(int souls) implements CustomPayload {

    // 1. The ID: Tells Minecraft "This is a Soulspire Soul Data packet"
    public static final CustomPayload.Id<SoulDataS2CPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "soul_data"));

    // 2. The CODEC: Tells the game how to read and write the 'int souls' parameter
    public static final PacketCodec<RegistryByteBuf, SoulDataS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SoulDataS2CPayload::souls,
            SoulDataS2CPayload::new
    );

    // 3. The Interface Method: Required so the game can identify this payload type
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
