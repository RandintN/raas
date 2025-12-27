package software.robsoncassiano.raas.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration properties for YouTube-based podcast integration
 */
@ConfigurationProperties(prefix = "raas.podcast")
@Validated
public record PodcastProperties(

        /**
         * Cache duration for podcast data
         * Must be at least 1 minute, default: 30 minutes
         */
        @NotNull(message = "Podcast cache duration must not be null") Duration cacheDuration,

        /**
         * Map of show names/slugs to YouTube Playlist IDs
         */
        Map<String, String> playlistIds

) {

    /**
     * Create default PodcastProperties with sensible defaults and validation
     */
    public PodcastProperties {
        if (cacheDuration == null) {
            cacheDuration = Duration.ofMinutes(30);
        }

        // Custom validation: cache duration must be at least 1 minute
        if (cacheDuration.toMinutes() < 1) {
            throw new IllegalArgumentException(
                    "Podcast cache duration must be at least 1 minute, got: " + cacheDuration);
        }

        if (playlistIds == null) {
            playlistIds = Map.of();
        }
    }

    /**
     * Get cache duration in minutes
     */
    public long getCacheDurationMinutes() {
        return cacheDuration.toMinutes();
    }

    /**
     * Resolve show identifier (name or ID) to playlist ID
     */
    public Optional<String> getPlaylistIdByName(String identifier) {
        if (identifier == null || identifier.trim().isEmpty() || playlistIds.isEmpty()) {
            return Optional.empty();
        }

        String normalized = identifier.trim().toLowerCase();

        // Try direct match in keys
        if (playlistIds.containsKey(normalized)) {
            return Optional.of(playlistIds.get(normalized));
        }

        // Try fuzzy match in keys
        return playlistIds.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey()) || entry.getKey().contains(normalized))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
