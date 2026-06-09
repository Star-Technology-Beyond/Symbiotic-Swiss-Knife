package com.startechnologybeyond.symbiotic_swiss_knife.item;

import static com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife.SYMBIOTIC_REGISTRATE;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.data.recipe.generated.ToolRecipeHandler;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.SymbioticMultitoolItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import net.minecraft.world.item.Item;

public class SymbioticItems {
    public static final Int2ReferenceMap<ItemEntry<? extends Item>> EXTENDED_POWER_UNITS = new Int2ReferenceArrayMap<>();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> POWER_UNIT_LuV = SYMBIOTIC_REGISTRATE
            .item("luv_power_unit", ComponentItem::create)
            .lang("LuV Power Unit")
            .properties(p -> p.stacksTo(8))
            .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("item/tools/luv_power_unit")))
            .onRegister(attach(ElectricStats.createElectricItem(102400000, GTValues.LuV)))
            .register();

    public static void init() {
        SymbioticMultitoolItems.init();

        // copy GT's existing power units
        EXTENDED_POWER_UNITS.putAll(ToolRecipeHandler.powerUnitItems);
        // add ours
        EXTENDED_POWER_UNITS.put(GTValues.LuV, POWER_UNIT_LuV);
    }
}
