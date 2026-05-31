package com.startechnologybeyond.symbiotic_swiss_knife.network.packets;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolAutoSelectRules;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.SymbioticMultitoolItems;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CPacketMiddleClickAutoSelect implements IPacket {

    private static final int MAX_BLOCK_ID_LENGTH = 256;

    private int handOrdinal;
    private String blockId;

    public CPacketMiddleClickAutoSelect() {
    }

    public CPacketMiddleClickAutoSelect(InteractionHand hand, String blockId) {
        this.handOrdinal = hand.ordinal();
        this.blockId = blockId;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(handOrdinal);
        buf.writeUtf(blockId, MAX_BLOCK_ID_LENGTH);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        handOrdinal = buf.readVarInt();
        blockId = buf.readUtf(MAX_BLOCK_ID_LENGTH);
    }

    @Override
    public void execute(IHandlerContext handler) {
        Player player = handler.getPlayer();
        if (player == null)
            return;

        InteractionHand hand = InteractionHand.values()[handOrdinal];
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof MultitoolItem))
            return;

        // allow matching on full id and short id for reegex
        String shortId = blockId.contains(":") ? blockId.split(":", 2)[1] : blockId;

        // try full id first then short field
        MultitoolMode best = MultitoolAutoSelectRules.findBestMode(stack, blockId);
        if (best == null) {
            best = MultitoolAutoSelectRules.findBestMode(stack, shortId);
        }
        if (best == null) {
            handler.getPlayer().sendSystemMessage(Component.translatable("message.symbiotic_swiss_knife.auto_select.no_match"));
            return;
        }

        // only switch if it's actually a different mode
        MultitoolMode current = MultitoolMode.getActive(stack);
        if (best.equals(current))
            return;

        // create a new stack for that mode
        var nextItemEntry = SymbioticMultitoolItems.MULTITOOLS.get(best.toolType());
        if (nextItemEntry == null)
            return;

        // create new item and copy over nbt
        ItemStack newStack = new ItemStack(nextItemEntry.get());
        if (stack.hasTag()) {
            newStack.setTag(stack.getTag().copy());
        }

        // update the active one in the nbt
        MultitoolMode.setActive(newStack, best);

        // give to player
        player.setItemInHand(hand, newStack);

        // sync the updated item data back to the client so the client-side stack
        // has the correct behaviors tag and the actual updated item
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.sendAllDataToRemote();
        }
    }
}