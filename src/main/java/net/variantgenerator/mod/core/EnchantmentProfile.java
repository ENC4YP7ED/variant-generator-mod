package net.variantgenerator.mod.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Tracks enchantments that work with Enderite items
 * Builds a profile of compatible enchantments for use in variant generation
 */
public class EnchantmentProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-EnchantProfile");

    /**
     * Maps item types to compatible enchantments
     */
    private static final Map<String, Set<String>> ITEM_ENCHANTMENTS = new HashMap<>();

    /**
     * Caches enchantment data
     */
    private static final Map<String, EnchantmentData> ENCHANTMENT_CACHE = new HashMap<>();

    /**
     * Enchantment metadata
     */
    public static class EnchantmentData {
        public String enchantmentId;
        public String displayName;
        public int maxLevel;
        public boolean treasure;
        public Set<String> compatibleItems;

        public EnchantmentData(String id, String name, int maxLevel, boolean treasure) {
            this.enchantmentId = id;
            this.displayName = name;
            this.maxLevel = maxLevel;
            this.treasure = treasure;
            this.compatibleItems = new HashSet<>();
        }
    }

    /**
     * Tracks an enchantment for an item
     */
    public static void trackEnchantment(Item item, Enchantment enchantment) {
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            Identifier enchantId = Registries.ENCHANTMENT.getId(enchantment);

            if (itemId != null && enchantId != null) {
                String itemKey = itemId.toString();
                String enchKey = enchantId.toString();

                ITEM_ENCHANTMENTS.computeIfAbsent(itemKey, k -> new HashSet<>())
                        .add(enchKey);

                LOGGER.debug("Tracked enchantment {} for item {}", enchKey, itemKey);
            }
        } catch (Exception e) {
            LOGGER.debug("Error tracking enchantment", e);
        }
    }

    /**
     * Gets enchantments compatible with an item
     */
    public static Set<String> getCompatibleEnchantments(String itemId) {
        return ITEM_ENCHANTMENTS.getOrDefault(itemId, new HashSet<>());
    }

    /**
     * Caches enchantment metadata
     */
    public static void cacheEnchantment(String id, String name, int maxLevel, boolean treasure) {
        ENCHANTMENT_CACHE.put(id, new EnchantmentData(id, name, maxLevel, treasure));
        LOGGER.debug("Cached enchantment: {}", id);
    }

    /**
     * Gets enchantment metadata
     */
    public static EnchantmentData getEnchantmentData(String id) {
        return ENCHANTMENT_CACHE.get(id);
    }

    /**
     * Gets all tracked enchantments
     */
    public static Collection<String> getAllTrackedEnchantments() {
        return ENCHANTMENT_CACHE.keySet();
    }

    /**
     * Clears all tracked data
     */
    public static void clearProfile() {
        ITEM_ENCHANTMENTS.clear();
        ENCHANTMENT_CACHE.clear();
        LOGGER.debug("Cleared enchantment profile");
    }

    /**
     * Gets profile size
     */
    public static int getProfileSize() {
        return ITEM_ENCHANTMENTS.size();
    }

    /**
     * Prints profile contents (debug)
     */
    public static void printProfile() {
        LOGGER.debug("=== Enchantment Profile ===");
        ITEM_ENCHANTMENTS.forEach((item, enchantments) ->
            LOGGER.debug("  {}: {} enchantments", item, enchantments.size())
        );
    }
}
