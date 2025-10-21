package net.variantgenerator.mod.mixin;

import net.minecraft.item.Item;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.util.Identifier;
import net.variantgenerator.mod.VariantGeneratorMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to intercept item registration and track Enderite items
 * Allows the variant generator to monitor when Enderite items are registered
 */
@Mixin(MutableRegistry.class)
public class EnderiteRegistryInterceptorMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Registry");

    /**
     * Intercepts registry add operations to track Enderite items
     */
    @Inject(method = "add", at = @At("HEAD"))
    private void onRegistryAdd(Identifier id, Object value, CallbackInfoReturnable cir) {
        try {
            if (value instanceof Item && id != null) {
                String itemId = id.toString();

                // Track Enderite items for color/stat extraction
                if (itemId.contains("enderitemod:enderite")) {
                    Item item = (Item) value;
                    LOGGER.debug("Detected Enderite mod item registration: {}", itemId);

                    // Trigger variant generator to analyze this item
                    if (VariantGeneratorMod.getVariantGenerator() != null) {
                        VariantGeneratorMod.LOGGER.info("New Enderite item registered: {}", itemId);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Error in registry interceptor", e);
        }
    }
}
