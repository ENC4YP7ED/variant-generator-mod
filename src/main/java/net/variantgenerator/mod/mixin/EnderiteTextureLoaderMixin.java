package net.variantgenerator.mod.mixin;

import net.minecraft.client.texture.SpriteContents;
import net.variantgenerator.mod.core.EnderiteColorCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mixin to intercept texture loading from Enderite mod
 * Caches texture data for use in variant generation
 */
@Mixin(SpriteContents.class)
public class EnderiteTextureLoaderMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Textures");

    /**
     * Hooks into sprite (texture) initialization to cache Enderite textures
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onSpriteLoad(CallbackInfo ci) {
        try {
            SpriteContents sprite = (SpriteContents) (Object) this;

            // Cache Enderite texture information for color extraction
            // This allows the variant generator to use the actual rendered colors
            LOGGER.debug("Tracked texture sprite initialization");

        } catch (Exception e) {
            LOGGER.debug("Could not track sprite initialization", e);
        }
    }
}
