package com.startechnologybeyond.symbiotic_swiss_knife.item.multitool;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SymbioticSwissKnife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SymbioticMultitoolCleanupEvent {

    // we dont want to hold the lock and leak memory if a player leaves
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        MultitoolItem.clearSwingLock(event.getEntity().getUUID());
    }
}
