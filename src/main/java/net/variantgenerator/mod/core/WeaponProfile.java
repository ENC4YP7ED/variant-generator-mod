package net.variantgenerator.mod.core;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Tracks weapon properties from Enderite items
 * Profiles attack damage, speed, knockback, and other weapon characteristics
 */
public class WeaponProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-WeaponProfile");

    /**
     * Weapon statistics
     */
    public static class WeaponStats {
        public String itemName;
        public String weaponType;
        public float attackDamage;
        public float attackSpeed;
        public float knockback;
        public String material;

        public WeaponStats(String itemName, String type, float damage) {
            this.itemName = itemName;
            this.weaponType = type;
            this.attackDamage = damage;
            this.attackSpeed = -2.4f; // Default sword speed
            this.knockback = 0.0f;
            this.material = "unknown";
        }

        @Override
        public String toString() {
            return String.format("WeaponStats{name=%s, type=%s, damage=%.1f, speed=%.1f}",
                itemName, weaponType, attackDamage, attackSpeed);
        }
    }

    private static final Map<String, WeaponStats> WEAPON_CACHE = new HashMap<>();

    /**
     * Default Enderite weapon stats
     */
    public static final WeaponStats ENDERITE_SWORD_STATS = new WeaponStats("enderite_sword", "sword", 6.0f);
    public static final WeaponStats NETHERITE_SWORD_STATS = new WeaponStats("netherite_sword", "sword", 8.0f);
    public static final WeaponStats IRON_SWORD_STATS = new WeaponStats("iron_sword", "sword", 6.0f);

    static {
        WEAPON_CACHE.put("enderite_sword", ENDERITE_SWORD_STATS);
        WEAPON_CACHE.put("netherite_sword", NETHERITE_SWORD_STATS);
        WEAPON_CACHE.put("iron_sword", IRON_SWORD_STATS);
        LOGGER.debug("Initialized weapon profile with {} weapons", WEAPON_CACHE.size());
    }

    /**
     * Tracks a weapon item
     */
    public static void trackWeapon(Item item, String type, float attackDamage) {
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            if (itemId != null) {
                String key = itemId.getPath();
                WeaponStats stats = new WeaponStats(key, type, attackDamage);
                WEAPON_CACHE.put(key, stats);
                LOGGER.debug("Tracked weapon: {}", stats);
            }
        } catch (Exception e) {
            LOGGER.debug("Error tracking weapon", e);
        }
    }

    /**
     * Gets weapon stats
     */
    public static WeaponStats getWeaponStats(String weaponName) {
        return WEAPON_CACHE.getOrDefault(weaponName, IRON_SWORD_STATS);
    }

    /**
     * Gets all tracked weapons
     */
    public static Collection<WeaponStats> getAllWeapons() {
        return WEAPON_CACHE.values();
    }

    /**
     * Clears the profile
     */
    public static void clearProfile() {
        WEAPON_CACHE.clear();
        LOGGER.debug("Cleared weapon profile");
    }
}
