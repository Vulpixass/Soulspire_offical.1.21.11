package net.vulpixass.soulspire.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static net.vulpixass.soulspire.Soulspire.MOD_ID;

public record SoulDataS2CPayload(int souls) implements CustomPayload {

    public static final CustomPayload.Id<SoulDataS2CPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "soul_data"));
    public static final PacketCodec<RegistryByteBuf, SoulDataS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SoulDataS2CPayload::souls,
            SoulDataS2CPayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
