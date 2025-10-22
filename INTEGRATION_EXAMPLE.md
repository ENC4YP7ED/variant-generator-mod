# Mod Integration Example

This guide shows how another mod can work with the Variant Generator to automatically create variants of its items.

## Requirements

Your mod needs:
1. Items with "iron" prefix in their name (e.g., `iron_hammer`, `iron_axe`)
2. PNG texture files following Minecraft standards
3. Proper item registration with NeoForge
4. Optional: Custom material definitions

## Step 1: Declare Variant Generator as Dependency

In your `mods.toml`:

```toml
[[dependencies.yourmod]]
    modId="variantgenerator"
    mandatory=false
    versionRange="[1.0.0,)"
    ordering="AFTER"
    side="BOTH"
```

Set `mandatory=false` if you want your mod to work independently.

## Step 2: Name Your Items Consistently

The Variant Generator scans for items with "iron" in the name:

```java
public class YourModItems {
    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register(
        "iron_hammer",
        () -> new ToolItem(YourModMaterial.IRON, 2, -3.0f,
            new Item.Settings().tab(CreativeModeTab.TAB_TOOLS))
    );

    public static final RegistryObject<Item> IRON_PICKAXE = ITEMS.register(
        "iron_pickaxe",
        () -> new PickaxeItem(YourModMaterial.IRON, 1, -2.8f,
            new Item.Settings().tab(CreativeModeTab.TAB_TOOLS))
    );
}
```

**Naming Convention:**
- ✅ `iron_hammer` → generates `netherite_hammer`, `enderite_hammer`
- ✅ `iron_axe` → generates `netherite_axe`, `enderite_axe`
- ✅ `iron_sword` → generates `netherite_sword`, `enderite_sword`
- ❌ `special_item` → NOT scanned (doesn't contain "iron")
- ❌ `iron` → NOT scanned (too short)

## Step 3: Create Textures

Place textures in `src/main/resources/assets/yourmod/textures/item/`:

```
your_mod/
├── textures/
│   └── item/
│       ├── iron_hammer.png      (16×16 grayscale)
│       ├── iron_pickaxe.png     (16×16 grayscale)
│       └── iron_axe.png         (16×16 grayscale)
```

**Texture Requirements:**
- **Format:** PNG with alpha channel (RGBA)
- **Size:** 16×16 pixels (standard Minecraft)
- **Color:** Grayscale (R=G=B) for best results
- **Brightness Range:** 53 (dark) to 255 (light)

**Grayscale Format:**
```
Pixel structure: (Red, Green, Blue, Alpha)
Valid: (100, 100, 100, 255) → grayscale
Valid: (255, 255, 255, 255) → white
Invalid: (255, 100, 50, 255) → not grayscale
```

## Step 4: Define Item Models

Create JSON models in `src/main/resources/assets/yourmod/models/item/`:

```json
{
  "parent": "item/handheld",
  "textures": {
    "layer0": "yourmod:item/iron_hammer"
  }
}
```

The Variant Generator will automatically:
1. ✅ Generate colored variants of your textures
2. ✅ Create model files for `netherite_hammer` and `enderite_hammer`
3. ✅ Register the items
4. ✅ Generate crafting recipes

## Step 5: Create Recipes (Optional)

While Variant Generator creates recipes, you can provide templates:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "I I",
    "IHI",
    " I "
  ],
  "key": {
    "I": { "item": "minecraft:iron_ingot" },
    "H": { "item": "minecraft:stick" }
  },
  "result": {
    "item": "yourmod:iron_hammer",
    "count": 1
  }
}
```

Variant Generator will automatically create:
- Netherite version (using netherite ingots)
- Enderite version (using enderite ingots)
- Smithing upgrade recipes (for tool upgrades)

## Example: Custom Hammer Mod

### Project Structure
```
CustomHammerMod/
├── src/main/java/com/example/hammers/
│   ├── CustomHammerMod.java
│   └── items/
│       └── HammerItems.java
├── src/main/resources/
│   ├── assets/customhammers/
│   │   ├── textures/item/
│   │   │   ├── iron_hammer.png
│   │   │   ├── iron_pickaxe.png
│   │   │   └── iron_shovel.png
│   │   ├── models/item/
│   │   │   ├── iron_hammer.json
│   │   │   ├── iron_pickaxe.json
│   │   │   └── iron_shovel.json
│   │   └── lang/
│   │       └── en_us.json
│   └── data/customhammers/
│       └── recipes/
│           ├── iron_hammer.json
│           ├── iron_pickaxe.json
│           └── iron_shovel.json
└── mods.toml
```

### Java Code Example

```java
package com.example.hammers;

import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

@Mod("customhammers")
public class CustomHammerMod {
    public static final String MOD_ID = "customhammers";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);

    static {
        ITEMS.register("iron_hammer", () -> new ToolItem(
            CustomHammerMaterial.IRON,
            2,           // attack damage bonus
            -3.0f,       // attack speed
            new Item.Settings()
        ));

        ITEMS.register("iron_pickaxe", () -> new PickaxeItem(
            CustomHammerMaterial.IRON,
            1,           // attack damage bonus
            -2.8f,       // attack speed
            new Item.Settings()
        ));

        ITEMS.register("iron_shovel", () -> new ShovelItem(
            CustomHammerMaterial.IRON,
            1.5f,        // attack damage bonus
            -3.0f,       // attack speed
            new Item.Settings()
        ));
    }
}
```

### Item Model JSON

**iron_hammer.json:**
```json
{
  "parent": "item/handheld",
  "textures": {
    "layer0": "customhammers:item/iron_hammer"
  }
}
```

### Recipe JSON

**iron_hammer.json:**
```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "III",
    "ISI",
    " S "
  ],
  "key": {
    "I": { "item": "minecraft:iron_ingot" },
    "S": { "item": "minecraft:stick" }
  },
  "result": {
    "item": "customhammers:iron_hammer",
    "count": 1
  }
}
```

## What Variant Generator Does Automatically

When your mod loads with iron items:

### 1. Texture Generation
```
Input:  customhammers:iron_hammer.png (grayscale)
        ↓
Process: Analyze brightness range (53-255)
        Extract Enderite colors: (4,14,12) → (29,94,83)
        ↓
Output: customhammers:netherite_hammer.png (dark gray)
        customhammers:enderite_hammer.png (cyan)
```

### 2. Item Registration
```
Registers automatically:
- customhammers:netherite_hammer (with scaled stats)
- customhammers:enderite_hammer (with scaled stats)

Stats scaled by:
- Netherite: 1.25x
- Enderite: 1.5x
```

### 3. Model Generation
```
Creates automatically:
- assets/customhammers/models/item/netherite_hammer.json
- assets/customhammers/models/item/enderite_hammer.json
```

### 4. Recipe Generation
```
Creates automatically:
- Crafting recipe for netherite_hammer (using netherite ingots)
- Crafting recipe for enderite_hammer (using enderite ingots)
- Smithing recipe: iron_hammer → netherite_hammer → enderite_hammer
```

## Customization

### Adjust Stat Scaling

If you want custom scaling, override in your mod:

```java
// In your mod's variant setup
EnderiteStatCache.cacheToolMaterial(
    "my_material",
    durability,
    miningSpeed,
    attackDamage,
    enchantability
);
```

### Custom Colors

If you want specific colors for your variants:

```java
// Override default colors
EnderiteColorCache.cacheItemColors(yourItem);
// Or manually set colors
Pixel customBright = new Pixel(200, 100, 50, 255);
Pixel customDark = new Pixel(100, 50, 20, 255);
```

### Skip Variant Generation

If you create an item with "iron" but don't want variants:

```java
// Name it differently
ITEMS.register("iron_special_unique", ...); // Won't scan for "iron_special"
```

## Testing Your Integration

### 1. Check Logs for Variant Generation

```
[INFO] Found items to generate variants for
[DEBUG] Generating netherite variant for: iron_hammer
[DEBUG] Generating enderite variant for: iron_hammer
[INFO] Completed variant generation
```

### 2. Verify Generated Files

Check that these files exist:
- Textures: `customhammers:netherite_hammer.png`, `customhammers:enderite_hammer.png`
- Models: `netherite_hammer.json`, `enderite_hammer.json`
- Recipes: recipe files for variants

### 3. Test in Game

```
/give @s customhammers:netherite_hammer
/give @s customhammers:enderite_hammer
```

Verify:
- ✅ Items appear in creative menu
- ✅ Textures display correctly
- ✅ Models render correctly
- ✅ Crafting recipes work
- ✅ Stats are properly scaled

## Troubleshooting

### Variants Not Generated

**Problem:** No netherite/enderite variants appear
**Solution:**
1. Check item names contain "iron"
2. Verify textures are grayscale
3. Check logs for errors
4. Ensure Variant Generator is installed
5. Verify Enderite mod is installed

### Wrong Colors

**Problem:** Generated colors are incorrect
**Solution:**
1. Verify textures are true grayscale (R=G=B)
2. Check color extraction in logs
3. Override with custom colors if needed
4. Check Enderite color cache

### Stats Too High/Low

**Problem:** Variant items are overpowered
**Solution:**
1. Adjust base item stats (before generation)
2. Override stat cache values
3. Modify multipliers in config

### Recipes Not Working

**Problem:** Crafting recipes for variants don't work
**Solution:**
1. Check recipe JSON syntax
2. Verify item registration
3. Check recipe resource pack location
4. Ensure items are in registries

## Performance Notes

Variant generation happens once at startup:
- Minimal performance impact (<1s)
- Caches all data in memory
- No runtime overhead

## Distribution

When distributing your mod with Variant Generator:

1. **Optional Dependency:** Users can use your mod without Variant Generator
2. **With Variants:** Users with Variant Generator get automatic netherite/enderite items
3. **Better Together:** Encourage using both mods for maximum content

## Examples in Wild

Mods that could benefit from Variant Generator:
- Tool mods (hammers, drills, etc.)
- Weapon mods (swords, spears, etc.)
- Armor mods (custom armor sets)
- Equipment mods (shields, elytra variants)
- Any mod with "iron_*" named items

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System design
- [FEATURES.md](FEATURES.md) - Feature list
- [ENDERITE_INTEGRATION.md](ENDERITE_INTEGRATION.md) - Enderite integration details
