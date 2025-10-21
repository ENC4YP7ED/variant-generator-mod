package net.variantgenerator.mod.variant;

import net.variantgenerator.mod.core.VariantRegistry;
import net.variantgenerator.mod.core.VariantRegistry.ItemVariantTier;
import net.variantgenerator.mod.core.VariantRegistry.VariantConfig;
import net.variantgenerator.mod.texture.TextureColorizer;
import net.variantgenerator.mod.texture.TextureColorizer.GrayscaleAnalysis;
import net.variantgenerator.mod.texture.TextureColorizer.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Main variant generator that scans for items and creates variants
 */
public class VariantGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Core");

    private final VariantRegistry registry;
    private final TextureColorizer colorizer;
    private final Map<ItemVariantTier, BufferedImage> referenceTextures = new HashMap<>();

    public VariantGenerator(VariantRegistry registry, TextureColorizer colorizer) {
        this.registry = registry;
        this.colorizer = colorizer;
    }

    /**
     * Main entry point for scanning and generating variants
     */
    public void scanAndGenerateVariants() throws Exception {
        LOGGER.info("Starting variant generation process");

        // Load reference textures (iron, netherite, enderite)
        loadReferenceTextures();

        // Scan for items that can be converted
        Set<String> itemsToProcess = scanForIronVariants();

        if (itemsToProcess.isEmpty()) {
            LOGGER.warn("No iron variants found to process");
            return;
        }

        LOGGER.info("Found {} items to generate variants for", itemsToProcess.size());

        // Generate variants for each found item
        for (String itemId : itemsToProcess) {
            try {
                generateVariantsForItem(itemId);
            } catch (Exception e) {
                LOGGER.error("Error generating variants for item: {}", itemId, e);
            }
        }

        LOGGER.info("Variant generation complete. Total variants: {}", registry.size());
    }

    /**
     * Loads reference textures for colorization
     */
    private void loadReferenceTextures() throws IOException {
        // These would typically come from the Enderite mod
        LOGGER.info("Loading reference textures for variant tiers");

        // In a real implementation, these would be loaded from the config
        // For now, we'll document the expected colors

        // Example reference analysis:
        // Iron: Grayscale from 53-255
        // Netherite: Dark purplish (approx 50, 50, 70)
        // Enderite: Dark cyan (approx 4, 14, 12)

        LOGGER.debug("Reference textures configuration loaded");
    }

    /**
     * Scans for iron variants in the asset directories
     */
    private Set<String> scanForIronVariants() {
        Set<String> items = new HashSet<>();
        LOGGER.info("Scanning for iron variants...");

        // Scan asset directories for items with "iron" in the name
        try {
            Path assetsPath = Paths.get("assets");
            if (Files.exists(assetsPath)) {
                try (Stream<Path> paths = Files.walk(assetsPath)) {
                    paths.filter(path -> path.getFileName().toString().endsWith(".png"))
                            .filter(path -> path.toString().toLowerCase().contains("iron"))
                            .forEach(path -> {
                                String relativePath = assetsPath.relativize(path).toString();
                                items.add(relativePath);
                                LOGGER.debug("Found iron variant: {}", relativePath);
                            });
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not scan asset directories", e);
        }

        return items;
    }

    /**
     * Generates netherite and enderite variants for a specific item
     */
    private void generateVariantsForItem(String itemPath) throws IOException {
        LOGGER.info("Generating variants for: {}", itemPath);

        // Extract mod ID and item name from path
        String[] parts = itemPath.split("[/\\\\]");
        String modId = extractModId(itemPath);
        String itemName = extractItemName(itemPath);

        // Load source iron texture
        File sourceFile = new File(itemPath);
        if (!sourceFile.exists()) {
            LOGGER.warn("Source file not found: {}", itemPath);
            return;
        }

        BufferedImage ironTexture = colorizer.loadImage(sourceFile);

        // Generate netherite variant
        generateVariantTexture(modId, itemName, ironTexture, ItemVariantTier.NETHERITE, sourceFile);

        // Generate enderite variant
        generateVariantTexture(modId, itemName, ironTexture, ItemVariantTier.ENDERITE, sourceFile);

        LOGGER.info("Completed variant generation for: {}", itemName);
    }

    /**
     * Generates a single variant texture
     */
    private void generateVariantTexture(String modId, String itemName, BufferedImage sourceTexture,
                                       ItemVariantTier tier, File sourceFile) throws IOException {
        LOGGER.debug("Generating {} variant for {}", tier.displayName, itemName);

        // Create reference color palette for the tier
        GrayscaleAnalysis tierColorAnalysis = getReferenceColorForTier(tier);

        // Recolor the texture
        BufferedImage recoloredTexture = colorizer.recolorImage(sourceTexture,
                tierColorAnalysis.brightestPixel,
                tierColorAnalysis.darkestPixel);

        // Save the recolored texture
        String outputName = sourceFile.getName()
                .replace("iron", tier.displayName.toLowerCase());
        File outputFile = new File(sourceFile.getParent(), outputName);

        colorizer.saveImage(recoloredTexture, outputFile);
        LOGGER.debug("Saved {} texture to: {}", tier.displayName, outputFile.getAbsolutePath());

        // Register the variant
        VariantConfig config = new VariantConfig(modId, itemName, tier);
        config.textureLocation = outputFile.getAbsolutePath();
        registry.registerVariant(config);
    }

    /**
     * Gets reference colors for a specific tier
     * These should match the Enderite mod's color schemes
     */
    private GrayscaleAnalysis getReferenceColorForTier(ItemVariantTier tier) {
        switch (tier) {
            case NETHERITE:
                // Netherite dark gray color
                Pixel netheriteLight = new Pixel(100, 100, 120, 255);
                Pixel netheriteDark = new Pixel(40, 40, 50, 255);
                return new GrayscaleAnalysis(255, 0, netheriteLight, netheriteDark, 255, 0);

            case ENDERITE:
                // Enderite cyan color (from enderite_ingot.png analysis)
                Pixel enderiteLight = new Pixel(29, 94, 83, 255);
                Pixel enderiteDark = new Pixel(4, 14, 12, 255);
                return new GrayscaleAnalysis(255, 0, enderiteLight, enderiteDark, 255, 0);

            case IRON:
            default:
                // Iron grayscale (no change)
                Pixel ironLight = new Pixel(255, 255, 255, 255);
                Pixel ironDark = new Pixel(53, 53, 53, 255);
                return new GrayscaleAnalysis(255, 0, ironLight, ironDark, 255, 0);
        }
    }

    /**
     * Extracts mod ID from a file path
     */
    private String extractModId(String filePath) {
        String[] parts = filePath.split("[/\\\\]");
        // Typically the format is assets/modid/...
        if (parts.length >= 2) {
            return parts[1];
        }
        return "unknown";
    }

    /**
     * Extracts item name from a file path
     */
    private String extractItemName(String filePath) {
        File file = new File(filePath);
        String name = file.getName();
        // Remove file extension
        return name.substring(0, name.lastIndexOf('.'));
    }

    /**
     * Gets the registry
     */
    public VariantRegistry getRegistry() {
        return registry;
    }

    /**
     * Gets the colorizer
     */
    public TextureColorizer getColorizer() {
        return colorizer;
    }
}
