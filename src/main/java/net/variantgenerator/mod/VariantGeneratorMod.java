package net.variantgenerator.mod;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.variantgenerator.mod.core.VariantRegistry;
import net.variantgenerator.mod.texture.TextureColorizer;
import net.variantgenerator.mod.variant.VariantGenerator;
import net.variantgenerator.mod.recipe.RecipeScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Variant Generator Mod
 *
 * This mod automatically generates netherite and enderite variants for items
 * from other mods that have iron variants. It:
 * - Scans for iron_*.png textures
 * - Colorizes them using enderite/netherite color palettes
 * - Creates corresponding item variants with appropriate stats
 * - Generates crafting recipes for the new items
 */
public class VariantGeneratorMod {
    public static final String MOD_ID = "variantgenerator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM);

    // Variant Generator Core Components
    private static VariantRegistry variantRegistry;
    private static TextureColorizer textureColorizer;
    private static VariantGenerator variantGenerator;
    private static RecipeScanner recipeScanner;

    public static void init() {
        LOGGER.info("Initializing Variant Generator Mod");

        // Initialize core systems
        variantRegistry = new VariantRegistry();
        textureColorizer = new TextureColorizer();
        variantGenerator = new VariantGenerator(variantRegistry, textureColorizer);
        recipeScanner = new RecipeScanner();

        // Register lifecycle event to scan and generate variants after resource reload
        LifecycleEvent.SETUP.register(() -> {
            LOGGER.info("Starting variant generation process");
            try {
                // Scan for variants from other mods
                variantGenerator.scanAndGenerateVariants();
                LOGGER.info("Variant generation complete");
            } catch (Exception e) {
                LOGGER.error("Error during variant generation", e);
            }
        });

        LOGGER.info("Variant Generator Mod initialized successfully");
    }

    public static VariantRegistry getVariantRegistry() {
        return variantRegistry;
    }

    public static TextureColorizer getTextureColorizer() {
        return textureColorizer;
    }

    public static VariantGenerator getVariantGenerator() {
        return variantGenerator;
    }

    public static RecipeScanner getRecipeScanner() {
        return recipeScanner;
    }
}
