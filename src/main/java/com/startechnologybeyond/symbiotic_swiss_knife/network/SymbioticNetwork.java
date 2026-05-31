package com.startechnologybeyond.symbiotic_swiss_knife.network;

import com.lowdragmc.lowdraglib.networking.INetworking;
import com.lowdragmc.lowdraglib.networking.forge.LDLNetworkingImpl;
import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketSetMultitoolMode;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketUninstallMultitoolMode;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketMiddleClickAutoSelect;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketReleaseSingleBlockLock;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketSaveAutoSelectRules;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketToggleSingleBlockMode;

public class SymbioticNetwork {
    public static final INetworking NETWORK = LDLNetworkingImpl.createNetworking(
            SymbioticSwissKnife.resourceLocation("network"), "0.0.1");

    public static void init() {
        NETWORK.registerC2S(CPacketSetMultitoolMode.class);
        NETWORK.registerC2S(CPacketUninstallMultitoolMode.class);
        NETWORK.registerC2S(CPacketReleaseSingleBlockLock.class);
        NETWORK.registerC2S(CPacketToggleSingleBlockMode.class);
        NETWORK.registerC2S(CPacketSaveAutoSelectRules.class);
        NETWORK.registerC2S(CPacketMiddleClickAutoSelect.class);
    }
}
