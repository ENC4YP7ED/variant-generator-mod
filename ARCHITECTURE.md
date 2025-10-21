# Variant Generator Mod - Architecture Documentation

## Overview

The Variant Generator Mod is a sophisticated Minecraft tool for NeoForge 1.21.1 that automatically creates netherite and enderite variants of items from other mods. It combines texture processing, item registration, and recipe generation into a cohesive system.

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    VariantGeneratorMod                      │
│                   (Main Entry Point)                        │
└──────────┬───────────────────────────────────────────────────┘
           │
    ┌──────┴──────┬────────────┬──────────────┬────────────┐
    │             │            │              │            │
    ▼             ▼            ▼              ▼            ▼
┌─────────┐ ┌─────────────┐ ┌──────────┐ ┌────────┐ ┌──────────┐
│Variant  │ │   Texture   │ │  Recipe  │ │Config  │ │Utilities │
│Registry │ │ Colorizer   │ │ Scanner  │ │System  │ │(File/Item)
└────┬────┘ └──────┬──────┘ └────┬─────┘ └───┬────┘ └────┬─────┘
     │             │             │            │           │
     ▼             ▼             ▼            ▼           ▼
┌────────────────────────────────────────────────────────────┐
│        Variant Generator (Orchestrator)                    │
│  - Coordinates variant creation                           │
│  - Manages generation pipeline                            │
│  - Handles error recovery                                 │
└──────────────────┬─────────────────────────────────────────┘
                   │
        ┌──────────┴──────────────┐
        ▼                         ▼
   Textures                   Items/Recipes
(PNGs with colors)       (Registry & models)
```

## Core Components

### 1. VariantGeneratorMod (Core Entry Point)
**Location**: `src/main/java/net/variantgenerator/mod/VariantGeneratorMod.java`

Initialization and coordination of all subsystems:
- Initializes texture colorizer, variant registry, and recipe scanner
- Sets up lifecycle events
- Provides singleton access to all systems

```java
VariantGeneratorMod.init();  // Called during mod load

// Access systems
VariantGeneratorMod.getVariantRegistry();
VariantGeneratorMod.getTextureColorizer();
VariantGeneratorMod.getVariantGenerator();
VariantGeneratorMod.getRecipeScanner();
```

### 2. TextureColorizer (Texture Processing)
**Location**: `src/main/java/net/variantgenerator/mod/texture/TextureColorizer.java`

Implements the grayscale-to-color conversion algorithm (Java equivalent of Python code).

#### Key Features:
- Analyzes grayscale texture to find min/max brightness
- Extracts reference colors from Enderite/Netherite textures
- Maps grayscale values to color range
- Generates missing colors through interpolation

#### Algorithm:
```
For each pixel in image:
  If pixel is grayscale (R=G=B):
    brightness = pixel.r (0-255)
    t = brightness / 255.0
    color = darkColor + t * (brightColor - darkColor)
    pixel.r = color.r
    pixel.g = color.g
    pixel.b = color.b
```

#### Usage:
```java
TextureColorizer colorizer = new TextureColorizer();

// Analyze image
GrayscaleAnalysis analysis = colorizer.analyzeGrayscale(image);

// Recolor image
BufferedImage recolored = colorizer.recolorImage(
    sourceImage,
    enderiteLight,  // Bright reference color
    enderiteDark    // Dark reference color
);

// Save result
colorizer.saveImage(recolored, new File("enderite_item.png"));
```

### 3. VariantRegistry (Item Registration)
**Location**: `src/main/java/net/variantgenerator/mod/core/VariantRegistry.java`

Manages registration and tracking of generated variants.

#### Tier System:
```java
public enum ItemVariantTier {
    IRON(1.0f, "Iron"),           // Baseline
    NETHERITE(1.25f, "Netherite"), // 25% stronger
    ENDERITE(1.5f, "Enderite")     // 50% stronger
}
```

#### Stat Scaling:
- **Mining Speed**: multiplied by tier multiplier
- **Attack Damage**: multiplied by tier multiplier
- **Durability**: multiplied by tier multiplier
- **Armor**: multiplied by tier multiplier
- **Toughness**: multiplied by tier multiplier
- **Enchantability**: scaled with reduced impact

#### Usage:
```java
VariantRegistry registry = VariantGeneratorMod.getVariantRegistry();

// Create variant config
VariantConfig config = new VariantConfig(
    "modid",
    "iron_hammer",
    ItemVariantTier.ENDERITE
);

// Scale stats
ItemStats enderiteStats = VariantRegistry.scaleStats(
    ironStats,
    ItemVariantTier.ENDERITE
);

// Register
registry.registerVariant(config);
```

### 4. VariantGenerator (Orchestrator)
**Location**: `src/main/java/net/variantgenerator/mod/variant/VariantGenerator.java`

Coordinates the entire variant generation process.

#### Generation Pipeline:
1. Load reference textures (iron, netherite, enderite)
2. Scan for items to process (files with "iron" in name)
3. For each found item:
   - Load source texture
   - Generate netherite variant
   - Generate enderite variant
   - Register variants

#### Usage:
```java
VariantGenerator generator = VariantGeneratorMod.getVariantGenerator();
generator.scanAndGenerateVariants();  // Main entry point
```

### 5. RecipeScanner (Recipe Processing)
**Location**: `src/main/java/net/variantgenerator/mod/recipe/RecipeScanner.java`

Scans and generates recipes for variants.

#### Process:
1. Scan recipe JSON files from other mods
2. Identify recipes using iron items
3. Create variant recipes:
   - Replace iron items with netherite/enderite equivalents
   - Adjust crafting patterns if needed
   - Generate smithing recipes for upgrades

### 6. Configuration System
**Location**: `src/main/java/net/variantgenerator/mod/config/VariantGeneratorConfig.java`

Centralized configuration management.

#### Color Palettes:
```java
// Iron: Grayscale
IRON_COLORS.brightColor = (255, 255, 255)
IRON_COLORS.darkColor = (53, 53, 53)

// Netherite: Dark gray/purple
NETHERITE_COLORS.brightColor = (100, 100, 120)
NETHERITE_COLORS.darkColor = (40, 40, 50)

// Enderite: Cyan
ENDERITE_COLORS.brightColor = (29, 94, 83)
ENDERITE_COLORS.darkColor = (4, 14, 12)
```

#### Configurable Parameters:
- Stat multipliers
- Texture generation options
- Recipe generation templates
- Scanning patterns
- Excluded mods

### 7. Advanced Texture Processor
**Location**: `src/main/java/net/variantgenerator/mod/texture/AdvancedTextureProcessor.java`

Sophisticated color transformations for high-quality variant generation.

#### Features:
- HSL color space transformations
- Hue shifting
- Saturation adjustment
- Brightness/gamma correction
- Missing color generation through interpolation

#### Color Space Conversion:
- RGB ↔ HSL conversion with proper gamut mapping
- Gamma correction for realistic brightness scaling
- Interpolation-based color generation

## Data Flow

### Texture Generation Flow:
```
Source Texture (iron_*.png)
    │
    ▼
Analyze Grayscale Distribution
    │
    ▼
Load Reference Color Palette
    │
    ▼
Recolor Using Interpolation
    │
    ▼
Save Result (enderite_*.png)
    │
    ▼
Register Item & Create Model
```

### Item Registration Flow:
```
Scan File System
    │
    ├─→ Find "iron_*.png" files
    │
    ▼
Extract Mod ID & Item Name
    │
    ├─→ Use FileUtils for file operations
    │
    ▼
Create Variant Configs
    │
    ├─→ Set item statistics
    ├─→ Scale for each tier
    │
    ▼
Register with VariantRegistry
    │
    ├─→ Track by mod ID
    ├─→ Track by tier
    │
    ▼
Generate Models & Textures
    │
    ├─→ Create JSON models
    ├─→ Reference texture files
```

## Utility Systems

### FileUtils
```java
FileUtils.findFiles(directory, pattern)      // Find files matching pattern
FileUtils.findPNGFiles(directory)             // Find all PNGs
FileUtils.createDirectoryIfNeeded(dir)        // Ensure directory exists
FileUtils.copyFile(source, destination)       // Copy file
FileUtils.getFileExtension(file)              // Get file extension
FileUtils.deleteDirectory(directory)          // Recursive delete
```

### ItemUtils
```java
ItemUtils.isIronVariant(name)                 // Check if iron variant
ItemUtils.extractModId(registryName)          // Get mod ID
ItemUtils.toVariantName(name, tier)           // Convert to variant name
ItemUtils.humanizeName(itemName)              // snake_case → Title Case
ItemUtils.isValidTier(tier)                   // Validate tier name
```

## Integration Points

### NeoForge Integration
**Location**: `src/main/java/net/variantgenerator/mod/VariantGeneratorModNeoForge.java`

- Handles mod lifecycle events
- Initializes common systems
- Integrates with NeoForge event bus

### External Mod Compatibility
- Scans other mods' asset directories
- Respects existing item registries
- Reads recipe JSON files
- Generates compatible items

## Extension Points

### Adding New Color Palettes:
```java
public static final TierColors CUSTOM_TIER = new TierColors(
    "CustomTier",
    new Pixel(255, 200, 100, 255),  // Bright
    new Pixel(100, 50, 20, 255)      // Dark
);
```

### Custom Stat Scaling:
```java
StatScaling scaling = config.statScaling;
scaling.netheriteMultiplier = 1.5f;  // Custom scaling
```

### Advanced Texture Processing:
```java
BufferedImage processed = AdvancedTextureProcessor
    .transformHSL(source, hueShift, saturation, lightness);
```

## Performance Considerations

- **Lazy Loading**: Textures and models generated on-demand
- **Caching**: Reference textures cached in memory
- **Parallel Processing**: Could be extended for large batches
- **Memory Efficient**: Pixel-by-pixel processing without frame buffer

## Testing Recommendations

1. **Texture Processing**:
   - Test with various grayscale ranges
   - Verify color interpolation accuracy
   - Check alpha channel preservation

2. **Registry System**:
   - Test variant tracking by mod/tier
   - Verify stat scaling calculations
   - Check registration completeness

3. **Recipe Generation**:
   - Validate recipe JSON structure
   - Test ingredient substitution
   - Verify output item generation

4. **Integration**:
   - Test with multiple mods
   - Check for conflicts
   - Verify performance under load

## Future Enhancements

- [ ] GUI for configuration
- [ ] Batch texture processing
- [ ] Advanced recipe templates
- [ ] Custom stat templates per item type
- [ ] Texture preview system
- [ ] Quality presets (fast/balanced/quality)
- [ ] Support for other material tiers
