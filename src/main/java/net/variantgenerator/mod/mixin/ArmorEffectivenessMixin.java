package net.variantgenerator.mod.mixin;

import net.minecraft.item.ArmorItem;
import net.variantgenerator.mod.core.ArmorProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to track armor protection and durability properties
 * Captures comprehensive armor statistics for variant generation
 */
@Mixin(ArmorItem.class)
public class ArmorEffectivenessMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Armor");

    /**
     * Hooks into armor item creation to track protection statistics
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onArmorInit(CallbackInfo ci) {
        try {
            ArmorItem armor = (ArmorItem) (Object) this;
            String itemName = armor.getTranslationKey();

            if (itemName.contains("enderite")) {
                // Track armor properties
                ArmorProfile.trackArmor(armor, itemName);
                LOGGER.debug("Tracked Enderite armor: {}", itemName);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not track armor effectiveness", e);
        }
    }
}
