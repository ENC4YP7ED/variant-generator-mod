package net.variantgenerator.mod.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Caches material statistics from Enderite mod
 * Used to extract and reuse Enderite's balanced stats for variant generation
 */
public class EnderiteStatCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-StatCache");

    /**
     * Tool material statistics
     */
    public static class ToolStats {
        public String name;
        public int durability;
        public float miningSpeed;
        public float attackDamage;
        public int enchantability;

        public ToolStats(String name, int durability, float miningSpeed, float attackDamage, int enchantability) {
            this.name = name;
            this.durability = durability;
            this.miningSpeed = miningSpeed;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
        }

        @Override
        public String toString() {
            return String.format("ToolStats{name=%s, durability=%d, speed=%.1f, damage=%.1f}",
                name, durability, miningSpeed, attackDamage);
        }
    }

    /**
     * Armor material statistics
     */
    public static class ArmorStats {
        public String name;
        public int durability;
        public Map<String, Integer> protectionValues;
        public int enchantability;
        public float toughness;
        public float knockbackResistance;

        public ArmorStats(String name, int durability, int enchantability, float toughness, float knockbackResistance) {
            this.name = name;
            this.durability = durability;
            this.protectionValues = new HashMap<>();
            this.enchantability = enchantability;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }

        public void setProtection(String type, int value) {
            protectionValues.put(type, value);
        }

        @Override
        public String toString() {
            return String.format("ArmorStats{name=%s, durability=%d, toughness=%.1f, knbk=%.1f}",
                name, durability, toughness, knockbackResistance);
        }
    }

    private static final Map<String, ToolStats> TOOL_CACHE = new HashMap<>();
    private static final Map<String, ArmorStats> ARMOR_CACHE = new HashMap<>();

    /**
     * Default Enderite tool stats (from reference)
     */
    public static final ToolStats ENDERITE_TOOL_STATS = new ToolStats(
        "enderite",
        4096,      // durability
        15.0f,     // mining speed
        2.0f,      // attack damage
        17         // enchantability
    );

    /**
     * Default Netherite tool stats
     */
    public static final ToolStats NETHERITE_TOOL_STATS = new ToolStats(
        "netherite",
        2031,      // durability
        12.0f,     // mining speed
        4.0f,      // attack damage
        15         // enchantability
    );

    /**
     * Default Iron tool stats
     */
    public static final ToolStats IRON_TOOL_STATS = new ToolStats(
        "iron",
        250,       // durability
        6.0f,      // mining speed
        6.0f,      // attack damage
        14         // enchantability
    );

    static {
        // Initialize with defaults
        TOOL_CACHE.put("enderite", ENDERITE_TOOL_STATS);
        TOOL_CACHE.put("netherite", NETHERITE_TOOL_STATS);
        TOOL_CACHE.put("iron", IRON_TOOL_STATS);

        // Armor stats
        ArmorStats enderiteArmor = new ArmorStats("enderite", 592, 17, 4.0f, 0.1f);
        enderiteArmor.setProtection("helmet", 4);
        enderiteArmor.setProtection("chestplate", 9);
        enderiteArmor.setProtection("leggings", 7);
        enderiteArmor.setProtection("boots", 4);
        ARMOR_CACHE.put("enderite", enderiteArmor);

        ArmorStats netheriteArmor = new ArmorStats("netherite", 592, 15, 3.0f, 0.1f);
        netheriteArmor.setProtection("helmet", 3);
        netheriteArmor.setProtection("chestplate", 8);
        netheriteArmor.setProtection("leggings", 6);
        netheriteArmor.setProtection("boots", 3);
        ARMOR_CACHE.put("netherite", netheriteArmor);

        ArmorStats ironArmor = new ArmorStats("iron", 240, 9, 0.0f, 0.0f);
        ironArmor.setProtection("helmet", 2);
        ironArmor.setProtection("chestplate", 6);
        ironArmor.setProtection("leggings", 5);
        ironArmor.setProtection("boots", 2);
        ARMOR_CACHE.put("iron", ironArmor);

        LOGGER.debug("Initialized Enderite stat cache with {} tool materials and {} armor materials",
            TOOL_CACHE.size(), ARMOR_CACHE.size());
    }

    /**
     * Caches tool material statistics
     */
    public static void cacheToolMaterial(String name, int durability, float miningSpeed, float attackDamage, int enchantability) {
        ToolStats stats = new ToolStats(name, durability, miningSpeed, attackDamage, enchantability);
        TOOL_CACHE.put(name, stats);
        LOGGER.debug("Cached tool material: {}", stats);
    }

    /**
     * Caches armor material statistics
     */
    public static void cacheArmorMaterial(String name, int durability, int enchantability, float toughness, float knockbackResistance) {
        ArmorStats stats = new ArmorStats(name, durability, enchantability, toughness, knockbackResistance);
        ARMOR_CACHE.put(name, stats);
        LOGGER.debug("Cached armor material: {}", stats);
    }

    /**
     * Gets tool stats for a material
     */
    public static ToolStats getToolStats(String material) {
        return TOOL_CACHE.getOrDefault(material.toLowerCase(), IRON_TOOL_STATS);
    }

    /**
     * Gets armor stats for a material
     */
    public static ArmorStats getArmorStats(String material) {
        return ARMOR_CACHE.getOrDefault(material.toLowerCase(), ARMOR_CACHE.get("iron"));
    }

    /**
     * Gets all cached tool materials
     */
    public static Collection<ToolStats> getAllToolStats() {
        return TOOL_CACHE.values();
    }

    /**
     * Gets all cached armor materials
     */
    public static Collection<ArmorStats> getAllArmorStats() {
        return ARMOR_CACHE.values();
    }

    /**
     * Clears the cache
     */
    public static void clearCache() {
        TOOL_CACHE.clear();
        ARMOR_CACHE.clear();
        LOGGER.debug("Cleared Enderite stat cache");
    }

    /**
     * Prints cache contents (debug)
     */
    public static void printCacheContents() {
        LOGGER.debug("=== Enderite Tool Stats Cache ===");
        TOOL_CACHE.forEach((key, stats) -> LOGGER.debug("  {}: {}", key, stats));
        LOGGER.debug("=== Enderite Armor Stats Cache ===");
        ARMOR_CACHE.forEach((key, stats) -> LOGGER.debug("  {}: {}", key, stats));
    }
}
