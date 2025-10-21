package net.variantgenerator.mod.texture;

import net.variantgenerator.mod.texture.TextureColorizer.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

/**
 * Advanced texture processing for sophisticated color transformations
 */
public class AdvancedTextureProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-TextureProcessor");

    /**
     * Applies HSL-based color transformation
     */
    public static BufferedImage transformHSL(BufferedImage source, float hueShift, float saturation, float lightness) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);

                // Extract ARGB components
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Skip transparent pixels
                if (a == 0) {
                    result.setRGB(x, y, argb);
                    continue;
                }

                // Convert RGB to HSL
                float[] hsl = rgbToHSL(r, g, b);

                // Apply transformations
                hsl[0] = (hsl[0] + hueShift) % 360.0f;
                if (hsl[0] < 0) hsl[0] += 360.0f;
                hsl[1] = Math.min(1.0f, hsl[1] * saturation);
                hsl[2] = Math.min(1.0f, hsl[2] + lightness);

                // Convert back to RGB
                int[] rgb = hslToRGB(hsl[0], hsl[1], hsl[2]);

                // Combine with alpha
                int newARGB = (a << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                result.setRGB(x, y, newARGB);
            }
        }

        LOGGER.debug("Applied HSL transformation: hueShift={}, saturation={}, lightness={}", hueShift, saturation, lightness);
        return result;
    }

    /**
     * Applies brightness adjustment with gamma correction
     */
    public static BufferedImage adjustBrightness(BufferedImage source, float brightnessMultiplier, float gamma) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                if (a == 0) {
                    result.setRGB(x, y, argb);
                    continue;
                }

                // Apply gamma correction then brightness
                r = applyGammaAndBrightness(r, gamma, brightnessMultiplier);
                g = applyGammaAndBrightness(g, gamma, brightnessMultiplier);
                b = applyGammaAndBrightness(b, gamma, brightnessMultiplier);

                int newARGB = (a << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newARGB);
            }
        }

        LOGGER.debug("Adjusted brightness: multiplier={}, gamma={}", brightnessMultiplier, gamma);
        return result;
    }

    /**
     * Generates missing colors through interpolation and extrapolation
     */
    public static BufferedImage generateMissingColors(BufferedImage source, Pixel minColor, Pixel maxColor) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                if (a == 0) {
                    result.setRGB(x, y, argb);
                    continue;
                }

                // For grayscale, interpolate between min and max color
                if (r == g && g == b) {
                    float t = r / 255.0f;
                    int newR = (int) (minColor.r + t * (maxColor.r - minColor.r));
                    int newG = (int) (minColor.g + t * (maxColor.g - minColor.g));
                    int newB = (int) (minColor.b + t * (maxColor.b - minColor.b));

                    int newARGB = (a << 24) | (newR << 16) | (newG << 8) | newB;
                    result.setRGB(x, y, newARGB);
                } else {
                    // For non-grayscale, apply color shift
                    result.setRGB(x, y, argb);
                }
            }
        }

        LOGGER.debug("Generated missing colors");
        return result;
    }

    /**
     * Converts RGB to HSL color space
     */
    private static float[] rgbToHSL(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float l = (max + min) / 2.0f;

        float h, s;

        if (max == min) {
            h = s = 0.0f;
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2.0f - max - min) : d / (max + min);

            if (max == rf) {
                h = (gf - bf) / d + (gf < bf ? 6 : 0);
            } else if (max == gf) {
                h = (bf - rf) / d + 2;
            } else {
                h = (rf - gf) / d + 4;
            }
            h /= 6.0f;
        }

        return new float[]{h * 360.0f, s, l};
    }

    /**
     * Converts HSL to RGB color space
     */
    private static int[] hslToRGB(float h, float s, float l) {
        h = h / 360.0f;

        float r, g, b;

        if (s == 0.0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRGB(p, q, h + 1.0f / 3.0f);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1.0f / 3.0f);
        }

        return new int[]{
                Math.max(0, Math.min(255, (int) Math.round(r * 255))),
                Math.max(0, Math.min(255, (int) Math.round(g * 255))),
                Math.max(0, Math.min(255, (int) Math.round(b * 255)))
        };
    }

    private static float hueToRGB(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6 * t;
        if (t < 1.0f / 2.0f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6;
        return p;
    }

    private static int applyGammaAndBrightness(int value, float gamma, float brightness) {
        float normalized = value / 255.0f;
        float gammaApplied = (float) Math.pow(normalized, 1.0f / Math.max(0.1f, gamma));
        float brightened = gammaApplied * brightness;
        return Math.max(0, Math.min(255, (int) Math.round(brightened * 255)));
    }
}
