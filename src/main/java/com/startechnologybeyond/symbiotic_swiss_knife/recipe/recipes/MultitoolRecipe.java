package com.startechnologybeyond.symbiotic_swiss_knife.recipe.recipes;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.startechnologybeyond.symbiotic_swiss_knife.item.SymbioticItems;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.SymbioticMultitoolItems;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MultitoolRecipe {

    public static final void init(Consumer<FinishedRecipe> provider) {
        multitoolRecipe(provider);
    }

    public static void powerUnitRecipe(Consumer<FinishedRecipe> provider, ItemStack ingredientStack, String name) {
            VanillaRecipeHelper.addShapedEnergyTransferRecipe(
                provider,
                true,
                false,
                true,
                "luv_power_unit" + name,

                Ingredient.of(ingredientStack.copy()),
                SymbioticItems.POWER_UNIT_LuV.asStack(),

                "X d",
                "PMP",
                "NUN",

                'S', GTToolType.SCREWDRIVER,
                'U', ingredientStack.copy(),
                'M', GTItems.ELECTRIC_MOTOR_LuV.asStack(),
                'N', new MaterialEntry(TagPrefix.plate, GTMaterials.RhodiumPlatedPalladium),
                'P', new MaterialEntry(TagPrefix.gearSmall, GTMaterials.RhodiumPlatedPalladium),
                'X', new MaterialEntry(TagPrefix.screw, GTMaterials.RhodiumPlatedPalladium));
    }

    public static void multitoolRecipe(Consumer<FinishedRecipe> provider) {
        powerUnitRecipe(provider, GTItems.BATTERY_LuV_VANADIUM.asStack(), "battery");
        powerUnitRecipe(provider, GTItems.ENERGY_LAPOTRONIC_ORB_CLUSTER.asStack(), "energy_cluster");

        VanillaRecipeHelper.addShapedEnergyTransferRecipe(
                provider,
                true,
                false,
                true,
                "multitool_empty",

                Ingredient.of(SymbioticItems.POWER_UNIT_LuV.asStack()),
                SymbioticMultitoolItems.MULTITOOL_EMPTY.asStack(),

                "wdx",
                "PUP",
                "NMN",

                'W', GTToolType.WRENCH,
                'S', GTToolType.SCREWDRIVER,
                'C', GTToolType.WIRE_CUTTER,
                'P', GTItems.ELECTRIC_PISTON_LuV.asStack(),
                'U', SymbioticItems.POWER_UNIT_LuV.asStack(),
                'M', GTItems.ELECTRIC_MOTOR_LuV.asStack(),
                'N', new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.NiobiumTitanium));
    }

}
