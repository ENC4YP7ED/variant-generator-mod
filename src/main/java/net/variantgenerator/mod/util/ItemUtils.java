package net.variantgenerator.mod.util;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for item operations
 */
public class ItemUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-ItemUtils");

    /**
     * Checks if an item name contains iron
     */
    public static boolean isIronVariant(String itemName) {
        String lower = itemName.toLowerCase();
        return lower.contains("iron") && !lower.contains("iron_ore") && !lower.contains("iron_block");
    }

    /**
     * Extracts the mod ID from an item registry name
     */
    public static String extractModId(String registryName) {
        if (registryName.contains(":")) {
            return registryName.split(":")[0];
        }
        return "minecraft";
    }

    /**
     * Extracts the item path from an item registry name
     */
    public static String extractItemPath(String registryName) {
        if (registryName.contains(":")) {
            return registryName.split(":")[1];
        }
        return registryName;
    }

    /**
     * Converts an item name to a variant name
     */
    public static String toVariantName(String itemName, String tier) {
        return itemName.replace("iron", tier.toLowerCase());
    }

    /**
     * Gets a human-readable name from an item name
     */
    public static String humanizeName(String itemName) {
        // Convert snake_case to Title Case
        StringBuilder result = new StringBuilder();
        String[] words = itemName.split("_");

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
                if (result.length() < itemName.replace("_", "").length() + words.length - 1) {
                    result.append(" ");
                }
            }
        }

        return result.toString();
    }

    /**
     * Checks if an item is a tool
     */
    public static boolean isTool(Item item) {
        return item instanceof ToolItem;
    }

    /**
     * Tries to get an item from the registry
     */
    public static Item getItem(String registryName) {
        try {
            Identifier id = Identifier.tryParse(registryName);
            if (id != null && Registries.ITEM.contains(id)) {
                return Registries.ITEM.get(id);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not find item: {}", registryName, e);
        }
        return null;
    }

    /**
     * Gets the registry name of an item
     */
    public static String getRegistryName(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        if (id != null) {
            return id.toString();
        }
        return "unknown";
    }

    /**
     * Normalizes an item name (converts to lowercase, removes spaces)
     */
    public static String normalizeName(String name) {
        return name.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
    }

    /**
     * Checks if a tier is valid
     */
    public static boolean isValidTier(String tier) {
        String lower = tier.toLowerCase();
        return lower.equals("iron") || lower.equals("netherite") || lower.equals("enderite");
    }

    /**
     * Extracts the base item name from a variant name
     */
    public static String extractBaseItemName(String variantName) {
        String lower = variantName.toLowerCase();
        if (lower.contains("enderite")) {
            return variantName.replace("enderite", "iron");
        } else if (lower.contains("netherite")) {
            return variantName.replace("netherite", "iron");
        }
        return variantName;
    }
}
