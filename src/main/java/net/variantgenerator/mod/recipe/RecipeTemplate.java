package net.variantgenerator.mod.recipe;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Template system for generating recipes
 * Provides standardized patterns for creating variant recipes
 */
public class RecipeTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-RecipeTemplate");

    /**
     * Recipe template types
     */
    public enum TemplateType {
        SHAPED_CRAFTING("minecraft:crafting_shaped"),
        SHAPELESS_CRAFTING("minecraft:crafting_shapeless"),
        SMELTING("minecraft:smelting"),
        BLASTING("minecraft:blasting"),
        SMOKING("minecraft:smoking"),
        SMITHING("minecraft:smithing_transform"),
        STONECUTTING("minecraft:stonecutting");

        public final String recipeType;

        TemplateType(String recipeType) {
            this.recipeType = recipeType;
        }
    }

    /**
     * Template metadata
     */
    public static class Template {
        public String templateId;
        public TemplateType type;
        public String description;
        public Map<String, String> variables;

        public Template(String id, TemplateType type) {
            this.templateId = id;
            this.type = type;
            this.variables = new HashMap<>();
        }

        @Override
        public String toString() {
            return String.format("Template{id=%s, type=%s}", templateId, type);
        }
    }

    private static final Map<String, Template> TEMPLATES = new HashMap<>();

    static {
        // Initialize default templates
        Template smithingTemplate = new Template("upgrade_template", TemplateType.SMITHING);
        smithingTemplate.description = "Smithing table upgrade from netherite to enderite";
        TEMPLATES.put("upgrade_template", smithingTemplate);

        Template toolTemplate = new Template("tool_crafting", TemplateType.SHAPED_CRAFTING);
        toolTemplate.description = "Tool crafting template";
        TEMPLATES.put("tool_crafting", toolTemplate);

        Template armorTemplate = new Template("armor_crafting", TemplateType.SHAPED_CRAFTING);
        armorTemplate.description = "Armor crafting template";
        TEMPLATES.put("armor_crafting", armorTemplate);

        LOGGER.debug("Initialized {} recipe templates", TEMPLATES.size());
    }

    /**
     * Gets a template
     */
    public static Template getTemplate(String templateId) {
        return TEMPLATES.get(templateId);
    }

    /**
     * Registers a custom template
     */
    public static void registerTemplate(String templateId, Template template) {
        TEMPLATES.put(templateId, template);
        LOGGER.debug("Registered template: {}", templateId);
    }

    /**
     * Creates a smithing recipe JSON
     */
    public static JsonObject createSmithingRecipe(String templateId, String baseItem, String additionItem, String resultItem) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", TemplateType.SMITHING.recipeType);
        recipe.addProperty("template", templateId);

        JsonObject baseObj = new JsonObject();
        baseObj.addProperty("item", baseItem);
        recipe.add("base", baseObj);

        JsonObject additionObj = new JsonObject();
        additionObj.addProperty("item", additionItem);
        recipe.add("addition", additionObj);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", resultItem);
        recipe.add("result", resultObj);

        LOGGER.debug("Created smithing recipe: {} → {}", baseItem, resultItem);
        return recipe;
    }

    /**
     * Creates a crafting recipe JSON
     */
    public static JsonObject createCraftingRecipe(String pattern, Map<String, String> ingredients, String resultItem, int count) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", TemplateType.SHAPED_CRAFTING.recipeType);

        // Add pattern
        List<String> patternList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i < pattern.length() / 3) {
                patternList.add(pattern.substring(i * 3, (i + 1) * 3));
            }
        }
        recipe.add("pattern", new com.google.gson.JsonArray(patternList.stream().map(com.google.gson.JsonPrimitive::new).toArray(com.google.gson.JsonElement[]::new)));

        // Add key (ingredients)
        JsonObject keyObj = new JsonObject();
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
            JsonObject itemObj = new JsonObject();
            itemObj.addProperty("item", entry.getValue());
            keyObj.add(entry.getKey(), itemObj);
        }
        recipe.add("key", keyObj);

        // Add result
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", resultItem);
        resultObj.addProperty("count", count);
        recipe.add("result", resultObj);

        LOGGER.debug("Created crafting recipe: {}", resultItem);
        return recipe;
    }

    /**
     * Gets all templates
     */
    public static Collection<Template> getAllTemplates() {
        return TEMPLATES.values();
    }

    /**
     * Gets template count
     */
    public static int getTemplateCount() {
        return TEMPLATES.size();
    }
}
