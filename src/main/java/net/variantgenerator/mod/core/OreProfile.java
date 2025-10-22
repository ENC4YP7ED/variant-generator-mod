package net.variantgenerator.mod.core;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Tracks ore block properties including hardness and blast resistance
 * Used to determine mining tool requirements and proper tier assignment
 */
public class OreProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-OreProfile");

    /**
     * Ore block statistics
     */
    public static class OreStats {
        public String blockName;
        public float hardness;
        public float blastResistance;
        public float miningSpeed;
        public String requiredTool;

        public OreStats(String blockName, float hardness, float blastResistance) {
            this.blockName = blockName;
            this.hardness = hardness;
            this.blastResistance = blastResistance;
            this.miningSpeed = 1.0f;
            this.requiredTool = determineRequiredTool(hardness);
        }

        private String determineRequiredTool(float hardness) {
            if (hardness >= 50.0f) return "enderite_pickaxe";
            if (hardness >= 20.0f) return "netherite_pickaxe";
            if (hardness >= 5.0f) return "iron_pickaxe";
            return "wooden_pickaxe";
        }

        @Override
        public String toString() {
            return String.format("OreStats{name=%s, hardness=%.1f, resistance=%.1f, tool=%s}",
                blockName, hardness, blastResistance, requiredTool);
        }
    }

    private static final Map<String, OreStats> ORE_CACHE = new HashMap<>();

    /**
     * Default Enderite ore stats
     */
    public static final OreStats ENDERITE_ORE = new OreStats("enderite_ore", 50.0f, 1200.0f);

    static {
        ORE_CACHE.put("enderite_ore", ENDERITE_ORE);
        LOGGER.debug("Initialized ore profile");
    }

    /**
     * Tracks an ore block
     */
    public static void trackOre(Block block, float hardness, float blastResistance) {
        try {
            Identifier blockId = Registries.BLOCK.getId(block);
            if (blockId != null) {
                String key = blockId.getPath();
                OreStats stats = new OreStats(key, hardness, blastResistance);
                ORE_CACHE.put(key, stats);
                LOGGER.debug("Tracked ore: {}", stats);
            }
        } catch (Exception e) {
            LOGGER.debug("Error tracking ore", e);
        }
    }

    /**
     * Gets ore stats
     */
    public static OreStats getOreStats(String oreName) {
        return ORE_CACHE.getOrDefault(oreName, ENDERITE_ORE);
    }

    /**
     * Determines required tool for a hardness value
     */
    public static String getRequiredToolForHardness(float hardness) {
        if (hardness >= 50.0f) return "enderite";
        if (hardness >= 20.0f) return "netherite";
        if (hardness >= 5.0f) return "iron";
        return "wood";
    }

    /**
     * Gets all tracked ores
     */
    public static Collection<OreStats> getAllOres() {
        return ORE_CACHE.values();
    }

    /**
     * Clears the profile
     */
    public static void clearProfile() {
        ORE_CACHE.clear();
        LOGGER.debug("Cleared ore profile");
    }
}
