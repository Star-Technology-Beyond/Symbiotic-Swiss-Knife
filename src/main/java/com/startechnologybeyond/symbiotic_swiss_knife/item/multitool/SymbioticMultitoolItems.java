package com.startechnologybeyond.symbiotic_swiss_knife.item.multitool;

import static com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife.SYMBIOTIC_REGISTRATE;

import java.util.HashMap;
import java.util.Map;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SymbioticMultitoolItems {
    // all generated multitool variants (one for each type)
    public static final Map<GTToolType, ItemEntry<MultitoolItem>> MULTITOOLS = new HashMap<>();

    // empty variant
    public static final ItemEntry<MultitoolItem> MULTITOOL_EMPTY = SYMBIOTIC_REGISTRATE
            .item("multitool_empty",
                    properties -> new MultitoolItem(GTToolType.KNIFE,
                            GTMaterials.Neutronium.getToolTier(),
                            GTMaterials.Neutronium, properties, GTValues.LuV))
            .properties(properties -> properties.stacksTo(1))
            .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static void init() {
        SYMBIOTIC_REGISTRATE.addDataGenerator(ProviderType.LANG,
                prov -> prov.add("item.symbiotic_swiss_knife.gregtech_multitool", "Symbiotic Swiss Knife"));

        // generate multitool variant for the type with proper
        // tags for crafting purposes
        for (GTToolType type : GTToolType.getTypes().values()) {
            var builder = SYMBIOTIC_REGISTRATE.item("multitool_" + type.name,
                    properties -> new MultitoolItem(type,
                            GTMaterials.Neutronium.getToolTier(),
                            GTMaterials.Neutronium, properties, GTValues.LuV))
                    .properties(properties -> properties.stacksTo(1))
                    .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
                    .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                    .color(() -> IGTTool::tintColor);

            for (TagKey<Item> tag : type.itemTags) {
                builder = builder.tag(tag);
            }

            MULTITOOLS.put(type, builder.register());
        }
    }
}
