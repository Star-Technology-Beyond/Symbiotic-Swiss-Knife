package com.startechnologybeyond.symbiotic_swiss_knife.api;

import static com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife.SYMBIOTIC_REGISTRATE;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import com.startechnologybeyond.symbiotic_swiss_knife.item.SymbioticItems;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class SymbioticCreativeTab {
    public static RegistryEntry<CreativeModeTab> symbiotic_swiss_knife = SYMBIOTIC_REGISTRATE
            .defaultCreativeTab(SymbioticSwissKnife.MOD_ID,
                    builder -> builder
                            .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(
                                    SymbioticSwissKnife.MOD_ID, SYMBIOTIC_REGISTRATE))
                            .title(Component.translatable("tab.symbiotic_swiss_knife.creative"))
                            .icon(SymbioticItems.POWER_UNIT_LuV::asStack)
                            .build())
            .register();

    public static void init() {

    }
}