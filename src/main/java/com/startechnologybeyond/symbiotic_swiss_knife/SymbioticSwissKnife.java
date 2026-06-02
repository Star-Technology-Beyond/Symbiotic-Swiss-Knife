package com.startechnologybeyond.symbiotic_swiss_knife;

import com.startechnologybeyond.symbiotic_swiss_knife.lang.LangHandler;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;

import com.startechnologybeyond.symbiotic_swiss_knife.item.SymbioticItems;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnologybeyond.symbiotic_swiss_knife.item.SymbioticItemModelDatagenProvider;
import com.startechnologybeyond.symbiotic_swiss_knife.api.SymbioticConfig;
import com.startechnologybeyond.symbiotic_swiss_knife.api.SymbioticCreativeTab;
import com.startechnologybeyond.symbiotic_swiss_knife.integration.SymbioticIntegration;
import com.startechnologybeyond.symbiotic_swiss_knife.network.SymbioticNetwork;
import com.startechnologybeyond.symbiotic_swiss_knife.recipe.RecipeSerialisers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(SymbioticSwissKnife.MOD_ID)
public class SymbioticSwissKnife {
    public static final String MOD_ID = "symbiotic_swiss_knife";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GTRegistrate SYMBIOTIC_REGISTRATE = GTRegistrate.create(SymbioticSwissKnife.MOD_ID);
    @SuppressWarnings("deprecation")
    public static final RandomSource RNG = RandomSource.createThreadSafe();

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(SymbioticSwissKnife.MOD_ID, path);
    }

    public SymbioticSwissKnife(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.CLIENT, SymbioticConfig.SPEC);
        SymbioticCreativeTab.init();
        SYMBIOTIC_REGISTRATE.creativeModeTab(() -> SymbioticCreativeTab.symbiotic_swiss_knife);
        SYMBIOTIC_REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
        SYMBIOTIC_REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, SymbioticItemModelDatagenProvider::init);

        modEventBus.addListener(this::commonSetup);
        SYMBIOTIC_REGISTRATE.registerRegistrate();

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);
        RecipeSerialisers.init(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SymbioticNetwork.init();
        SymbioticIntegration.integrationSetup(event);
    }
}
