package com.startechnologybeyond.symbiotic_swiss_knife.api;

import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SymbioticSwissKnife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SymbioticConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue DEBUG_NO_BLOCK_FOUND = BUILDER
            .comment("Should debug information about the short ID, block ID and tags be shown for no block found?")
            .define("isDebugNoBlockFound", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}