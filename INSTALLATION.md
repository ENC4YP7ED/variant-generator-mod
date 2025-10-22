# Installation & Setup Guide

Complete guide for installing and setting up the Variant Generator Mod.

## System Requirements

- **Minecraft Version:** 1.21.1
- **Java:** 21 or higher
- **Loader:** NeoForge 21.1.24+
- **RAM:** 2GB minimum (4GB recommended)
- **Disk Space:** 500MB

## Installation Methods

### Method 1: CurseForge (Recommended)

1. **Download CurseForge Launcher**
   - Visit [curseforge.com](https://www.curseforge.com)
   - Download and install CurseForge launcher

2. **Create Minecraft Instance**
   - Click "Create Custom Profile"
   - Select Minecraft 1.21.1
   - Select NeoForge as loader
   - Set NeoForge version 21.1.24+

3. **Add Variant Generator**
   - Search for "Variant Generator"
   - Click "Install"
   - Wait for download to complete

4. **Add Enderite Mod** (Required)
   - Search for "Enderite Mod"
   - Click "Install"
   - Ensure version 1.7.0+ is selected

5. **Launch Game**
   - Click "Play"
   - Wait for mods to load
   - Verify in mod list (should show both mods)

### Method 2: Manual Installation

1. **Download Files**
   - Download `variantgenerator-1.0.0.jar` from GitHub Releases
   - Download Enderite Mod JAR (required)

2. **Locate Mods Folder**
   ```
   Windows:   %APPDATA%\.minecraft\mods
   macOS:     ~/Library/Application Support/minecraft/mods
   Linux:     ~/.minecraft/mods
   ```

3. **Copy JAR Files**
   - Copy variant-generator JAR to mods folder
   - Copy Enderite Mod JAR to mods folder

4. **Verify Installation**
   - Start Minecraft with NeoForge
   - Check mod list in main menu
   - Both mods should appear

### Method 3: From Source

**For Developers:**

```bash
# Clone repository
git clone https://github.com/ENC4YP7ED/variant-generator-mod.git
cd variant-generator-mod

# Build mod
./gradlew build

# Built JAR location
# build/libs/variantgenerator-1.0.0.jar

# Copy to mods folder
cp build/libs/variantgenerator-1.0.0.jar ~/.minecraft/mods/
```

## Dependency Installation

### Required Mods

| Mod | Version | Required | Source |
|-----|---------|----------|--------|
| NeoForge | 21.1.24+ | ✓ Yes | neoforged.net |
| Architectury API | 15.0.0+ | ✓ Yes | CurseForge |
| Enderite Mod | 1.7.0+ | ✓ Yes | GitHub |

### Optional Mods

| Mod | Purpose |
|-----|---------|
| Cloth Config API | Configuration UI |
| Mod Menu | Access mod configs |

## Configuration

### Default Configuration

The mod works with defaults. No configuration needed for basic functionality.

### Manual Configuration (Advanced)

Create `config/variantgenerator/config.json`:

```json
{
  "textureConfig": {
    "textureSize": 16,
    "generateMipmaps": true,
    "outputFormat": "PNG"
  },
  "statScaling": {
    "netheriteMultiplier": 1.25,
    "enderiteMultiplier": 1.5
  },
  "recipeConfig": {
    "generateSmithingRecipes": true,
    "generateCraftingRecipes": true
  },
  "scanningConfig": {
    "enableMissingTextureGeneration": true,
    "scanSubdirectories": true
  }
}
```

## Verification

### Startup Verification

Check Minecraft logs for successful initialization:

```
[INFO] Loading Variant Generator Mod
[INFO] Initializing Variant Generator Mod
[INFO] Starting variant generation process
[INFO] Variant generation complete
```

### In-Game Verification

1. **Check Creative Menu**
   - Look for Variant Generator tab
   - Should contain generated variants

2. **Check Recipes**
   - Open recipe book
   - Search for "enderite" or "netherite"
   - Should show variant recipes

3. **Test Crafting**
   ```
   /give @s minecraft:iron_ingot 64
   # Craft iron tools
   # Use smithing table to upgrade to variants
   ```

## Troubleshooting

### Mod Not Loading

**Symptom:** "Variant Generator" missing from mod list

**Solutions:**
1. Verify JAR is in mods folder
2. Check Minecraft version (must be 1.21.1)
3. Verify NeoForge is installed
4. Check file isn't corrupted (re-download)
5. Check logs for error messages

### Enderite Dependency Missing

**Symptom:** Mod crashes with "Enderite mod not found"

**Solution:**
```
[ERROR] Missing required mod: enderitemod [1.7.0,)
```

**Fix:**
1. Install Enderite Mod 1.7.0 or higher
2. Place in same mods folder
3. Restart Minecraft

### Variants Not Generating

**Symptom:** No netherite/enderite items in creative menu

**Solutions:**
1. Check logs for generation errors
2. Verify mod loaded successfully
3. Check if other mods have iron items
4. Try creating new world
5. Check disk space (needs ~100MB)

### Performance Issues

**Symptom:** Lag during mod startup or item generation

**Solutions:**
1. Allocate more RAM (4GB+ recommended)
2. Disable other mods temporarily
3. Check disk speed (use SSD if possible)
4. Wait longer for initial generation

### Color/Texture Issues

**Symptom:** Generated variants have wrong colors

**Solutions:**
1. Verify Enderite mod is loaded
2. Check Enderite color cache in logs
3. Verify source textures are grayscale
4. Check texture file format (must be PNG)

## Uninstallation

### Remove Mod

```bash
# Locate mods folder
cd ~/.minecraft/mods/  # Linux/macOS
cd %APPDATA%\.minecraft\mods\  # Windows

# Remove JAR files
rm variantgenerator-*.jar
rm enderitemod-*.jar  # Optional
```

### Clear Configuration

```bash
# Optional: Remove generated config
rm -rf config/variantgenerator/
```

## Updating

### Check for Updates

1. Visit [GitHub Releases](https://github.com/ENC4YP7ED/variant-generator-mod/releases)
2. Check if new version available
3. Review changelog for breaking changes

### Update Procedure

1. **Backup World** (Important!)
   ```bash
   cp -r ~/.minecraft/saves/YourWorld ~/YourWorld.backup
   ```

2. **Remove Old Mod**
   ```bash
   rm ~/.minecraft/mods/variantgenerator-*.jar
   ```

3. **Install New Version**
   - Download new JAR
   - Place in mods folder

4. **Load World**
   - Variants may be regenerated with new settings
   - This is normal

5. **Verify**
   - Check logs for errors
   - Verify items still work

## Command Line Arguments

### For Debugging

```bash
# Run with debug logging
java -jar launcher.jar --debug

# Run with verbose output
java -jar launcher.jar --verbose

# Run with specific memory
java -Xmx4G -Xms2G -jar launcher.jar
```

## Common Issues

### Issue: "javafxmod" not recognized

**Error:** `modLoader="javafxmod" not recognized`

**Solution:**
```toml
# Change in neoforge.mods.toml to:
modLoader="neoforge"
loaderVersion="[21,)"
```

### Issue: Mixin errors

**Error:** `Failed to apply mixin...`

**Solution:**
1. Verify Java 21+
2. Check mixin configuration
3. Try fresh install
4. Check mod conflicts

### Issue: Out of Memory

**Error:** `java.lang.OutOfMemoryError: Java heap space`

**Solution:**
- Allocate more RAM in launcher settings
- Disable other mods temporarily
- Clear cache: `Delete Contents` in launcher

## Performance Tuning

### Recommended Settings

**For Low-End PC (2GB RAM):**
```
- Disable texture generation mipmaps
- Reduce render distance
- Disable other heavy mods
```

**For Mid-Range PC (4GB RAM):**
```
- Default settings work fine
- Can use texture generation
- 2-3 other mods safe
```

**For High-End PC (8GB+ RAM):**
```
- All features enabled
- Many mods can be loaded
- No performance concerns
```

### JVM Arguments

Add to launcher settings:

```
-Xmx4G -Xms2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

## Getting Help

### Where to Get Support

1. **GitHub Issues** (Preferred)
   - https://github.com/ENC4YP7ED/variant-generator-mod/issues
   - Report bugs with logs

2. **Documentation**
   - Check TROUBLESHOOTING section
   - Read ARCHITECTURE.md
   - Review FEATURES.md

3. **Logs Location**
   - `.minecraft/logs/latest.log`
   - Include full error trace

### Providing Good Bug Reports

Include:
1. Minecraft version
2. NeoForge version
3. Complete error log
4. Steps to reproduce
5. Expected vs actual behavior

## Advanced Setup

### Development Installation

```bash
# Setup development environment
./gradlew idea  # For IntelliJ
./gradlew eclipse  # For Eclipse

# Run in development
./gradlew runClient

# Debug mode
./gradlew runClient --debug-jvm
```

### Custom Texture Paths

To use custom texture locations:

```java
// In your mod integration
System.setProperty("variant.texture.path", "/custom/path");
```

## FAQ

**Q: Will variants be created for all mods?**
A: Only for items with "iron" in the name.

**Q: Can I disable variant generation?**
A: Not currently, but can skip specific items by renaming them.

**Q: Do I need Enderite mod?**
A: Yes, it's required. Variants use Enderite colors and stats.

**Q: Will variants appear in old worlds?**
A: Variants are created on mod load. Old worlds need reload.

**Q: Can I customize variant colors?**
A: Yes, through configuration (see advanced setup).

**Q: Is this mod multiplayer compatible?**
A: Yes, works on servers and clients.

## Next Steps

After installation:

1. **Read Documentation**
   - [FEATURES.md](FEATURES.md) - What the mod does
   - [ARCHITECTURE.md](ARCHITECTURE.md) - How it works
   - [ENDERITE_INTEGRATION.md](ENDERITE_INTEGRATION.md) - Enderite integration

2. **Try It Out**
   - Launch world with both mods
   - Check generated variants
   - Test crafting recipes

3. **Integrate with Your Mods**
   - See [INTEGRATION_EXAMPLE.md](INTEGRATION_EXAMPLE.md)
   - Follow naming conventions
   - Create grayscale textures

4. **Report Issues**
   - If problems occur
   - Check logs first
   - Open GitHub issue

## Support

For help, questions, or to report issues:
- **GitHub:** https://github.com/ENC4YP7ED/variant-generator-mod/issues
- **Documentation:** Check README.md and ARCHITECTURE.md
