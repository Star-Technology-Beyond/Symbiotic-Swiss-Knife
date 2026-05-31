package com.startechnologybeyond.symbiotic_swiss_knife.recipe;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import com.startechnologybeyond.symbiotic_swiss_knife.recipe.recipes.MultitoolInstallModeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerialisers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, SymbioticSwissKnife.MOD_ID);

    public static final RegistryObject<RecipeSerializer<MultitoolInstallModeRecipe>> MULTITOOL_INSTALL_MODE = SERIALIZERS
            .register("multitool_install_mode", () -> MultitoolInstallModeRecipe.SERIALIZER);

    public static void init(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }
}