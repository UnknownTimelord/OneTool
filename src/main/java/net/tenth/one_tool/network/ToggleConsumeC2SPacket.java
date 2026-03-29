package net.tenth.one_tool.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;

public record ToggleConsumeC2SPacket() implements CustomPayload {
    public static final Id<ToggleConsumeC2SPacket> ID =
            new Id<>(Identifier.of(OneTool.MOD_ID, "toggle_consume_c2s_packet"));
    public static final PacketCodec<RegistryByteBuf, ToggleConsumeC2SPacket> CODEC =
            PacketCodec.unit(new ToggleConsumeC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
