package com.startechnologybeyond.symbiotic_swiss_knife;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.startechnologybeyond.symbiotic_swiss_knife.item.SymbioticItems;
import com.startechnologybeyond.symbiotic_swiss_knife.recipe.SymbioticRecipes;

import net.minecraft.data.recipes.FinishedRecipe;

@GTAddon
public class SymbioticSwissKnifeGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return SymbioticSwissKnife.SYMBIOTIC_REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        SymbioticItems.init();
    }

    @Override
    public String addonModId() {
        return SymbioticSwissKnife.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        SymbioticRecipes.init(provider);
    }

    // If you have custom ingredient types, uncomment this & change to match your
    // capability.
    // KubeJS WILL REMOVE YOUR RECIPES IF THESE ARE NOT REGISTERED.
    /*
     * public static final ContentJS<Double> PRESSURE_IN = new
     * ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE,
     * false);
     * public static final ContentJS<Double> PRESSURE_OUT = new
     * ContentJS<>(NumberComponent.ANY_DOUBLE, GregitasRecipeCapabilities.PRESSURE,
     * true);
     * 
     * @Override
     * public void registerRecipeKeys(KJSRecipeKeyEvent event) {
     * event.registerKey(CustomRecipeCapabilities.PRESSURE, Pair.of(PRESSURE_IN,
     * PRESSURE_OUT));
     * }
     */
}
