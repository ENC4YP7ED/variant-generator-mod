package net.variantgenerator.mod.core;

import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;
import net.variantgenerator.mod.core.EnderiteStatCache.ToolStats;
import net.variantgenerator.mod.core.EnderiteStatCache.ArmorStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Registry for variant items and their configurations
 * Tracks generated variants and their properties
 */
public class VariantRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Registry");

    /**
     * Represents a variant item configuration
     */
    public static class VariantConfig {
        public String sourceModId;
        public String baseItemName;
        public String sourceItemId;
        public ItemVariantTier tier;
        public String textureLocation;
        public ItemStats baseStats;
        public ItemStats variantStats;

        public VariantConfig(String sourceModId, String baseItemName, ItemVariantTier tier) {
            this.sourceModId = sourceModId;
            this.baseItemName = baseItemName;
            this.tier = tier;
            this.baseStats = new ItemStats();
            this.variantStats = new ItemStats();
        }

        @Override
        public String toString() {
            return String.format("VariantConfig{mod=%s, item=%s, tier=%s}", sourceModId, baseItemName, tier);
        }
    }

    /**
     * Item statistics for tools and armor
     */
    public static class ItemStats {
        public float miningSpeed = 0;
        public float attackDamage = 0;
        public float attackSpeed = -2.4f;
        public int durability = 0;
        public int armor = 0;
        public float toughness = 0;
        public float knockbackResistance = 0;
        public int enchantability = 0;

        public ItemStats copy() {
            ItemStats copy = new ItemStats();
            copy.miningSpeed = this.miningSpeed;
            copy.attackDamage = this.attackDamage;
            copy.attackSpeed = this.attackSpeed;
            copy.durability = this.durability;
            copy.armor = this.armor;
            copy.toughness = this.toughness;
            copy.knockbackResistance = this.knockbackResistance;
            copy.enchantability = this.enchantability;
            return copy;
        }
    }

    /**
     * Variant tiers
     */
    public enum ItemVariantTier {
        IRON(1.0f, "Iron"),
        NETHERITE(1.25f, "Netherite"),
        ENDERITE(1.5f, "Enderite");

        public final float multiplier;
        public final String displayName;

        ItemVariantTier(float multiplier, String displayName) {
            this.multiplier = multiplier;
            this.displayName = displayName;
        }
    }

    private final Map<String, VariantConfig> registeredVariants = new HashMap<>();
    private final Map<String, List<VariantConfig>> variantsByMod = new HashMap<>();
    private final Map<String, List<VariantConfig>> variantsByTier = new HashMap<>();

    /**
     * Registers a variant configuration
     */
    public void registerVariant(VariantConfig config) {
        String key = config.sourceModId + ":" + config.baseItemName + ":" + config.tier.name();
        registeredVariants.put(key, config);

        variantsByMod.computeIfAbsent(config.sourceModId, k -> new ArrayList<>()).add(config);
        variantsByTier.computeIfAbsent(config.tier.name(), k -> new ArrayList<>()).add(config);

        LOGGER.info("Registered variant: {}", config);
    }

    /**
     * Gets a registered variant configuration
     */
    public VariantConfig getVariant(String modId, String itemName, ItemVariantTier tier) {
        String key = modId + ":" + itemName + ":" + tier.name();
        return registeredVariants.get(key);
    }

    /**
     * Gets all variants for a specific mod
     */
    public List<VariantConfig> getVariantsForMod(String modId) {
        return variantsByMod.getOrDefault(modId, new ArrayList<>());
    }

    /**
     * Gets all variants of a specific tier
     */
    public List<VariantConfig> getVariantsForTier(ItemVariantTier tier) {
        return variantsByTier.getOrDefault(tier.name(), new ArrayList<>());
    }

    /**
     * Gets all registered variants
     */
    public Collection<VariantConfig> getAllVariants() {
        return registeredVariants.values();
    }

    /**
     * Calculates scaled stats for a variant tier
     */
    public static ItemStats scaleStats(ItemStats baseStats, ItemVariantTier targetTier) {
        ItemStats scaled = baseStats.copy();

        float multiplier = targetTier.multiplier;

        // Scale tool properties
        if (scaled.miningSpeed > 0) {
            scaled.miningSpeed *= multiplier;
        }
        if (scaled.attackDamage > 0) {
            scaled.attackDamage *= multiplier;
        }
        if (scaled.durability > 0) {
            scaled.durability = (int) (scaled.durability * multiplier);
        }

        // Scale armor properties
        if (scaled.armor > 0) {
            scaled.armor = (int) Math.ceil(scaled.armor * multiplier);
        }
        if (scaled.toughness > 0) {
            scaled.toughness *= multiplier;
        }

        // Enchantability scales less dramatically
        if (scaled.enchantability > 0) {
            scaled.enchantability = Math.max(scaled.enchantability, (int) (scaled.enchantability * 1.1f * multiplier));
        }

        return scaled;
    }

    /**
     * Gets the size of the registry
     */
    public int size() {
        return registeredVariants.size();
    }

    /**
     * Scales stats using Enderite mod cached statistics
     * Provides access to actual Enderite material stats for comparison
     */
    public static ItemStats scaleStatsUsingEnderite(ItemStats baseStats, ItemVariantTier targetTier) {
        ItemStats scaled = baseStats.copy();

        // Get Enderite stats for reference
        ToolStats enderiteStats = EnderiteStatCache.getToolStats("enderite");
        ToolStats baseToolStats = EnderiteStatCache.getToolStats("iron");

        if (enderiteStats != null && baseToolStats != null) {
            // Use actual Enderite multipliers
            float multiplier = (float) enderiteStats.durability / baseToolStats.durability;

            LOGGER.debug("Using Enderite stat multiplier: {}", multiplier);

            // Apply scaling
            if (scaled.miningSpeed > 0) {
                scaled.miningSpeed *= multiplier;
            }
            if (scaled.attackDamage > 0) {
                scaled.attackDamage *= multiplier;
            }
            if (scaled.durability > 0) {
                scaled.durability = (int) (scaled.durability * multiplier);
            }
            if (scaled.armor > 0) {
                scaled.armor = (int) Math.ceil(scaled.armor * multiplier);
            }
            if (scaled.toughness > 0) {
                scaled.toughness *= multiplier;
            }
        } else {
            // Fall back to tier multiplier
            LOGGER.debug("Enderite stats not available, using tier multiplier");
            scaled = scaleStats(baseStats, targetTier);
        }

        return scaled;
    }

    /**
     * Clears the registry
     */
    public void clear() {
        registeredVariants.clear();
        variantsByMod.clear();
        variantsByTier.clear();
        LOGGER.info("Cleared variant registry");
    }
}
