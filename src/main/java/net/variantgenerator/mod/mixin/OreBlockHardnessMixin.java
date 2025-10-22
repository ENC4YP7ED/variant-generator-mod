package net.variantgenerator.mod.mixin;

import net.minecraft.block.Block;
import net.variantgenerator.mod.core.OreProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to track ore block hardness and blast resistance
 * Captures mining difficulty for proper tool tier assignment
 */
@Mixin(Block.class)
public class OreBlockHardnessMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Ores");

    /**
     * Hooks into block creation to track ore hardness
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onBlockInit(CallbackInfo ci) {
        try {
            Block block = (Block) (Object) this;
            String blockName = block.getTranslationKey();

            if (blockName.contains("enderite_ore") || blockName.contains("ore")) {
                // Extract hardness and blast resistance
                float hardness = block.getHardness();
                float blastResistance = block.getBlastResistance();

                OreProfile.trackOre(block, hardness, blastResistance);
                LOGGER.debug("Tracked ore block: {} (hardness={})", blockName, hardness);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not track ore hardness", e);
        }
    }
}
