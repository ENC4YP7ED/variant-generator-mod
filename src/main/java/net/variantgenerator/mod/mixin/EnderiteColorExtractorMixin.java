package net.variantgenerator.mod.mixin;

import net.minecraft.item.Item;
import net.variantgenerator.mod.VariantGeneratorMod;
import net.variantgenerator.mod.core.EnderiteColorCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to extract and cache color information from Enderite mod items.
 * Intercepts item registration to capture color data for use in variant generation.
 */
@Mixin(Item.class)
public class EnderiteColorExtractorMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Colors");

    /**
     * Hooks into item initialization to extract color information
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onItemInit(Item.Settings settings, CallbackInfo ci) {
        try {
            Item item = (Item) (Object) this;
            String itemName = item.getTranslationKey();

            // Cache colors from Enderite items for reuse
            if (itemName.contains("enderite")) {
                EnderiteColorCache.cacheItemColors(item);
                LOGGER.debug("Cached Enderite color information for: {}", itemName);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not extract Enderite color information", e);
        }
    }
}
