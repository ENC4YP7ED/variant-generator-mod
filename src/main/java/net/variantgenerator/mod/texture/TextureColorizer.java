package net.variantgenerator.mod.texture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handles texture colorization for variant generation
 * Equivalent to the Python colorizer in main.py
 *
 * Converts grayscale textures to colored variants using a reference color palette
 */
public class TextureColorizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Texture");

    /**
     * Represents pixel color information
     */
    public static class Pixel {
        public int r, g, b, a;

        public Pixel(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public boolean isGrayscale() {
            return r == g && g == b;
        }

        public int brightness() {
            return (r + g + b) / 3;
        }

        public int toARGB() {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        @Override
        public String toString() {
            return String.format("(%d,%d,%d,%d)", r, g, b, a);
        }
    }

    /**
     * Extracts grayscale pixels from an image and returns brightness range
     */
    public static class GrayscaleAnalysis {
        public int brightest;
        public int darkest;
        public Pixel brightestPixel;
        public Pixel darkestPixel;
        public int maxBrightness;
        public int minBrightness;

        public GrayscaleAnalysis(int brightest, int darkest, Pixel brightestPixel,
                               Pixel darkestPixel, int maxBrightness, int minBrightness) {
            this.brightest = brightest;
            this.darkest = darkest;
            this.brightestPixel = brightestPixel;
            this.darkestPixel = darkestPixel;
            this.maxBrightness = maxBrightness;
            this.minBrightness = minBrightness;
        }
    }

    /**
     * Analyzes an image and extracts grayscale information
     */
    public GrayscaleAnalysis analyzeGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int maxBrightness = -1;
        int minBrightness = 256;
        Pixel brightestPixel = null;
        Pixel darkestPixel = null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = getPixel(image, x, y);

                // Only consider non-transparent pixels
                if (pixel.a > 0 && pixel.isGrayscale()) {
                    int brightness = pixel.brightness();

                    if (brightness > maxBrightness) {
                        maxBrightness = brightness;
                        brightestPixel = pixel;
                    }
                    if (brightness < minBrightness) {
                        minBrightness = brightness;
                        darkestPixel = pixel;
                    }
                }
            }
        }

        if (brightestPixel == null) {
            brightestPixel = new Pixel(255, 255, 255, 255);
        }
        if (darkestPixel == null) {
            darkestPixel = new Pixel(0, 0, 0, 255);
        }

        LOGGER.info("Grayscale Analysis - Brightest: {} ({}), Darkest: {} ({})",
                maxBrightness, brightestPixel, minBrightness, darkestPixel);

        return new GrayscaleAnalysis(255, 0, brightestPixel, darkestPixel, maxBrightness, minBrightness);
    }

    /**
     * Analyzes an image and extracts color information based on brightness
     */
    public GrayscaleAnalysis analyzeColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int maxBrightness = -1;
        int minBrightness = 256;
        Pixel brightestPixel = null;
        Pixel darkestPixel = null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = getPixel(image, x, y);

                // Only consider non-transparent pixels
                if (pixel.a > 0) {
                    int brightness = pixel.brightness();

                    if (brightness > maxBrightness) {
                        maxBrightness = brightness;
                        brightestPixel = pixel;
                    }
                    if (brightness < minBrightness) {
                        minBrightness = brightness;
                        darkestPixel = pixel;
                    }
                }
            }
        }

        if (brightestPixel == null) {
            brightestPixel = new Pixel(255, 255, 255, 255);
        }
        if (darkestPixel == null) {
            darkestPixel = new Pixel(0, 0, 0, 255);
        }

        LOGGER.info("Color Analysis - Brightest: {} ({}), Darkest: {} ({})",
                maxBrightness, brightestPixel, minBrightness, darkestPixel);

        return new GrayscaleAnalysis(255, 0, brightestPixel, darkestPixel, maxBrightness, minBrightness);
    }

    /**
     * Recolors an image by mapping grayscale values to a color range
     */
    public BufferedImage recolorImage(BufferedImage sourceImage, Pixel startColor, Pixel endColor) {
        BufferedImage result = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int changedPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = getPixel(sourceImage, x, y);

                if (pixel.isGrayscale()) {
                    // Map brightness to color range
                    float t = pixel.r / 255.0f;
                    int r = (int) (endColor.r + t * (startColor.r - endColor.r));
                    int g = (int) (endColor.g + t * (startColor.g - endColor.g));
                    int b = (int) (endColor.b + t * (startColor.b - endColor.b));
                    int a = pixel.a;

                    result.setRGB(x, y, new Pixel(r, g, b, a).toARGB());
                    changedPixels++;
                } else {
                    result.setRGB(x, y, pixel.toARGB());
                }
            }
        }

        LOGGER.info("Recolored {} pixels", changedPixels);
        return result;
    }

    /**
     * Loads a PNG image from file
     */
    public BufferedImage loadImage(File file) throws IOException {
        LOGGER.debug("Loading image: {}", file.getAbsolutePath());
        return ImageIO.read(file);
    }

    /**
     * Saves a BufferedImage as PNG
     */
    public void saveImage(BufferedImage image, File file) throws IOException {
        LOGGER.debug("Saving image: {}", file.getAbsolutePath());
        ImageIO.write(image, "png", file);
    }

    /**
     * Gets a pixel from an image
     */
    private Pixel getPixel(BufferedImage image, int x, int y) {
        int argb = image.getRGB(x, y);
        return new Pixel(
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF,
                (argb >> 24) & 0xFF
        );
    }

    /**
     * Converts grayscale image to colored variant using reference color palette
     */
    public BufferedImage convertToVariant(BufferedImage grayscaleImage, BufferedImage referenceColorImage) throws IOException {
        // Analyze grayscale image
        GrayscaleAnalysis grayscaleAnalysis = analyzeGrayscale(grayscaleImage);

        // Analyze reference color image
        GrayscaleAnalysis colorAnalysis = analyzeColors(referenceColorImage);

        // Recolor the grayscale image using the reference palette
        return recolorImage(grayscaleImage, colorAnalysis.brightestPixel, colorAnalysis.darkestPixel);
    }
}
