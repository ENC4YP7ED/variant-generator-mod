# Enderite Mod Integration Guide

This document describes how the Variant Generator Mod integrates with the Enderite mod using a comprehensive mixin system.

## Integration Overview

The Variant Generator uses **9 strategic mixins** to inject code into Minecraft and Enderite classes, non-destructively extracting:

- Color information from Enderite ingots and items
- Material statistics (durability, mining speed, attack damage)
- Enchantment compatibility profiles
- Weapon damage characteristics
- Armor protection values
- Mining requirements (ore hardness)
- Loot table patterns

## Complete Mixin List

### Core Mixins (Color & Materials)

| Mixin | Target | Purpose | Data Extracted |
|-------|--------|---------|-----------------|
| `EnderiteColorExtractorMixin` | `Item` | Caches Enderite item colors | RGB values, brightness |
| `EnderiteMaterialInjectorMixin` | `ToolMaterial` | Captures tool stats | Durability, speed, damage |

### Integration Mixins

| Mixin | Target | Purpose | Data Extracted |
|-------|--------|---------|-----------------|
| `EnderiteRegistryInterceptorMixin` | `MutableRegistry` | Monitors item registration | Item IDs, registration order |
| `EnderiteSmithingTableMixin` | `SmithingTransformRecipe` | Analyzes upgrade patterns | Template, base, addition, result |
| `EnchantabilityTrackerMixin` | `Enchantment` | Tracks compatible enchantments | Item → Enchantment mappings |

### Property Mixins

| Mixin | Target | Purpose | Data Extracted |
|-------|--------|---------|-----------------|
| `WeaponDamageTrackerMixin` | `SwordItem` | Captures weapon stats | Attack damage, weapon type |
| `ArmorEffectivenessMixin` | `ArmorItem` | Extracts armor properties | Protection, durability, toughness |
| `OreBlockHardnessMixin` | `Block` | Tracks ore difficulty | Hardness, blast resistance |
| `LootTableGeneratorMixin` | `LootTable` | Monitors loot patterns | Drop rates, quantities |

### Client-Side Mixin

| Mixin | Target | Purpose | Data Extracted |
|-------|--------|---------|-----------------|
| `EnderiteTextureLoaderMixin` | `SpriteContents` | Caches texture sprites | Rendered texture data |

## Data Flow Architecture

```
┌─────────────────────────────────────────────────┐
│         Enderite Mod Initialization             │
└────────────┬────────────────────────────────────┘
             │
    ┌────────┴────────┬──────────────┬──────────┬──────────┐
    ▼                 ▼              ▼          ▼          ▼
Items Created    Tools Registered  Recipes  Enchantments Blocks
    │                │              │          │          │
    │                │              │          │          │
▼────────────────────────────────────────────────────────────────▼

ColorExtractor  MaterialInjector  SmithingTable  EnchantTracker  HardnessMixin
    │                │              │              │              │
    └────────────┬───┴──────────┬───┴──────┬──────┴────────┬─────┘
                 │              │          │               │
         EnderiteColorCache  EnderiteStatCache  EnchantmentProfile
                 │              │          │               │
                 └──────────┬───┴──────────┴───────────┬────┘
                            │                         │
                    VariantGenerator        RecipeScanner
                            │                         │
                    ┌────────┴──────────────────┬─────┘
                    │                          │
                    ▼                          ▼
            Generate Textures        Generate Recipes
            (Colorization)           (Smithing/Crafting)
                    │                          │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼─────────┐
                    │  Variant Items      │
                    │  - Netherite Tier  │
                    │  - Enderite Tier   │
                    └────────────────────┘
```

## Cache Systems

### EnderiteColorCache
Stores RGB color values extracted from Enderite items.

**Pre-loaded Default Colors:**
```
Enderite:   Bright=(29, 94, 83),   Dark=(4, 14, 12)
Netherite:  Bright=(100, 100, 120), Dark=(40, 40, 50)
Iron:       Bright=(255, 255, 255), Dark=(53, 53, 53)
```

**Usage in Code:**
```java
Pixel enderiteLight = EnderiteColorCache.getBrightColor("enderite");
Pixel enderiteDark = EnderiteColorCache.getDarkColor("enderite");
```

### EnderiteStatCache
Caches material statistics for scaling calculations.

**Default Tool Stats:**
```
Enderite:  Durability=4096,  Speed=15.0,  Damage=2.0,  Enchantability=17
Netherite: Durability=2031,  Speed=12.0,  Damage=4.0,  Enchantability=15
Iron:      Durability=250,   Speed=6.0,   Damage=6.0,  Enchantability=14
```

**Usage in Code:**
```java
ToolStats stats = EnderiteStatCache.getToolStats("enderite");
float scaledDamage = baseStats.attackDamage * stats.durability / baseDurability;
```

### Additional Profiles

**EnchantmentProfile:** Item ↔ Enchantment compatibility mappings
**WeaponProfile:** Weapon damage and type statistics
**ArmorProfile:** Armor protection by slot and toughness values
**OreProfile:** Mining hardness and required tool tiers
**LootProfile:** Drop patterns and loot table information

## Integration Timeline

```
Game Start
    │
    ├─ 0ms: Mixin Processor loads and transforms classes
    │
    ├─ 100ms: Enderite mod initializes
    │        ├─ Items created → ColorExtractorMixin caches colors
    │        ├─ Tools registered → MaterialInjectorMixin caches stats
    │        ├─ Recipes loaded → SmithingTableMixin analyzes patterns
    │        └─ Enchantments applied → EnchantabilityTracker monitors
    │
    ├─ 150ms: Caches populated with Enderite data
    │
    └─ 200ms: Variant Generator activates
             ├─ Reads all caches
             ├─ Generates netherite variants
             ├─ Generates enderite variants
             └─ Items & recipes created
```

## Mixin Injection Points

### Item Color Extraction
```java
@Mixin(Item.class)
public class EnderiteColorExtractorMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onItemInit(...) {
        // Item created and fully initialized
        // Safe to extract color data
    }
}
```

**Why TAIL?** Ensures the item is fully constructed before extraction.

### Tool Material Caching
```java
@Mixin(ToolMaterial.class)
public class EnderiteMaterialInjectorMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onToolMaterialInit(...) {
        // Tool material fully created
        // Safe to read all properties
    }
}
```

**Why TAIL?** Guarantees all material properties are initialized.

### Registry Interception
```java
@Mixin(MutableRegistry.class)
public class EnderiteRegistryInterceptorMixin {
    @Inject(method = "add", at = @At("HEAD"))
    private void onRegistryAdd(...) {
        // Item about to be registered
        // Can intercept and log before registration
    }
}
```

**Why HEAD?** To observe registration before it happens.

## Error Handling & Fallbacks

All mixins use try-catch blocks with graceful degradation:

```java
try {
    // Mixin code - extract data
    EnderiteColorCache.cacheItemColors(item);
} catch (Exception e) {
    LOGGER.debug("Could not extract color information", e);
    // Continues without error - fallback colors used
}
```

**Fallback Colors (if Enderite not loaded):**
```java
public static final Pixel ENDERITE_BRIGHT = new Pixel(29, 94, 83, 255);
public static final Pixel ENDERITE_DARK = new Pixel(4, 14, 12, 255);
```

## Performance Impact

**Minimal Overhead:**
- Mixin injection: ~1ms per hook
- Cache lookup: O(1) HashMap - <1μs
- Total variant generation: <1000ms
- No runtime performance degradation

## Compatibility

✅ **Compatible With:**
- Fabric Loader (with Fabric-like mixin support)
- NeoForge (current implementation)
- Quilt (via Architectury)
- Other mixin-using mods
- Vanilla Minecraft classes

✅ **Safe Integration:**
- Non-invasive data extraction only
- No modification of Enderite code
- Respects mixin priority system
- Graceful degradation if mixins fail

## Debugging Mixins

### Enable Mixin Logging
```xml
<!-- In log4j2.xml -->
<Logger name="VariantGenerator-Mixin" level="debug">
    <AppenderRef ref="SysOut" />
</Logger>
```

### Debug Output Examples
```
[DEBUG] EnderiteColorExtractorMixin: Cached Enderite ingot colors
[DEBUG] EnderiteMaterialInjectorMixin: Cached Enderite tool: durability=4096
[DEBUG] EnchantabilityTrackerMixin: Tracked enchantment for Enderite item
```

### Verify Cache Populations
```java
// In code or console
EnderiteColorCache.printCacheContents();
EnderiteStatCache.printCacheContents();
```

## Extending the Integration

### Adding New Mixins

1. Create mixin class:
```java
@Mixin(TargetClass.class)
public class YourNewMixin {
    @Inject(method = "methodName", at = @At("TAIL"))
    private void onEvent(CallbackInfo ci) {
        // Your injection code
    }
}
```

2. Register in `variantgenerator.mixins.json`:
```json
"mixins": [
    "ExistingMixin",
    "YourNewMixin"  // Add here
]
```

3. Create a cache class (optional):
```java
public class YourDataCache {
    private static final Map<String, Data> CACHE = new HashMap<>();
    // Cache implementation
}
```

## Troubleshooting

### Mixins Not Applying
- Verify `mixin="variantgenerator.mixins.json"` in neoforge.mods.toml
- Check mixin plugin in build.gradle: `apply plugin: 'org.spongepowered.mixin'`
- Verify Java version is 21+
- Check for mixin errors in logs

### Caches Not Populated
- Verify Enderite mod is loaded and initialized first
- Check mixin dependency ordering
- Enable debug logging to see injection points
- Verify injection timing (@TAIL vs @HEAD)

### Wrong Colors/Stats Used
- Check cache priority (Enderite cache vs defaults)
- Verify cache initialization in static blocks
- Review mixin injection conditions
- Check log output for cache hits

## Related Documentation

- [MIXINS.md](MIXINS.md) - Detailed mixin system documentation
- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture overview
- [FEATURES.md](FEATURES.md) - Feature descriptions

## References

- [Spongepowered Mixin Documentation](https://docs.spongepowered.org/mixin/)
- [Architectury API Docs](https://docs.architectury.dev/)
- [NeoForge Documentation](https://docs.neoforged.net/)
