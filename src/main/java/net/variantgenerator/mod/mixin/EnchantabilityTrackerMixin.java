package net.variantgenerator.mod.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.variantgenerator.mod.core.EnchantmentProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to track enchantments applied to Enderite items
 * Creates a profile of which enchantments work with Enderite gear
 */
@Mixin(Enchantment.class)
public class EnchantabilityTrackerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Enchants");

    /**
     * Hooks into enchantment acceptance to track Enderite compatibility
     */
    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void onEnchantmentAccept(Item item, CallbackInfoReturnable<Boolean> cir) {
        try {
            String itemName = item.getTranslationKey();

            // Track enchantments for Enderite items
            if (itemName.contains("enderite")) {
                Enchantment enchantment = (Enchantment) (Object) this;
                EnchantmentProfile.trackEnchantment(item, enchantment);
                LOGGER.debug("Tracked enchantment for Enderite item: {}", itemName);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not track enchantment", e);
        }
    }
}
