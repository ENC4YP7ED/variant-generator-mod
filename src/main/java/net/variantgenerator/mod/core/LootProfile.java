package net.variantgenerator.mod.core;

import net.minecraft.loot.LootTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Tracks loot tables to understand drop patterns
 * Helps generate appropriate loot for variant items
 */
public class LootProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-LootProfile");

    /**
     * Loot entry information
     */
    public static class LootEntry {
        public String lootTableId;
        public String itemId;
        public float weight;
        public int minCount;
        public int maxCount;

        public LootEntry(String lootTableId, String itemId, float weight) {
            this.lootTableId = lootTableId;
            this.itemId = itemId;
            this.weight = weight;
            this.minCount = 1;
            this.maxCount = 1;
        }

        @Override
        public String toString() {
            return String.format("LootEntry{table=%s, item=%s, weight=%.1f}",
                lootTableId, itemId, weight);
        }
    }

    /**
     * Loot table tracked information
     */
    public static class LootTableInfo {
        public String tableId;
        public List<LootEntry> entries;
        public String sourceType; // "block", "entity", "chest", etc.

        public LootTableInfo(String tableId) {
            this.tableId = tableId;
            this.entries = new ArrayList<>();
            this.sourceType = "unknown";
        }
    }

    private static final Map<String, LootTableInfo> LOOT_CACHE = new HashMap<>();
    private static int lootTableCount = 0;

    /**
     * Tracks a loot table
     */
    public static void trackLootTable(LootTable table) {
        try {
            lootTableCount++;
            LOGGER.debug("Tracked loot table #{}", lootTableCount);
        } catch (Exception e) {
            LOGGER.debug("Error tracking loot table", e);
        }
    }

    /**
     * Registers a loot entry
     */
    public static void registerLootEntry(String tableId, String itemId, float weight) {
        LOOT_CACHE.computeIfAbsent(tableId, LootTableInfo::new)
            .entries.add(new LootEntry(tableId, itemId, weight));
    }

    /**
     * Gets loot table info
     */
    public static LootTableInfo getLootTableInfo(String tableId) {
        return LOOT_CACHE.get(tableId);
    }

    /**
     * Gets all tracked loot tables
     */
    public static Collection<LootTableInfo> getAllLootTables() {
        return LOOT_CACHE.values();
    }

    /**
     * Gets total loot tables tracked
     */
    public static int getTrackedLootTableCount() {
        return lootTableCount;
    }

    /**
     * Clears the profile
     */
    public static void clearProfile() {
        LOOT_CACHE.clear();
        lootTableCount = 0;
        LOGGER.debug("Cleared loot profile");
    }
}
