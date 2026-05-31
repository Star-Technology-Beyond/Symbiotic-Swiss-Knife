package com.startechnologybeyond.symbiotic_swiss_knife.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("tab.symbiotic_swiss_knife.creative", "Symbiotic Swiss Knife");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.selected_mode", "Selected: %s");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.ejected_mode", "Ejected: %s");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.hint", "Hold the multitool selector key or scroll while holding to swap tool modes.");
        provider.add("key.categories.symbiotic_swiss_knife", "Symbiotic Swiss Knife");
        provider.add("key.symbiotic_swiss_knife.multitool_selector", "Open Multitool Selector");
        provider.add("key.symbiotic_swiss_knife.multitool_selector.hint", "Scroll or Mouse to select");
        provider.add("key.symbiotic_swiss_knife.multitool_selector.eject", "Click to eject tool");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.empty", "No Installed Tools");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.line", "§c§lGregTech Multitool");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l1", "§dTool Installation:");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l2", "§7Install tools by combining this with other full durability tools in a crafting table. §7Only §fone§7 tool of a type may be installed at any time.");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l3", "§7Tools can be ejected by holding shift and clicking in the multitool selector. Inserted tools do not keep their enchantments or charge!");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l5", "§aPower Unit:");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l6", "§7When using a tool, the knife consumes energy from its power unit unless the active tool is unbreakable. The power unit must be recharged if it goes down to §b0§7.");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l7", "§eSymbiosis");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l8", "§7Tools can be manually swapped between by scrolling with the knife held or using the selector menu that can be opened with the multitool selector key.");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.l9", "§7Middle clicking a block will attempt to select the best tool for the block's functions. If there are multiple options, the priority is the tool installation order with oldest first.");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.installed", "Installed Modes:");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.single_block_on", "§6Single Block: ON");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.single_block_off", "§7Single Block: OFF");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.auto_select_btn", "§7Auto Config");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.auto_select", "Auto Selecting Tool");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.regex_pattern_placeholder", "regex pattern..");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.auto_select_rules_header", "Customise Auto Select Rules");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.pattern_header", "PATTERN");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.tool_header", "TOOL");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.add_btn", "ADD");
        provider.add("item.symbiotic_swiss_knife.gregtech_multitool.save_btn", "SAVE");
        provider.add("block.symbiotic_swiss_knife.breaker_line", "§8-----------------------------------");
    }
}
