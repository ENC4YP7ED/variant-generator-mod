package net.variantgenerator.mod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

/**
 * NeoForge entry point for the Variant Generator Mod
 */
@Mod(VariantGeneratorMod.MOD_ID)
public class VariantGeneratorModNeoForge {

    public VariantGeneratorModNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Initialize the common mod
        VariantGeneratorMod.init();

        // Listen for load complete event
        modEventBus.addListener(this::onLoadComplete);
    }

    private void onLoadComplete(FMLLoadCompleteEvent event) {
        VariantGeneratorMod.LOGGER.info("Variant Generator Mod loaded successfully");
    }
}
