package com.startechnologybeyond.symbiotic_swiss_knife.recipe;

import java.util.function.Consumer;

import com.startechnologybeyond.symbiotic_swiss_knife.recipe.recipes.*;

import net.minecraft.data.recipes.FinishedRecipe;

public class SymbioticRecipes {
    public static final void init(Consumer<FinishedRecipe> provider) {
        MultitoolRecipe.init(provider);
    }
}
