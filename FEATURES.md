# Variant Generator Mod - Features Documentation

## Core Features

### 1. Automatic Item Detection
- **Automatic Scanning**: Scans all mod directories for items with "iron" in the name
- **Multi-Mod Support**: Works with items from any mod that follows naming conventions
- **Flexible Pattern Matching**: Configurable scan patterns for different item types
- **Mod Exclusion**: Can exclude specific mods from scanning (Minecraft, self, etc.)

### 2. Texture Colorization
#### Grayscale Analysis
- Detects grayscale pixels (R=G=B) in source textures
- Extracts brightness range (min/max)
- Identifies texture characteristics for intelligent coloring

#### Color Mapping Algorithm
```
Input: Grayscale texture + Reference color palette
Process:
  1. Analyze source brightness range
  2. Load reference texture (e.g., Enderite Ingot)
  3. Extract bright and dark reference colors
  4. For each grayscale pixel:
     - Calculate relative brightness (0-1)
     - Interpolate between dark and bright colors
     - Apply interpolated color
  5. Preserve alpha channel (transparency)
Output: Colorized texture
```

#### Color Palettes
- **Iron**: Neutral grayscale (53 → 255)
- **Netherite**: Dark purplish-gray (40 → 100)
- **Enderite**: Deep cyan (4 → 29)

### 3. Item Variant Generation
#### What Gets Generated
- **Tools**: Pickaxes, axes, shovels, hoes, swords, shears
- **Armor**: Helmets, chestplates, leggings, boots
- **Weapons**: Swords, tridents, crossbows
- **Equipment**: Shields, elytra
- **Blocks**: Ores, storage blocks, decorative blocks
- **Items**: Ingots, nuggets, raw materials, crafting components

#### Statistics Scaling
The mod intelligently scales item properties based on material tier:

**Tool Properties**:
- Mining Speed: `iron_speed × tier_multiplier`
- Attack Damage: `iron_damage × tier_multiplier`
- Attack Speed: Preserved from original
- Durability: `iron_durability × tier_multiplier`
- Enchantability: Scaled with reduced impact (×1.1)

**Armor Properties**:
- Protection: `iron_protection × tier_multiplier`
- Durability: `iron_durability × tier_multiplier`
- Enchantability: Scaled appropriately
- Toughness: Added for higher tiers
- Knockback Resistance: Configurable per tier

**Tier Multipliers**:
- Iron: 1.0x (baseline)
- Netherite: 1.25x
- Enderite: 1.5x

### 4. Recipe Generation
#### Recipe Scanning
- Scans recipe JSON files from other mods
- Identifies recipes using iron items
- Analyzes crafting patterns

#### Automatic Recipe Creation
- **Shaped Recipes**: Preserves crafting pattern
- **Shapeless Recipes**: Converts to variant materials
- **Smelting**: Creates variant smelting recipes
- **Smithing**: Generates upgrade recipes using smithing table

#### Recipe Template System
```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "III",
    "I I",
    "III"
  ],
  "key": {
    "I": { "item": "modid:iron_item" }
  },
  "result": {
    "item": "modid:iron_result",
    "count": 1
  }
}

// Converted to:
{
  "type": "minecraft:smithing_transform",
  "template": "variantgenerator:variant_upgrade_template",
  "base": "modid:netherite_result",
  "addition": "enderitemod:enderite_ingot",
  "result": { "item": "modid:enderite_result" }
}
```

### 5. Texture Generation
#### File Processing
- Loads PNG textures from mod directories
- Supports all standard Minecraft texture formats
- Preserves transparency and alpha channels
- Maintains texture resolution

#### Output Generation
- Generates colorized variants for each tier
- Creates accompanying model JSON files
- Outputs with proper naming conventions
- Supports mipmaps and optimization

#### Advanced Processing Options
- **HSL Transformation**: Hue, saturation, lightness adjustments
- **Brightness Correction**: Gamma correction for realistic scaling
- **Missing Color Generation**: Interpolation for incomplete palettes
- **Quality Levels**: Fast, balanced, quality modes

### 6. Model Generation
#### Automatic Model Creation
- Generates item model JSON files
- References texture locations correctly
- Supports multiple item types:
  - `item/generated` (simple items)
  - `item/handheld` (tools)
  - Custom parents for special items

#### Model Structure
```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "modid:item/iron_item"
  }
}
```

### 7. Configuration System
#### Tier Configuration
```yaml
Tiers:
  Iron:
    Multiplier: 1.0
    Color: Grayscale
  Netherite:
    Multiplier: 1.25
    Color: Dark Gray
  Enderite:
    Multiplier: 1.5
    Color: Cyan
```

#### Stat Scaling Configuration
- Adjustable multipliers per tier
- Enable/disable specific properties
- Min/max value constraints
- Enchantability scaling options

#### Texture Configuration
- Texture size settings
- Mipmap generation
- Output format options
- Reference texture locations

#### Scanning Configuration
- Scan patterns (item name matching)
- Excluded mods list
- Subdirectory scanning depth
- File filter options

### 8. Data Export & Logging
#### Comprehensive Logging
- Logs all variant generation steps
- Tracks texture processing progress
- Records recipe creation
- Detailed error messages
- Performance metrics

#### Debug Information
- Color analysis results
- Stat scaling calculations
- Registry statistics
- File operation logs
- Processing timing

### 9. Integration Features
#### Mod Compatibility
- Works alongside Enderite mod
- Respects existing item registries
- Non-destructive modifications
- Backward compatible

#### Multi-Mod Support
- Handles items from multiple mods simultaneously
- Preserves original mod items
- Creates separate namespace for variants
- Avoids ID conflicts

#### Architectury Support
- Built with Architectury API
- Supports multiple loaders (extensible)
- Platform-agnostic core code
- Loader-specific adaptations only in entry points

### 10. Extension Points
#### Custom Color Palettes
Add new material tiers with custom colors:
```java
public static final TierColors CUSTOM = new TierColors(
    "CustomTier",
    new Pixel(255, 200, 100, 255),  // Bright
    new Pixel(100, 50, 20, 255)      // Dark
);
```

#### Custom Stat Templates
Define item-type-specific stat scaling:
```java
StatScaling scaling = new StatScaling();
scaling.netheriteMultiplier = 1.3f;
scaling.enderiteMultiplier = 1.6f;
```

#### Advanced Texture Processing
Use color space transformations:
```java
BufferedImage hslTransformed = AdvancedTextureProcessor
    .transformHSL(source, hueShift, saturation, lightness);
```

## Feature Comparison Matrix

| Feature | Iron | Netherite | Enderite |
|---------|------|-----------|----------|
| Tool Variants | ✓ | ✓ | ✓ |
| Armor Variants | ✓ | ✓ | ✓ |
| Weapon Variants | ✓ | ✓ | ✓ |
| Equipment Variants | ✓ | ✓ | ✓ |
| Texture Colorization | ✓ | ✓ | ✓ |
| Recipe Generation | ✓ | ✓ | ✓ |
| Stat Scaling | 1.0x | 1.25x | 1.5x |
| Custom Colors | Yes | Yes | Yes |
| Enchantability | Normal | Normal | Normal |
| Toughness | No | Partial | Yes |
| Knockback Resistance | No | Configurable | Yes |

## Performance Characteristics

### Memory Usage
- Lightweight texture processing (pixel-by-pixel)
- Efficient color space conversions
- Minimal caching overhead

### Processing Speed
- **Texture Processing**: ~10-50ms per texture (16×16)
- **Item Registration**: O(n) where n = number of items
- **Recipe Generation**: O(r) where r = number of recipes
- **Total Generation**: < 1 second for typical mods

### Scalability
- Tested with 50+ mod items
- Supports 100+ recipes
- Linear scaling with item count
- No significant memory leaks

## Limitations & Constraints

### Current Limitations
- Only processes PNG textures (not other formats)
- Requires standardized naming conventions (iron_*)
- Assumes grayscale iron textures
- Simple recipe template system

### Future Enhancements
- Support for other texture formats
- Machine learning for item type detection
- Advanced recipe template system
- Custom transformation chains
- Per-item configuration

## Use Cases

### 1. Vanilla Item Variants
Create variant armor/tools for vanilla items:
- iron_pickaxe → netherite_pickaxe → enderite_pickaxe

### 2. Modded Items
Extend any mod's iron items with high-tier variants:
- mymod:iron_hammer → mymod:netherite_hammer → mymod:enderite_hammer

### 3. Complete Item Sets
Generate full variant progression:
- Source: 1 iron item
- Output: 2 variants (netherite, enderite)
- Total: 3 tiers

### 4. Texture Customization
Create themed material variants:
- Same item shape, different colors
- Preserve original aesthetic
- Add visual progression

### 5. Modpack Development
Populate modpacks with balanced variant items:
- Automatic generation
- Consistent stat scaling
- Reduced manual work
