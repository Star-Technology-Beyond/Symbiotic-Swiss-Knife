package com.startechnologybeyond.symbiotic_swiss_knife.integration;

import net.minecraft.resources.ResourceLocation;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;

import guideme.Guide;

public class GuideMEIntegration {

    public static void register() {
        Guide.builder(SymbioticSwissKnife.resourceLocation("guide")).build();
    }
}