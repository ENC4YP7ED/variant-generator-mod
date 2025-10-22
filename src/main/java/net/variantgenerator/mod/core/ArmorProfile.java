package net.variantgenerator.mod.core;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Tracks armor protection and durability properties from Enderite items
 * Captures comprehensive armor statistics for variant generation
 */
public class ArmorProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-ArmorProfile");

    /**
     * Armor statistics
     */
    public static class ArmorStats {
        public String itemName;
        public EquipmentType slotType;
        public int protection;
        public int maxDamage;
        public float toughness;
        public float knockbackResistance;

        public ArmorStats(String itemName, EquipmentType slot, int protection) {
            this.itemName = itemName;
            this.slotType = slot;
            this.protection = protection;
            this.maxDamage = 0;
            this.toughness = 0.0f;
            this.knockbackResistance = 0.0f;
        }

        @Override
        public String toString() {
            return String.format("ArmorStats{name=%s, slot=%s, protection=%d, toughness=%.1f}",
                itemName, slotType, protection, toughness);
        }
    }

    private static final Map<String, ArmorStats> ARMOR_CACHE = new HashMap<>();

    /**
     * Default Enderite armor stats
     */
    public static final ArmorStats ENDERITE_HELMET = new ArmorStats("enderite_helmet", EquipmentType.HELMET, 4);
    public static final ArmorStats ENDERITE_CHESTPLATE = new ArmorStats("enderite_chestplate", EquipmentType.CHESTPLATE, 9);
    public static final ArmorStats ENDERITE_LEGGINGS = new ArmorStats("enderite_leggings", EquipmentType.LEGGINGS, 7);
    public static final ArmorStats ENDERITE_BOOTS = new ArmorStats("enderite_boots", EquipmentType.BOOTS, 4);

    static {
        ENDERITE_HELMET.toughness = 4.0f;
        ENDERITE_HELMET.knockbackResistance = 0.1f;
        ENDERITE_CHESTPLATE.toughness = 4.0f;
        ENDERITE_CHESTPLATE.knockbackResistance = 0.1f;
        ENDERITE_LEGGINGS.toughness = 4.0f;
        ENDERITE_LEGGINGS.knockbackResistance = 0.1f;
        ENDERITE_BOOTS.toughness = 4.0f;
        ENDERITE_BOOTS.knockbackResistance = 0.1f;

        ARMOR_CACHE.put("enderite_helmet", ENDERITE_HELMET);
        ARMOR_CACHE.put("enderite_chestplate", ENDERITE_CHESTPLATE);
        ARMOR_CACHE.put("enderite_leggings", ENDERITE_LEGGINGS);
        ARMOR_CACHE.put("enderite_boots", ENDERITE_BOOTS);

        LOGGER.debug("Initialized armor profile with {} armor pieces", ARMOR_CACHE.size());
    }

    /**
     * Tracks an armor item
     */
    public static void trackArmor(ArmorItem item, String itemName) {
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            if (itemId != null) {
                String key = itemId.getPath();
                ArmorStats stats = new ArmorStats(key, item.getSlotType(), 0);
                ARMOR_CACHE.put(key, stats);
                LOGGER.debug("Tracked armor: {}", stats);
            }
        } catch (Exception e) {
            LOGGER.debug("Error tracking armor", e);
        }
    }

    /**
     * Gets armor stats
     */
    public static ArmorStats getArmorStats(String armorName) {
        return ARMOR_CACHE.getOrDefault(armorName, ENDERITE_HELMET);
    }

    /**
     * Gets all armor in a slot type
     */
    public static Collection<ArmorStats> getArmorBySlot(EquipmentType slot) {
        List<ArmorStats> result = new ArrayList<>();
        ARMOR_CACHE.values().stream()
            .filter(stats -> stats.slotType == slot)
            .forEach(result::add);
        return result;
    }

    /**
     * Gets all tracked armor
     */
    public static Collection<ArmorStats> getAllArmor() {
        return ARMOR_CACHE.values();
    }

    /**
     * Clears the profile
     */
    public static void clearProfile() {
        ARMOR_CACHE.clear();
        LOGGER.debug("Cleared armor profile");
    }
}
