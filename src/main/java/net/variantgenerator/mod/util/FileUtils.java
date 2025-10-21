package net.variantgenerator.mod.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class for file operations
 */
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-FileUtils");

    /**
     * Finds all files matching a pattern in a directory
     */
    public static List<File> findFiles(File directory, String filePattern) {
        List<File> files = new ArrayList<>();

        try {
            Path startPath = directory.toPath();
            if (!Files.exists(startPath)) {
                LOGGER.warn("Directory not found: {}", directory.getAbsolutePath());
                return files;
            }

            try (Stream<Path> paths = Files.walk(startPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.toLowerCase().contains(filePattern.toLowerCase());
                        })
                        .map(Path::toFile)
                        .forEach(files::add);
            }
        } catch (IOException e) {
            LOGGER.error("Error scanning directory: {}", directory.getAbsolutePath(), e);
        }

        return files;
    }

    /**
     * Finds all PNG files in a directory
     */
    public static List<File> findPNGFiles(File directory) {
        List<File> files = new ArrayList<>();

        try {
            Path startPath = directory.toPath();
            if (!Files.exists(startPath)) {
                LOGGER.debug("Directory not found: {}", directory.getAbsolutePath());
                return files;
            }

            try (Stream<Path> paths = Files.walk(startPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".png"))
                        .map(Path::toFile)
                        .forEach(files::add);
            }
        } catch (IOException e) {
            LOGGER.debug("Error scanning PNG files in directory: {}", directory.getAbsolutePath());
        }

        return files;
    }

    /**
     * Gets the relative path from a parent directory
     */
    public static String getRelativePath(File parent, File child) {
        try {
            return parent.toPath().relativize(child.toPath()).toString();
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Could not compute relative path", e);
            return child.getAbsolutePath();
        }
    }

    /**
     * Creates a directory if it doesn't exist
     */
    public static boolean createDirectoryIfNeeded(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                LOGGER.debug("Created directory: {}", directory.getAbsolutePath());
            } else {
                LOGGER.warn("Failed to create directory: {}", directory.getAbsolutePath());
            }
            return created;
        }
        return true;
    }

    /**
     * Copies a file
     */
    public static boolean copyFile(File source, File destination) {
        try {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.debug("Copied file: {} -> {}", source.getAbsolutePath(), destination.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LOGGER.error("Error copying file: {} -> {}", source.getAbsolutePath(), destination.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Gets the file extension
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Gets the file name without extension
     */
    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(0, lastDot);
        }
        return name;
    }

    /**
     * Checks if a file exists and is readable
     */
    public static boolean isFileReadable(File file) {
        return file.exists() && file.isFile() && file.canRead();
    }

    /**
     * Checks if a directory exists and is readable
     */
    public static boolean isDirectoryReadable(File dir) {
        return dir.exists() && dir.isDirectory() && dir.canRead();
    }

    /**
     * Recursively deletes a directory and all its contents
     */
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }

        try {
            Files.walk(directory.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            LOGGER.debug("Deleted directory: {}", directory.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LOGGER.error("Error deleting directory: {}", directory.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Gets the size of a directory in bytes
     */
    public static long getDirectorySize(File directory) {
        long size = 0;
        try {
            size = Files.walk(directory.toPath())
                    .map(Path::toFile)
                    .mapToLong(File::length)
                    .sum();
        } catch (IOException e) {
            LOGGER.warn("Error calculating directory size", e);
        }
        return size;
    }
}
