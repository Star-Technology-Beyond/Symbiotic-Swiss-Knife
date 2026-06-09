package com.startechnologybeyond.symbiotic_swiss_knife.item;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class SymbioticItemModelDatagenProvider {
    // datagen all the multitool mode models
    // this saves us from manually doing each one
    // and automatically tells us if we're missing textures
    private static void registerMultitoolModeModels(ItemModelProvider provider) {
        for (GTToolType type : GTToolType.getTypes().values()) {
            provider.getBuilder("multitool_" + type.name)
                    .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                    .texture("layer0", "symbiotic_swiss_knife:item/tools/head_" + type.name)
                    .texture("layer1", "symbiotic_swiss_knife:item/tools/body_" + type.name);
        }

        // empty multitool model
        provider.getBuilder("multitool_empty")
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", "symbiotic_swiss_knife:item/tools/multitool_empty");
    }

    public static void init(ItemModelProvider provider) {
        registerMultitoolModeModels(provider);
    }
}
