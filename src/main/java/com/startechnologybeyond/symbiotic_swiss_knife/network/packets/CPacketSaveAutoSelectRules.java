package com.startechnologybeyond.symbiotic_swiss_knife.network.packets;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolAutoSelectRules;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CPacketSaveAutoSelectRules implements IPacket {

    private static final int MAX_RULES = 64;
    private static final int MAX_STR_LENGTH = 512;

    private int handOrdinal;
    private List<MultitoolAutoSelectRules.Rule> rules;

    public CPacketSaveAutoSelectRules() {
    }

    public CPacketSaveAutoSelectRules(InteractionHand hand,
            List<MultitoolAutoSelectRules.Rule> rules) {
        this.handOrdinal = hand.ordinal();
        this.rules = rules;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(handOrdinal);
        buf.writeVarInt(rules.size());

        // encode all the rules
        for (MultitoolAutoSelectRules.Rule rule : rules) {
            buf.writeUtf(rule.regex(), MAX_STR_LENGTH);
            buf.writeUtf(rule.toolTypeName(), 128);
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        handOrdinal = buf.readVarInt();
        int count = Math.min(buf.readVarInt(), MAX_RULES);

        // decode rules back
        rules = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String regex = buf.readUtf(MAX_STR_LENGTH);
            String toolType = buf.readUtf(128);
            rules.add(new MultitoolAutoSelectRules.Rule(regex, toolType));
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

        // drop any rules with an invalid regex
        List<MultitoolAutoSelectRules.Rule> clean = new ArrayList<>();
        for (MultitoolAutoSelectRules.Rule rule : rules) {
            if (rule.isValid())
                clean.add(rule);
        }

        // set the rules for this stack to be the new clean rules
        MultitoolAutoSelectRules.setRules(stack, clean);
    }
}