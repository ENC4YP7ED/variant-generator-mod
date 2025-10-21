package net.variantgenerator.mod.recipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Scans recipes from other mods and generates variants
 * Analyzes how items are crafted and creates corresponding recipes for variants
 */
public class RecipeScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Recipe");

    /**
     * Represents a scanned recipe
     */
    public static class ScannedRecipe {
        public String modId;
        public String recipeId;
        public String recipeType;
        public String baseItem;
        public List<String> ingredients;
        public String output;

        public ScannedRecipe(String modId, String recipeId) {
            this.modId = modId;
            this.recipeId = recipeId;
            this.ingredients = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("Recipe{mod=%s, id=%s, type=%s}", modId, recipeId, recipeType);
        }
    }

    /**
     * Generated variant recipe
     */
    public static class VariantRecipe {
        public String originalRecipeId;
        public String variantRecipeId;
        public String tier;
        public List<String> variantIngredients;
        public String variantOutput;

        public VariantRecipe(String originalRecipeId, String tier) {
            this.originalRecipeId = originalRecipeId;
            this.tier = tier;
            this.variantIngredients = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("VariantRecipe{original=%s, tier=%s}", originalRecipeId, tier);
        }
    }

    private final Map<String, ScannedRecipe> scannedRecipes = new HashMap<>();
    private final Map<String, List<VariantRecipe>> generatedRecipes = new HashMap<>();

    /**
     * Scans for recipes involving iron items
     */
    public void scanForIronRecipes() {
        LOGGER.info("Scanning for iron-related recipes");

        // In a real implementation, this would:
        // 1. Load recipe JSON files from mods
        // 2. Parse the JSON and extract recipe information
        // 3. Identify recipes that use iron items
        // 4. Store them for variant generation

        // For now, this is a stub that documents the process
    }

    /**
     * Generates variant recipes from scanned recipes
     */
    public List<VariantRecipe> generateVariantRecipes(ScannedRecipe originalRecipe, String variantTier) {
        LOGGER.debug("Generating {} variant recipes for: {}", variantTier, originalRecipe.recipeId);

        List<VariantRecipe> variants = new ArrayList<>();

        // Generate netherite variant
        VariantRecipe netheriteVariant = createVariantRecipe(originalRecipe, "netherite");
        variants.add(netheriteVariant);

        // Generate enderite variant
        VariantRecipe enderiteVariant = createVariantRecipe(originalRecipe, "enderite");
        variants.add(enderiteVariant);

        return variants;
    }

    /**
     * Creates a single variant recipe
     */
    private VariantRecipe createVariantRecipe(ScannedRecipe original, String tier) {
        VariantRecipe variant = new VariantRecipe(original.recipeId, tier);
        variant.variantRecipeId = original.recipeId.replace("iron", tier);

        // Transform ingredients
        for (String ingredient : original.ingredients) {
            if (ingredient.toLowerCase().contains("iron")) {
                // Replace iron with variant material
                String variantIngredient = ingredient.replace("iron", tier);
                variant.variantIngredients.add(variantIngredient);
            } else {
                // Keep non-iron ingredients unchanged
                variant.variantIngredients.add(ingredient);
            }
        }

        // Transform output
        variant.variantOutput = original.output.replace("iron", tier);

        LOGGER.debug("Created variant recipe: {}", variant);
        return variant;
    }

    /**
     * Registers a scanned recipe
     */
    public void registerScannedRecipe(ScannedRecipe recipe) {
        String key = recipe.modId + ":" + recipe.recipeId;
        scannedRecipes.put(key, recipe);
        LOGGER.debug("Registered scanned recipe: {}", recipe);
    }

    /**
     * Gets a scanned recipe
     */
    public ScannedRecipe getScannedRecipe(String modId, String recipeId) {
        return scannedRecipes.get(modId + ":" + recipeId);
    }

    /**
     * Gets all scanned recipes for a mod
     */
    public Collection<ScannedRecipe> getScannedRecipesForMod(String modId) {
        return scannedRecipes.values().stream()
                .filter(recipe -> recipe.modId.equals(modId))
                .toList();
    }

    /**
     * Stores generated variant recipes
     */
    public void storeVariantRecipe(String originalRecipeId, VariantRecipe variantRecipe) {
        generatedRecipes.computeIfAbsent(originalRecipeId, k -> new ArrayList<>())
                .add(variantRecipe);
        LOGGER.debug("Stored variant recipe: {}", variantRecipe);
    }

    /**
     * Gets variant recipes for an original recipe
     */
    public List<VariantRecipe> getVariantRecipes(String originalRecipeId) {
        return generatedRecipes.getOrDefault(originalRecipeId, new ArrayList<>());
    }

    /**
     * Gets all variant recipes
     */
    public Collection<VariantRecipe> getAllVariantRecipes() {
        List<VariantRecipe> all = new ArrayList<>();
        generatedRecipes.values().forEach(all::addAll);
        return all;
    }

    /**
     * Gets the number of scanned recipes
     */
    public int getScannedRecipeCount() {
        return scannedRecipes.size();
    }

    /**
     * Gets the number of generated variant recipes
     */
    public int getVariantRecipeCount() {
        return (int) generatedRecipes.values().stream()
                .mapToLong(List::size)
                .sum();
    }

    /**
     * Clears all data
     */
    public void clear() {
        scannedRecipes.clear();
        generatedRecipes.clear();
        LOGGER.info("Cleared recipe scanner data");
    }
}
