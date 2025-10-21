package net.variantgenerator.mod.config;

import net.variantgenerator.mod.texture.TextureColorizer.Pixel;

/**
 * Configuration for the Variant Generator Mod
 */
public class VariantGeneratorConfig {

    /**
     * Color configurations for each material tier
     */
    public static class TierColors {
        public Pixel brightColor;
        public Pixel darkColor;
        public String tierName;

        public TierColors(String tierName, Pixel brightColor, Pixel darkColor) {
            this.tierName = tierName;
            this.brightColor = brightColor;
            this.darkColor = darkColor;
        }
    }

    /**
     * Stat scaling configuration
     */
    public static class StatScaling {
        public float netheriteMultiplier = 1.25f;
        public float enderiteMultiplier = 1.5f;
        public float minDurabilityIncrease = 0.1f;
        public boolean enableArmorToughness = true;
        public boolean enableKnockbackResistance = false;
    }

    /**
     * Texture configuration
     */
    public static class TextureConfig {
        public int textureSize = 16;
        public boolean generateMipmaps = true;
        public String outputFormat = "PNG";
        public String referenceTextureIron = "textures/items/iron_ingot";
        public String referenceTextureNetherite = "textures/items/netherite_ingot";
        public String referenceTextureEnderite = "textures/items/enderite_ingot";
    }

    /**
     * Recipe configuration
     */
    public static class RecipeConfig {
        public boolean generateSmithingRecipes = true;
        public boolean generateCraftingRecipes = true;
        public String smithingTemplateId = "variantgenerator:variant_upgrade_smithing_template";
        public boolean requireTemplateForUpgrades = true;
    }

    /**
     * Scanning configuration
     */
    public static class ScanningConfig {
        public boolean enableMissingTextureGeneration = true;
        public String[] scanPatterns = {
                "iron_",
                "iron",
        };
        public String[] excludedMods = {
                "minecraft",
                "variantgenerator"
        };
        public boolean scanSubdirectories = true;
    }

    // Configuration instances
    public StatScaling statScaling = new StatScaling();
    public TextureConfig textureConfig = new TextureConfig();
    public RecipeConfig recipeConfig = new RecipeConfig();
    public ScanningConfig scanningConfig = new ScanningConfig();

    // Tier color configurations
    public static final TierColors IRON_COLORS = new TierColors(
            "Iron",
            new Pixel(255, 255, 255, 255),
            new Pixel(53, 53, 53, 255)
    );

    public static final TierColors NETHERITE_COLORS = new TierColors(
            "Netherite",
            new Pixel(100, 100, 120, 255),
            new Pixel(40, 40, 50, 255)
    );

    public static final TierColors ENDERITE_COLORS = new TierColors(
            "Enderite",
            new Pixel(29, 94, 83, 255),
            new Pixel(4, 14, 12, 255)
    );

    /**
     * Gets color configuration for a tier
     */
    public static TierColors getColorForTier(String tierName) {
        return switch (tierName.toLowerCase()) {
            case "iron" -> IRON_COLORS;
            case "netherite" -> NETHERITE_COLORS;
            case "enderite" -> ENDERITE_COLORS;
            default -> IRON_COLORS;
        };
    }

    /**
     * Validates the configuration
     */
    public boolean validate() {
        if (statScaling.netheriteMultiplier < 1.0f) {
            throw new IllegalArgumentException("Netherite multiplier must be >= 1.0");
        }
        if (statScaling.enderiteMultiplier < statScaling.netheriteMultiplier) {
            throw new IllegalArgumentException("Enderite multiplier must be >= Netherite multiplier");
        }
        if (textureConfig.textureSize <= 0) {
            throw new IllegalArgumentException("Texture size must be positive");
        }
        return true;
    }
}
