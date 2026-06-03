package com.startechnologybeyond.symbiotic_swiss_knife.network.packets;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import com.startechnologybeyond.symbiotic_swiss_knife.api.multitool.MultitoolServerEvents;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class CPacketReleaseSingleBlockLock implements IPacket {

    private int handOrdinal;

    public CPacketReleaseSingleBlockLock(InteractionHand hand) {
        this.handOrdinal = hand.ordinal();
    }

    public CPacketReleaseSingleBlockLock() {
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(handOrdinal);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        handOrdinal = buf.readVarInt();
    }

    @Override
    public void execute(IHandlerContext handler) {
        Player player = handler.getPlayer();
        if (player == null)
            return;

        // remove the uuid from swing lock to permit them to
        // continue breaking blocks again
        MultitoolItem.clearSwingLock(player.getUUID());
        MultitoolServerEvents.clearBreakConsumed(player.getUUID());
    }
}