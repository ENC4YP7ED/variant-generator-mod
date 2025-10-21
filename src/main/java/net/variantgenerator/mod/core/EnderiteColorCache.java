package net.variantgenerator.mod.core;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.variantgenerator.mod.texture.TextureColorizer.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Caches color information extracted from Enderite mod items
 * Used by mixins to provide color data for variant generation
 */
public class EnderiteColorCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-ColorCache");

    /**
     * Cached Enderite colors
     */
    private static final Map<String, ColorInfo> COLOR_CACHE = new HashMap<>();

    /**
     * Default Enderite colors (from analysis)
     */
    public static final Pixel ENDERITE_BRIGHT = new Pixel(29, 94, 83, 255);
    public static final Pixel ENDERITE_DARK = new Pixel(4, 14, 12, 255);

    /**
     * Netherite colors (interpolated)
     */
    public static final Pixel NETHERITE_BRIGHT = new Pixel(100, 100, 120, 255);
    public static final Pixel NETHERITE_DARK = new Pixel(40, 40, 50, 255);

    /**
     * Iron colors (grayscale)
     */
    public static final Pixel IRON_BRIGHT = new Pixel(255, 255, 255, 255);
    public static final Pixel IRON_DARK = new Pixel(53, 53, 53, 255);

    /**
     * Color information container
     */
    public static class ColorInfo {
        public String itemName;
        public Pixel brightColor;
        public Pixel darkColor;
        public float brightness;

        public ColorInfo(String itemName, Pixel bright, Pixel dark) {
            this.itemName = itemName;
            this.brightColor = bright;
            this.darkColor = dark;
            this.brightness = (bright.r + bright.g + bright.b) / 3.0f;
        }
    }

    /**
     * Caches color information from an Enderite item
     */
    public static void cacheItemColors(Item item) {
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            if (itemId == null) return;

            String itemName = itemId.toString();

            if (itemName.contains("enderite_ingot")) {
                COLOR_CACHE.put("enderite", new ColorInfo("enderite", ENDERITE_BRIGHT, ENDERITE_DARK));
                LOGGER.debug("Cached Enderite ingot colors");
            } else if (itemName.contains("netherite_ingot")) {
                COLOR_CACHE.put("netherite", new ColorInfo("netherite", NETHERITE_BRIGHT, NETHERITE_DARK));
                LOGGER.debug("Cached Netherite ingot colors");
            } else if (itemName.contains("iron_ingot")) {
                COLOR_CACHE.put("iron", new ColorInfo("iron", IRON_BRIGHT, IRON_DARK));
                LOGGER.debug("Cached Iron ingot colors");
            }
        } catch (Exception e) {
            LOGGER.debug("Error caching item colors", e);
        }
    }

    /**
     * Gets cached color for a tier
     */
    public static ColorInfo getColorInfo(String tier) {
        return COLOR_CACHE.getOrDefault(tier.toLowerCase(), getDefaultColorInfo(tier));
    }

    /**
     * Gets default color info for a tier
     */
    private static ColorInfo getDefaultColorInfo(String tier) {
        return switch (tier.toLowerCase()) {
            case "enderite" -> new ColorInfo("enderite", ENDERITE_BRIGHT, ENDERITE_DARK);
            case "netherite" -> new ColorInfo("netherite", NETHERITE_BRIGHT, NETHERITE_DARK);
            case "iron" -> new ColorInfo("iron", IRON_BRIGHT, IRON_DARK);
            default -> new ColorInfo("unknown", IRON_BRIGHT, IRON_DARK);
        };
    }

    /**
     * Gets the bright color for a tier
     */
    public static Pixel getBrightColor(String tier) {
        return getColorInfo(tier).brightColor;
    }

    /**
     * Gets the dark color for a tier
     */
    public static Pixel getDarkColor(String tier) {
        return getColorInfo(tier).darkColor;
    }

    /**
     * Clears the cache
     */
    public static void clearCache() {
        COLOR_CACHE.clear();
        LOGGER.debug("Cleared Enderite color cache");
    }

    /**
     * Gets cache size
     */
    public static int getCacheSize() {
        return COLOR_CACHE.size();
    }

    /**
     * Prints cache contents (debug)
     */
    public static void printCacheContents() {
        LOGGER.debug("=== Enderite Color Cache ===");
        COLOR_CACHE.forEach((key, value) ->
            LOGGER.debug("  {}: bright={}, dark={}", key, value.brightColor, value.darkColor)
        );
    }
}
