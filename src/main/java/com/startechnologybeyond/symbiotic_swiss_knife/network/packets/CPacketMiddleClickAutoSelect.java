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

import java.util.HashSet;
import java.util.Set;

public class CPacketMiddleClickAutoSelect implements IPacket {

    private static final int MAX_BLOCK_ID_LENGTH = 256;
    private static final int MAX_TAG_COUNT = 128;
    private static final int MAX_TAG_LENGTH = 256;

    private int handOrdinal;
    private String blockId;
    private Set<String> blockTags;
    private boolean isDebug;

    public CPacketMiddleClickAutoSelect() {
    }

    public CPacketMiddleClickAutoSelect(InteractionHand hand, String blockId, Set<String> blockTags, boolean isDebug) {
        this.handOrdinal = hand.ordinal();
        this.blockId = blockId;
        this.blockTags = blockTags;
        this.isDebug = isDebug;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isDebug);
        buf.writeVarInt(handOrdinal);
        buf.writeUtf(blockId, MAX_BLOCK_ID_LENGTH);
        Set<String> tags = blockTags != null ? blockTags : Set.of();
        buf.writeVarInt(tags.size());
        for (String tag : tags) {
            buf.writeUtf(tag, MAX_TAG_LENGTH);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isDebug = buf.readBoolean();
        handOrdinal = buf.readVarInt();
        blockId = buf.readUtf(MAX_BLOCK_ID_LENGTH);
        int tagCount = Math.min(buf.readVarInt(), MAX_TAG_COUNT);
        blockTags = new HashSet<>(tagCount);
        for (int i = 0; i < tagCount; i++) {
            blockTags.add(buf.readUtf(MAX_TAG_LENGTH));
        }
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

        // allow matching on full id and short id for regex
        String shortId = blockId.contains(":") ? blockId.split(":", 2)[1] : blockId;
        Set<String> tags = blockTags != null ? blockTags : Set.of();

        // try full id + tags first, then fall back to short id + tags
        MultitoolMode best = MultitoolAutoSelectRules.findBestMode(stack, blockId, tags);
        if (best == null) {
            best = MultitoolAutoSelectRules.findBestMode(stack, shortId, tags);
        }

        if (best == null) {
            handler.getPlayer().sendSystemMessage(Component.translatable(
                    "message.symbiotic_swiss_knife.auto_select.no_match"));

            if (isDebug) {
                handler.getPlayer().sendSystemMessage(
                        Component.translatable("debug.symbiotic_swiss_knife.auto_select.short", shortId));
                handler.getPlayer().sendSystemMessage(
                        Component.translatable("debug.symbiotic_swiss_knife.auto_select.block", blockId));
                handler.getPlayer().sendSystemMessage(
                        Component.translatable("debug.symbiotic_swiss_knife.auto_select.tags", tags));
            }
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