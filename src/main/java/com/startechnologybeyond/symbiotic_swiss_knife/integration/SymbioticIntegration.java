package com.startechnologybeyond.symbiotic_swiss_knife.integration;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class SymbioticIntegration {
    public static final boolean GUIDEME_LOADED = ModList.get().isLoaded("guideme");

    public static void integrationSetup(final FMLCommonSetupEvent event) {
        if (GUIDEME_LOADED) {
            // only load GuideME here which should prevent java from loading the class
            // elsewhere
            GuideMEIntegration.register();
            SymbioticSwissKnife.LOGGER.info("GuideME found, loading integration");
        }
    }
}
