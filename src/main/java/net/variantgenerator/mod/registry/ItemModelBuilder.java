package net.variantgenerator.mod.registry;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates item model JSON files for variants
 */
public class ItemModelBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-ModelBuilder");

    /**
     * Creates a simple item model JSON
     */
    public static JsonObject createSimpleItemModel(String texturePath) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texturePath);

        model.add("textures", textures);
        return model;
    }

    /**
     * Creates a handheld item model JSON (for tools)
     */
    public static JsonObject createHandheldItemModel(String texturePath) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "item/handheld");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texturePath);

        model.add("textures", textures);
        return model;
    }

    /**
     * Saves a model to a JSON file
     */
    public static boolean saveModel(JsonObject model, File outputFile) {
        try {
            outputFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(model.toString());
            }

            LOGGER.debug("Saved model: {}", outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LOGGER.error("Error saving model: {}", outputFile.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Generates model file path from item name
     */
    public static String generateModelPath(String modId, String itemName) {
        return String.format("assets/%s/models/item/%s.json", modId, itemName);
    }

    /**
     * Generates texture path from item name
     */
    public static String generateTexturePath(String modId, String itemName) {
        return String.format("%s:item/%s", modId, itemName);
    }
}
