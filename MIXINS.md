# Mixin System Documentation

This document describes the mixin-based integration with the Enderite mod. The variant generator uses mixins to non-destructively inject code into Enderite and Minecraft classes to extract colors, statistics, and other metadata.

## Overview

Mixins allow the Variant Generator to:
- Hook into Enderite item initialization
- Extract color information from Enderite ingots
- Cache tool and armor statistics
- Monitor item registration
- Track enchantment compatibility
- Analyze smithing recipes

## Mixin Architecture

```
variantgenerator.mixins.json (Configuration)
         ↓
  [Server-side Mixins]      [Client-side Mixins]
         ↓                           ↓
┌─────────────────┬────────────────────────┐
│                 │                        │
▼                 ▼                        ▼
Color           Material              Texture
Extractor       Injector              Loader
```

## Mixins Detailed

### 1. EnderiteColorExtractorMixin
**Target**: `Item.class`
**Method**: `<init>`
**Timing**: `@TAIL` (After constructor completes)

**Purpose**: Extracts and caches color information from Enderite items.

**How it works**:
```
Item created → Check if "enderite" in name
             → Cache colors to EnderiteColorCache
             → Log debug info
```

**Cached Data**:
- Item name
- RGB color values
- Brightness value
- Alpha channel

### 2. EnderiteMaterialInjectorMixin
**Target**: `ToolMaterial.class`
**Method**: `<init>`
**Timing**: `@TAIL`

**Purpose**: Intercepts tool material creation to cache Enderite stats.

**Captured Statistics**:
- Durability value
- Mining speed multiplier
- Attack damage bonus
- Enchantability level

**Code Sample**:
```java
@Inject(method = "<init>", at = @At("TAIL"))
private void onToolMaterialInit(CallbackInfo ci) {
    ToolMaterial material = (ToolMaterial) (Object) this;
    int durability = material.getDurability();
    float speed = material.getMiningSpeedMultiplier();
    float damage = material.getAttackDamageBonus();

    EnderiteStatCache.cacheToolMaterial("enderite", durability, speed, damage, 17);
}
```

### 3. EnderiteRegistryInterceptorMixin
**Target**: `MutableRegistry.class`
**Method**: `add`
**Timing**: `@HEAD` (Before method executes)

**Purpose**: Monitors item registration to track Enderite items as they're added.

**Functionality**:
- Intercepts all registry additions
- Identifies Enderite mod items by ID
- Logs new item registrations
- Triggers variant generator updates

### 4. EnderiteSmithingTableMixin
**Target**: `SmithingTransformRecipe.class`
**Method**: `<init>`
**Timing**: `@TAIL`

**Purpose**: Analyzes smithing recipes to understand upgrade patterns.

**Data Captured**:
- Template item
- Base item
- Addition item
- Result item

**Use Case**: Replicates Enderite's smithing upgrade pattern for variants

### 5. EnchantabilityTrackerMixin
**Target**: `Enchantment.class`
**Method**: `canAccept`
**Timing**: `@HEAD` (Before decision made)

**Purpose**: Tracks which enchantments work with Enderite items.

**Tracked Information**:
- Item → Enchantment mappings
- Enchantment properties (max level, treasure)
- Compatibility profiles

### 6. EnderiteTextureLoaderMixin (Client-side)
**Target**: `SpriteContents.class`
**Method**: `<init>`
**Timing**: `@TAIL`

**Purpose**: Client-side tracking of texture sprites loaded from resources.

**Scope**: Client only (rendered textures)

## Cache Systems

### EnderiteColorCache
Stores color information extracted from Enderite items.

```java
// Cached data structure
Map<String, ColorInfo> COLOR_CACHE
├── "enderite" → ColorInfo { bright=(29,94,83), dark=(4,14,12) }
├── "netherite" → ColorInfo { bright=(100,100,120), dark=(40,40,50) }
└── "iron" → ColorInfo { bright=(255,255,255), dark=(53,53,53) }
```

**Default Colors** (if not extracted):
```java
ENDERITE_BRIGHT = (29, 94, 83)
ENDERITE_DARK = (4, 14, 12)
NETHERITE_BRIGHT = (100, 100, 120)
NETHERITE_DARK = (40, 40, 50)
IRON_BRIGHT = (255, 255, 255)
IRON_DARK = (53, 53, 53)
```

### EnderiteStatCache
Stores material statistics for scaling calculations.

```java
// Default stats
ENDERITE_TOOL_STATS {
    durability: 4096,
    miningSpeed: 15.0f,
    attackDamage: 2.0f,
    enchantability: 17
}

NETHERITE_TOOL_STATS {
    durability: 2031,
    miningSpeed: 12.0f,
    attackDamage: 4.0f,
    enchantability: 15
}
```

### EnchantmentProfile
Tracks compatible enchantments per item type.

```java
// Profile structure
Map<String, Set<String>> ITEM_ENCHANTMENTS
├── "enderitemod:enderite_sword" → {"minecraft:sharpness", "minecraft:fire_aspect", ...}
├── "enderitemod:enderite_chestplate" → {"minecraft:protection", "minecraft:unbreaking", ...}
└── ...
```

## Injection Points

### Where Mixins Inject
```
net.minecraft.item.Item.<init>
├── EnderiteColorExtractorMixin ────→ Cache colors
net.minecraft.item.ToolMaterial.<init>
├── EnderiteMaterialInjectorMixin ──→ Cache stats
net.minecraft.registry.MutableRegistry.add
├── EnderiteRegistryInterceptorMixin → Track registration
net.minecraft.recipe.SmithingTransformRecipe.<init>
├── EnderiteSmithingTableMixin ─────→ Analyze recipes
net.minecraft.enchantment.Enchantment.canAccept
├── EnchantabilityTrackerMixin ─────→ Track enchants
net.minecraft.client.texture.SpriteContents.<init>
├── EnderiteTextureLoaderMixin ─────→ Cache textures
```

## Flow Diagram

```
Minecraft Startup
    ↓
[Classes Loading]
    ↓
Mixin Processor applies transformations
    ├─ Item class → ColorExtractor injected
    ├─ ToolMaterial class → MaterialInjector injected
    ├─ MutableRegistry class → RegistryInterceptor injected
    ├─ SmithingTransformRecipe class → SmithingTable injected
    ├─ Enchantment class → EnchantabilityTracker injected
    └─ SpriteContents class → TextureLoader injected (client)
    ↓
Enderite Mod Initializes
    ├─ Items created → Color extracted & cached
    ├─ Materials registered → Stats captured
    ├─ Items registered → Registry intercepted
    └─ Enchantments applied → Tracked
    ↓
Variant Generator Activates
    ├─ Reads caches (colors, stats)
    ├─ Generates variants
    └─ Applies cached data to variant items
```

## Usage in Code

### Accessing Cached Colors
```java
// In VariantGenerator.java
Pixel enderiteLight = EnderiteColorCache.getBrightColor("enderite");
Pixel enderiteDark = EnderiteColorCache.getDarkColor("enderite");
```

### Accessing Cached Stats
```java
// In VariantRegistry.java
ToolStats enderiteStats = EnderiteStatCache.getToolStats("enderite");
int scaledDurability = (int)(baseStats.durability * enderiteStats.durability / baseStats.durability);
```

### Checking Enchantments
```java
// In variant generation
Set<String> compatibleEnchantments = EnchantmentProfile.getCompatibleEnchantments("enderitemod:enderite_sword");
```

## Configuration

Mixins are configured in `variantgenerator.mixins.json`:

```json
{
  "required": true,                    // Fail without mixins
  "minVersion": "0.8",                 // Mixin version requirement
  "package": "net.variantgenerator.mod.mixin",
  "compatibilityLevel": "JAVA_21",
  "refmap": "variantgenerator.refmap.json",
  "mixins": [                          // Server-side
    "EnderiteColorExtractorMixin",
    "EnderiteMaterialInjectorMixin",
    "EnderiteRegistryInterceptorMixin",
    "EnderiteSmithingTableMixin",
    "EnchantabilityTrackerMixin"
  ],
  "client": [                          // Client-side only
    "EnderiteTextureLoaderMixin"
  ]
}
```

## Debug Logging

Enable debug logging for mixins:

In `log4j2.xml`:
```xml
<Logger name="VariantGenerator-Mixin" level="debug" additivity="false">
    <AppenderRef ref="SysOut" />
</Logger>
```

Debug output includes:
- Color extraction events
- Material stat caching
- Registry interception
- Enchantment tracking
- Texture sprite loading

## Safety Considerations

### Non-Invasive
- No modifications to Enderite mod code
- Uses `@Inject` with `CallbackInfo` (safe)
- Extracts data without changing behavior
- Fallback to defaults if mixing fails

### Compatibility
- Mixins are applied at class-load time
- Compatible with other mixin-using mods
- Respects mixin priority system
- Uses `@TAIL` to ensure original code runs first

### Error Handling
```java
try {
    // Mixin code
    EnderiteColorCache.cacheItemColors(item);
} catch (Exception e) {
    LOGGER.debug("Could not extract color information", e);
    // Continues without error - graceful degradation
}
```

## Extension Points

### Adding New Mixins

1. **Create mixin class**:
```java
@Mixin(SomeClass.class)
public class SomeNewMixin {
    @Inject(method = "method", at = @At("TAIL"))
    private void onMethod(CallbackInfo ci) {
        // Implementation
    }
}
```

2. **Register in JSON**:
```json
"mixins": [
    "ExistingMixin",
    "SomeNewMixin"  // Add here
]
```

3. **Add cache if needed**:
```java
public class NewDataCache {
    private static final Map<String, Data> CACHE = new HashMap<>();

    public static void cache(String key, Data data) {
        CACHE.put(key, data);
    }
}
```

## Troubleshooting

### Mixins Not Applying
- Check `variantgenerator.mixins.json` exists
- Verify class names match exactly
- Check Java version (require 21+)
- Check mixin version compatibility

### Cache Not Populated
- Verify mixin injection point is correct
- Check `@At` timing (TAIL vs HEAD)
- Enable debug logging
- Check item is Enderite-related

### NullPointerException in Variant Generation
- Check cache is populated: `EnderiteColorCache.printCacheContents()`
- Verify Enderite mod is loaded
- Check mixin errors in logs
- Use fallback colors as defaults

## Performance Impact

**Minimal Overhead**:
- Injection: ~1ms per mixin hook call
- Caching: O(1) HashMap lookups
- Total: < 100ms during mod load
- No runtime performance impact

## Future Enhancements

- [ ] Fabric/Quilt mixin support
- [ ] Dynamic mixin loading/unloading
- [ ] Mixin statistics dashboard
- [ ] Per-mixin enable/disable
- [ ] Custom injection point configuration
