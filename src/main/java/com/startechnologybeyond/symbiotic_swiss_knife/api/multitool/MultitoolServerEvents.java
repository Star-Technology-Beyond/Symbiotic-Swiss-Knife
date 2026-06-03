package com.startechnologybeyond.symbiotic_swiss_knife.api.multitool;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolMode;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SymbioticSwissKnife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultitoolServerEvents {

    // consumed their break already this swing
    private static final Set<UUID> breakConsumed = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof MultitoolItem))
            return;
        if (!MultitoolMode.isSingleBlockMode(stack))
            return;

        // allow swing since it isnt locked
        if (!MultitoolItem.isBreakLocked(player.getUUID()))
            return;

        if (!breakConsumed.contains(player.getUUID())) {
            // first break this swing, consume the break for the rest
            breakConsumed.add(player.getUUID());
        } else {
            // cancel the break event for subsequent breaks
            event.setCanceled(true);
        }
    }

    public static void clearBreakConsumed(UUID uuid) {
        breakConsumed.remove(uuid);
    }
}