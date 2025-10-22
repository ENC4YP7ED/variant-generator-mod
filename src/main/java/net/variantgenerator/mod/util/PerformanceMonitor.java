package net.variantgenerator.mod.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Monitors performance metrics of the variant generator
 * Tracks generation times, cache hits, and system performance
 */
public class PerformanceMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Performance");

    /**
     * Performance metric
     */
    public static class Metric {
        public String name;
        public long startTime;
        public long endTime;
        public long duration;
        public boolean completed;

        public Metric(String name) {
            this.name = name;
            this.startTime = System.currentTimeMillis();
            this.completed = false;
        }

        public void complete() {
            this.endTime = System.currentTimeMillis();
            this.duration = endTime - startTime;
            this.completed = true;
        }

        @Override
        public String toString() {
            return String.format("Metric{name=%s, duration=%dms, completed=%s}",
                name, duration, completed);
        }
    }

    private static final Map<String, List<Metric>> METRICS = new HashMap<>();
    private static final Map<String, Long> CACHE_STATS = new HashMap<>();

    static {
        CACHE_STATS.put("color_cache_hits", 0L);
        CACHE_STATS.put("stat_cache_hits", 0L);
        CACHE_STATS.put("total_variants_generated", 0L);
        CACHE_STATS.put("total_textures_processed", 0L);
    }

    /**
     * Starts tracking a metric
     */
    public static Metric startMetric(String name) {
        Metric metric = new Metric(name);
        METRICS.computeIfAbsent(name, k -> new ArrayList<>()).add(metric);
        LOGGER.debug("Started metric: {}", name);
        return metric;
    }

    /**
     * Records a cache hit
     */
    public static void recordCacheHit(String cacheType) {
        String key = cacheType + "_cache_hits";
        CACHE_STATS.put(key, CACHE_STATS.getOrDefault(key, 0L) + 1);
    }

    /**
     * Records a variant generation
     */
    public static void recordVariantGenerated() {
        CACHE_STATS.put("total_variants_generated", CACHE_STATS.get("total_variants_generated") + 1);
    }

    /**
     * Records a texture processing
     */
    public static void recordTextureProcessed() {
        CACHE_STATS.put("total_textures_processed", CACHE_STATS.get("total_textures_processed") + 1);
    }

    /**
     * Gets average duration for a metric
     */
    public static long getAverageDuration(String metricName) {
        List<Metric> metrics = METRICS.getOrDefault(metricName, new ArrayList<>());
        if (metrics.isEmpty()) return 0;

        long totalDuration = metrics.stream()
            .filter(m -> m.completed)
            .mapToLong(m -> m.duration)
            .sum();

        return totalDuration / metrics.size();
    }

    /**
     * Gets cache hit count
     */
    public static long getCacheHitCount(String cacheType) {
        return CACHE_STATS.getOrDefault(cacheType + "_cache_hits", 0L);
    }

    /**
     * Gets total variants generated
     */
    public static long getTotalVariantsGenerated() {
        return CACHE_STATS.getOrDefault("total_variants_generated", 0L);
    }

    /**
     * Gets total textures processed
     */
    public static long getTotalTexturesProcessed() {
        return CACHE_STATS.getOrDefault("total_textures_processed", 0L);
    }

    /**
     * Prints performance report
     */
    public static void printReport() {
        LOGGER.info("=== Variant Generator Performance Report ===");
        LOGGER.info("Total Variants Generated: {}", getTotalVariantsGenerated());
        LOGGER.info("Total Textures Processed: {}", getTotalTexturesProcessed());
        LOGGER.info("Color Cache Hits: {}", getCacheHitCount("color"));
        LOGGER.info("Stat Cache Hits: {}", getCacheHitCount("stat"));
        LOGGER.info("Average Metric Times:");

        METRICS.keySet().forEach(metricName -> {
            long avgTime = getAverageDuration(metricName);
            if (avgTime > 0) {
                LOGGER.info("  {}: {}ms", metricName, avgTime);
            }
        });
    }

    /**
     * Clears all metrics
     */
    public static void clear() {
        METRICS.clear();
        CACHE_STATS.clear();
        LOGGER.debug("Cleared performance metrics");
    }

    /**
     * Gets metrics count
     */
    public static int getMetricsCount() {
        return METRICS.size();
    }
}
