# Contributing to Variant Generator Mod

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing.

## Code of Conduct

Be respectful, inclusive, and professional in all interactions.

## Getting Started

### Prerequisites
- Java 21+
- Gradle 8.0+
- Git
- IDE (IntelliJ IDEA recommended, but any Java IDE works)

### Setting Up Development Environment

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR-USERNAME/variant-generator-mod.git
   cd variant-generator-mod
   ```

2. **Import into IDE**
   - IntelliJ IDEA: Open the project root
   - VS Code: Install Extension Pack for Java
   - Eclipse: Import as Gradle project

3. **Build and Test**
   ```bash
   ./gradlew build
   ./gradlew runClient
   ```

## Development Workflow

### 1. Create a Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 2. Make Your Changes

**Code Style**
- Follow Java conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and small (< 50 lines ideal)

**Documentation**
- Add Javadoc comments for public methods
- Document complex algorithms
- Update README if adding user-facing features
- Add examples in FEATURES.md if appropriate

### 3. Testing

**Run Tests**
```bash
./gradlew test
```

**Manual Testing**
1. Start test client: `./gradlew runClient`
2. Test variant generation
3. Verify textures generated correctly
4. Check recipes in crafting menu
5. Test with different mods

### 4. Commit Changes

**Commit Message Format**
```
[Type] Brief description

Longer explanation if needed. Reference any related issues with #123.
```

**Types**
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code restructuring
- `docs`: Documentation
- `test`: Tests
- `perf`: Performance improvement
- `style`: Code style changes

**Example**
```
feat: Add HSL color transformation support

Implements advanced color space conversion for better texture quality.
Users can now configure hue, saturation, and lightness adjustments.
Fixes #42
```

### 5. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a PR on GitHub with:
- Clear title describing the change
- Description of what changed and why
- Reference to any related issues
- Screenshots if UI changes
- Performance impact if applicable

## Areas for Contribution

### High Priority
- [ ] GUI configuration system
- [ ] Additional color space support (CMYK, Lab)
- [ ] Performance optimizations
- [ ] Unit tests for core systems
- [ ] Error handling improvements

### Medium Priority
- [ ] Documentation improvements
- [ ] Example configuration files
- [ ] Additional texture quality presets
- [ ] Integration tests
- [ ] Logging improvements

### Low Priority
- [ ] Code style improvements
- [ ] Comment additions
- [ ] README updates
- [ ] Translations

## Pull Request Guidelines

### Before Submitting
- [ ] Code builds successfully
- [ ] No obvious bugs or issues
- [ ] Follows code style guidelines
- [ ] Added relevant tests
- [ ] Updated documentation
- [ ] Commit messages are clear
- [ ] PR description is informative

### What We Look For
- Code quality and clarity
- Test coverage
- Performance impact
- Documentation completeness
- Compatibility with existing code
- Creative problem-solving

### Review Process
1. Maintainers review your PR
2. Changes may be requested
3. Address feedback with new commits
4. Re-review if significant changes
5. Merge when approved

## Reporting Bugs

**Before Reporting**
- Check existing issues
- Try latest version
- Provide reproduction steps

**Issue Template**
```
## Description
Brief description of the bug

## Steps to Reproduce
1. Do this
2. Then this
3. And this

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Screenshots
If applicable

## Environment
- Mod version: X.X.X
- Minecraft: 1.21.1
- NeoForge: version
- Other mods: list them
```

## Feature Requests

**Good Feature Requests Include**
- Clear description of the feature
- Use cases and benefits
- Possible implementation approach
- Examples or mockups if applicable

## Architecture Decisions

When making significant changes:
1. Discuss in an issue first
2. Consider impact on existing code
3. Update architecture documentation
4. Add clear examples

## Performance Considerations

When optimizing:
- Measure before and after
- Include benchmarks in PR
- Consider memory vs speed tradeoffs
- Profile with realistic data

## Documentation Standards

### Code Comments
```java
// For complex algorithms or non-obvious logic
// Explain the WHY, not the WHAT

// For todo items
// TODO: Description of work needed - #123

// For warnings
// WARNING: Important caveat or gotcha
```

### Javadoc
```java
/**
 * Brief description.
 *
 * Longer description with details about behavior,
 * edge cases, and usage patterns.
 *
 * @param param1 Description of param1
 * @param param2 Description of param2
 * @return Description of return value
 * @throws IOException If IO error occurs
 */
public void methodName(String param1, String param2) throws IOException {
    // ...
}
```

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

- Open an issue for questions
- Check existing discussions
- Review documentation
- Ask on Discord (if available)

## Recognition

Contributors will be:
- Listed in README
- Credited in release notes
- Added to contributors list

Thank you for contributing to Variant Generator Mod!
