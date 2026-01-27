package net.vulpixass.soulspire.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static net.vulpixass.soulspire.Soulspire.MOD_ID;

public record SoulDataC2SPayload() implements CustomPayload {
    public static final CustomPayload.Id<SoulDataC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "soul_request"));
    public static final PacketCodec<RegistryByteBuf, SoulDataC2SPayload> CODEC = PacketCodec.unit(new SoulDataC2SPayload());
    @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
}
