package net.tenth.one_tool.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.tenth.one_tool.OneTool;

public record OpenOneToolInventoryC2SPacket() implements CustomPayload {
    public static final Id<OpenOneToolInventoryC2SPacket> ID =
            new Id<>(Identifier.of(OneTool.MOD_ID, "open_one_tool_inventory_c2s_packet"));
    public static final PacketCodec<RegistryByteBuf, OpenOneToolInventoryC2SPacket> CODEC =
            PacketCodec.unit(new OpenOneToolInventoryC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
