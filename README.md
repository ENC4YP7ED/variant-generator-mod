# Variant Generator Mod

[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.24%2B-orange?logo=neoforged)](https://neoforged.net/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen?logo=minecraft)](https://minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-red?logo=java)](https://www.java.com/)
[![GitHub License](https://img.shields.io/github/license/ENC4YP7ED/variant-generator-mod?color=green)](LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/ENC4YP7ED/variant-generator-mod?color=yellow)](https://github.com/ENC4YP7ED/variant-generator-mod/stargazers)

An intelligent Minecraft mod for NeoForge 1.21.1 that automatically generates netherite and enderite variants for items from other mods. Uses advanced mixin-based integration with the Enderite mod to extract colors and statistics for perfectly balanced variants.

## Features

- **Automatic Scanning**: Detects items with iron variants from other mods
- **Texture Colorization**: Converts grayscale textures to colored variants using Enderite color palettes
- **Smart Stats Scaling**: Adjusts tool/armor statistics based on material tier
- **Recipe Generation**: Automatically creates crafting recipes for new variants
- **Multi-Tier Support**: Creates both Netherite and Enderite variants

## How It Works

1. **Texture Processing**: The mod scans for PNG files containing "iron" in the name
2. **Color Extraction**: Analyzes reference Enderite ingot textures to extract color palettes
3. **Recoloring**: Converts grayscale iron textures to Netherite (dark gray) and Enderite (cyan) colors
4. **Item Registration**: Creates new item variants with scaled statistics
5. **Recipe Creation**: Generates crafting and smithing recipes for the new items

## Architecture

```
variantgenerator/
├── core/               # Core variant registry and configuration
├── texture/            # Texture colorization system (Java equivalent of Python code)
├── variant/            # Item variant generation logic
├── recipe/             # Recipe scanning and generation
├── registry/           # Item and block registry
└── util/               # Utility functions
```

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.24+
- Architectury API 15.0.0+
- Java 21+

## Building

```bash
./gradlew build
```

The compiled mod JAR will be in `build/libs/`

## Installation

1. Download the mod JAR
2. Place it in your `.minecraft/mods` folder
3. Launch Minecraft with NeoForge

## Configuration

The mod behavior can be configured through:
- Texture reference paths
- Stat scaling multipliers
- Recipe generation templates

## Color Reference

- **Iron**: Grayscale (53-255)
- **Netherite**: Dark gray/purple (~40-100)
- **Enderite**: Dark cyan (~4-29)

## Technical Details

### Texture Colorization Algorithm

The mod implements a grayscale-to-color mapping algorithm:

1. Analyze grayscale image to find min/max brightness
2. Load reference color image (e.g., Enderite ingot)
3. For each grayscale pixel:
   - Extract brightness value (0-255)
   - Normalize to 0-1 range
   - Interpolate between dark and bright reference colors
   - Apply the interpolated color to the pixel

### Item Stats Scaling

Stats are scaled using tier multipliers:
- Iron: 1.0x (baseline)
- Netherite: 1.25x
- Enderite: 1.5x

## Contributing

Feel free to submit issues and pull requests!

## License

MIT License

## Quick Start

### For Users
1. Download the latest mod JAR from [Releases](https://github.com/ENC4YP7ED/variant-generator-mod/releases)
2. Place in `.minecraft/mods` folder
3. Launch Minecraft with NeoForge 1.21.1+
4. The mod will automatically:
   - Scan for iron items from other mods
   - Generate netherite and enderite variants
   - Create textures and recipes

### For Developers
1. Clone the repository
2. Run `./gradlew build` to compile
3. Run `./gradlew runClient` to test
4. See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed design

## Documentation

### Getting Started
- **[INSTALLATION.md](INSTALLATION.md)** - Installation guide with 3 methods (CurseForge, manual, source)
- **[Quick Start](#quick-start)** - Below, for immediate setup
- **[FEATURES.md](FEATURES.md)** - Complete feature list and capabilities

### Integration & Examples
- **[INTEGRATION_EXAMPLE.md](INTEGRATION_EXAMPLE.md)** - How to integrate with your mod
- **[ENDERITE_INTEGRATION.md](ENDERITE_INTEGRATION.md)** - Detailed Enderite mod integration

### Technical Documentation
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System design and component descriptions (700+ lines)
- **[MIXINS.md](MIXINS.md)** - Mixin system and code injection details (500+ lines)

### Development & Testing
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Development guide and common tasks
- **[TESTING.md](TESTING.md)** - Testing framework and guidelines
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Contribution guidelines
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and planned features

## Support

For issues and questions, please open an issue on GitHub.
