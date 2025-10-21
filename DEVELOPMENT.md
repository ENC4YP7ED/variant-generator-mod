# Development Guide

This guide explains how to work with the Variant Generator Mod codebase.

## Project Structure

```
variant-generator-mod/
├── src/main/java/net/variantgenerator/mod/
│   ├── VariantGeneratorMod.java              # Main entry point
│   ├── VariantGeneratorModNeoForge.java      # NeoForge integration
│   ├── core/
│   │   └── VariantRegistry.java              # Variant registration
│   ├── texture/
│   │   ├── TextureColorizer.java             # Texture processing
│   │   └── AdvancedTextureProcessor.java     # Advanced transforms
│   ├── variant/
│   │   └── VariantGenerator.java             # Main generator
│   ├── recipe/
│   │   └── RecipeScanner.java                # Recipe processing
│   ├── registry/
│   │   └── ItemModelBuilder.java             # Model generation
│   ├── config/
│   │   └── VariantGeneratorConfig.java       # Configuration
│   └── util/
│       ├── FileUtils.java                    # File operations
│       └── ItemUtils.java                    # Item utilities
├── src/main/resources/
│   ├── META-INF/
│   │   └── neoforge.mods.toml               # Mod metadata
│   ├── assets/variantgenerator/
│   │   └── lang/en_us.json                  # Translations
│   └── log4j2.xml                           # Logging config
├── build.gradle                              # Build config
├── gradle.properties                         # Gradle settings
├── README.md                                 # User guide
├── ARCHITECTURE.md                           # System design
├── FEATURES.md                               # Feature list
├── CONTRIBUTING.md                           # Contribution guide
├── DEVELOPMENT.md                            # This file
└── LICENSE                                   # MIT License
```

## Building

### Development Build
```bash
./gradlew build
```

### Run Client for Testing
```bash
./gradlew runClient
```

### Run Server for Testing
```bash
./gradlew runServer
```

### Clean Build
```bash
./gradlew clean build
```

## Common Development Tasks

### Adding a New Texture Processing Feature

1. **Add method to TextureColorizer.java**
   ```java
   public BufferedImage processTexture(BufferedImage source, float intensity) {
       // Implementation
       return result;
   }
   ```

2. **Or extend AdvancedTextureProcessor.java** if it's complex
   ```java
   public static BufferedImage advancedProcessing(BufferedImage source,
                                                   Configuration config) {
       // Implementation
       return result;
   }
   ```

3. **Update VariantGenerator to use it**
   ```java
   BufferedImage processed = AdvancedTextureProcessor
       .advancedProcessing(texture, config);
   ```

4. **Add tests** (create `src/test/java/...`)

### Adding Configuration Options

1. **Add to VariantGeneratorConfig.java**
   ```java
   public static class MyConfig {
       public float myValue = 1.0f;
       public boolean myFlag = false;
   }

   public MyConfig myConfig = new MyConfig();
   ```

2. **Use in code**
   ```java
   VariantGeneratorConfig config = new VariantGeneratorConfig();
   float value = config.myConfig.myValue;
   ```

### Adding a New Material Tier

1. **Update VariantRegistry.java**
   ```java
   public enum ItemVariantTier {
       IRON(1.0f, "Iron"),
       NETHERITE(1.25f, "Netherite"),
       ENDERITE(1.5f, "Enderite"),
       MYTHRIL(1.75f, "Mythril"),  // NEW
   }
   ```

2. **Add colors to VariantGeneratorConfig.java**
   ```java
   public static final TierColors MYTHRIL_COLORS = new TierColors(
       "Mythril",
       new Pixel(200, 180, 220, 255),  // Bright
       new Pixel(100, 80, 120, 255)    // Dark
   );
   ```

3. **Update color mapping**
   ```java
   public static TierColors getColorForTier(String tierName) {
       return switch (tierName.toLowerCase()) {
           // ... existing cases ...
           case "mythril" -> MYTHRIL_COLORS;
           default -> IRON_COLORS;
       };
   }
   ```

### Debugging

**Enable Debug Logging**
- Modify `log4j2.xml` to change log levels to DEBUG
- Or add programmatically:
  ```java
  LOGGER.debug("Debug message: {}", variable);
  ```

**Common Debug Points**
```java
// In TextureColorizer
LOGGER.debug("Pixel: {} -> {}", from, to);

// In VariantGenerator
LOGGER.debug("Generating variants for: {}", itemName);

// In VariantRegistry
LOGGER.debug("Registered variant: {}", config);
```

**Inspection Points**
1. Check texture file I/O
2. Verify color extraction from reference
3. Test interpolation calculation
4. Validate registry tracking

## Testing Guidelines

### Unit Testing Pattern
```java
@Test
void testColorInterpolation() {
    Pixel start = new Pixel(100, 100, 100, 255);
    Pixel end = new Pixel(0, 0, 0, 255);

    // Test at t=0 (should be end color)
    // Test at t=1 (should be start color)
    // Test at t=0.5 (should be midpoint)

    assertEquals(50, interpolated.r);
}
```

### Integration Testing Pattern
```java
@Test
void testVariantGeneration() {
    VariantGenerator generator = new VariantGenerator(
        new VariantRegistry(),
        new TextureColorizer()
    );

    // Generate variants
    generator.scanAndGenerateVariants();

    // Verify results
    assertTrue(registeredVariantCount > 0);
}
```

### Manual Testing Checklist
- [ ] Mod loads without errors
- [ ] Textures generate correctly
- [ ] Recipes appear in recipe book
- [ ] Items have correct stats
- [ ] No console errors
- [ ] No memory leaks
- [ ] Performance acceptable

## Performance Profiling

### Using JProfiler or YourKit
1. Run with profiler attached
2. Generate variants
3. Check for:
   - Memory leaks
   - Hot spots
   - Inefficient allocations
   - Lock contention

### Optimization Checklist
- [ ] Minimize object allocation
- [ ] Cache frequently used objects
- [ ] Use efficient collections
- [ ] Avoid unnecessary loops
- [ ] Profile before optimizing

## Code Review Checklist

When reviewing PRs:
- [ ] Code follows style guidelines
- [ ] Logic is correct
- [ ] No obvious bugs
- [ ] Performance acceptable
- [ ] Documentation adequate
- [ ] Tests included/passing
- [ ] No breaking changes
- [ ] Clear commit messages

## Versioning

Version format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes, significant new features
- **MINOR**: New features, non-breaking improvements
- **PATCH**: Bug fixes, documentation

Update in:
1. `gradle.properties` (`mod_version`)
2. GitHub releases
3. CHANGELOG.md (when created)

## Dependencies

### Gradle Dependencies
```gradle
// In build.gradle
modImplementation "net.neoforged:neoforge:${neoforge_version}"
modImplementation "dev.architectury:architectury-neoforge:${architectury_api_version}"
```

### Minecraft/NeoForge Versions
- Minecraft: 1.21.1
- NeoForge: 21.1.24+
- Java: 21+
- Gradle: 8.0+

## Troubleshooting

### Build Fails
```bash
# Clear gradle cache
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies build
```

### IDE Issues
- Rebuild IDE project cache
- Reimport Gradle project
- Check Java version (should be 21)

### Runtime Issues
- Check log4j2.xml logging
- Verify textures in file system
- Test with minimal mod set
- Check for mod conflicts

## Useful Resources

- [NeoForge Documentation](https://docs.neoforged.net/)
- [Architectury API Docs](https://docs.architectury.dev/)
- [Minecraft Wiki](https://minecraft.wiki/)
- [Java Documentation](https://docs.oracle.com/en/java/)

## Contributing Back

1. Fork repository
2. Create feature branch
3. Make changes with tests
4. Submit pull request
5. Address review feedback
6. Merge and celebrate!

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## Questions?

- Open an issue
- Check existing issues/discussions
- Review architecture docs
- Look at similar implementations

## Release Checklist

When preparing a release:
- [ ] Update version in gradle.properties
- [ ] Update CHANGELOG.md
- [ ] Run full test suite
- [ ] Build release JAR
- [ ] Create GitHub release
- [ ] Tag git commit
- [ ] Update README with new version
- [ ] Announce on relevant platforms
