package software.robsoncassiano.raas.tools.podcast;

import software.robsoncassiano.raas.config.PodcastProperties;
import software.robsoncassiano.raas.tools.podcast.model.Episode;
import software.robsoncassiano.raas.tools.podcast.model.PodcastStats;
import software.robsoncassiano.raas.tools.podcast.model.Show;
import software.robsoncassiano.raas.tools.youtube.YouTubeService;
import software.robsoncassiano.raas.tools.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for interacting with podcasts hosted on YouTube
 */
@Service
public class PodcastService {

    private static final Logger logger = LoggerFactory.getLogger(PodcastService.class);

    private final YouTubeService youtubeService;
    private final PodcastProperties podcastProperties;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private LocalDateTime lastCacheTime;

    public PodcastService(YouTubeService youtubeService, PodcastProperties podcastProperties) {
        this.youtubeService = youtubeService;
        this.podcastProperties = podcastProperties;
        logger.info("Podcast service initialized (YouTube-based) with cache duration: {} minutes",
                podcastProperties.getCacheDurationMinutes());
    }

    /**
     * Get all podcast shows (YouTube Playlists)
     */
    public List<Show> getAllShows() {
        return getCachedShows();
    }

    /**
     * Get a specific show by ID
     */
    public Show getShowById(String showId) {
        try {
            var playlist = youtubeService.getPlaylistDetails(showId);
            return mapPlaylistToShow(playlist);
        } catch (Exception e) {
            logger.error("Error fetching show by ID: {}", showId, e);
            throw new RuntimeException("Failed to fetch show: " + showId, e);
        }
    }

    /**
     * Resolve show identifier (name or ID) to show ID
     */
    public String resolveShowIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        // Try to resolve as a known show name from configuration
        Optional<String> configuredPlaylistId = podcastProperties.getPlaylistIdByName(identifier);
        if (configuredPlaylistId.isPresent()) {
            return configuredPlaylistId.get();
        }

        // Match by title in cached shows
        return getCachedShows().stream()
                .filter(show -> show.title().toLowerCase().contains(identifier.toLowerCase()) ||
                        show.id().equals(identifier))
                .map(Show::id)
                .findFirst()
                .orElse(identifier);
    }

    /**
     * Get latest episodes
     */
    public List<Episode> getLatestEpisodes(int maxResults, String showIdentifier) {
        if (showIdentifier != null && !showIdentifier.trim().isEmpty()) {
            String playlistId = resolveShowIdentifier(showIdentifier);
            List<Video> videos = youtubeService.getVideosFromPlaylist(playlistId, maxResults);
            Show show = getShowById(playlistId);
            return videos.stream()
                    .map(v -> mapVideoToEpisode(v, show))
                    .toList();
        }

        // Get videos from all configured playlists
        List<Episode> allEpisodes = new ArrayList<>();
        for (Show show : getCachedShows()) {
            List<Video> videos = youtubeService.getVideosFromPlaylist(show.id(), maxResults);
            allEpisodes.addAll(videos.stream()
                    .map(v -> mapVideoToEpisode(v, show))
                    .toList());
        }

        return allEpisodes.stream()
                .sorted(Comparator.comparing(Episode::publishedAt).reversed())
                .limit(maxResults)
                .toList();
    }

    /**
     * Search episodes
     */
    public List<Episode> searchEpisodes(String keyword, int maxResults, String showIdentifier) {
        // For simplicity, search via YouTube service and filter if needed
        List<Video> videos = youtubeService.searchVideosByTopic(keyword, maxResults);

        // Map to episodes. Since search doesn't give us show info easily,
        // we'll try to find the match or use a generic show title
        return videos.stream()
                .map(v -> mapVideoToEpisode(v, null))
                .toList();
    }

    /**
     * Get a specific episode by ID
     */
    public Episode getEpisodeById(String episodeId) {
        // On YouTube, an episode ID is a Video ID
        // Note: This requires getting video details which isn't directly in
        // YouTubeService yet but we can adapt
        List<Video> videos = youtubeService.searchVideosByTopic(episodeId, 1);
        if (videos.isEmpty()) {
            throw new RuntimeException("Episode not found: " + episodeId);
        }
        return mapVideoToEpisode(videos.get(0), null);
    }

    /**
     * Get podcast statistics
     */
    public PodcastStats getPodcastStats() {
        List<Show> shows = getCachedShows();
        List<Episode> latestEpisodes = getLatestEpisodes(50, null);

        if (latestEpisodes.isEmpty()) {
            return new PodcastStats(shows.size(), 0, null, null, 0, 0, 0.0, List.of());
        }

        LocalDateTime latestPublished = latestEpisodes.get(0).publishedAt();
        String latestTitle = latestEpisodes.get(0).title();

        List<PodcastStats.ShowSummary> summaries = shows.stream()
                .map(show -> new PodcastStats.ShowSummary(show.title(), 0, null)) // Simplified
                .toList();

        return new PodcastStats(
                shows.size(),
                latestEpisodes.size(),
                latestPublished,
                latestTitle,
                0, 0, 0.0,
                summaries);
    }

    @SuppressWarnings("unchecked")
    private List<Show> getCachedShows() {
        if (needsCacheRefresh()) {
            refreshCache();
        }
        return (List<Show>) cache.getOrDefault("shows", List.of());
    }

    private boolean needsCacheRefresh() {
        return lastCacheTime == null ||
                ChronoUnit.MINUTES.between(lastCacheTime, LocalDateTime.now()) >= podcastProperties
                        .getCacheDurationMinutes();
    }

    private void refreshCache() {
        try {
            List<Show> shows = new ArrayList<>();
            for (String playlistId : podcastProperties.playlistIds().values()) {
                shows.add(getShowById(playlistId));
            }
            cache.put("shows", shows);
            lastCacheTime = LocalDateTime.now();
            logger.info("Podcast cache refreshed with {} shows", shows.size());
        } catch (Exception e) {
            logger.error("Failed to refresh podcast cache", e);
        }
    }

    private Show mapPlaylistToShow(com.google.api.services.youtube.model.Playlist playlist) {
        return new Show(
                playlist.getId(),
                playlist.getSnippet().getTitle(),
                playlist.getSnippet().getDescription(),
                playlist.getSnippet().getChannelTitle(),
                "https://www.youtube.com/playlist?list=" + playlist.getId(),
                playlist.getSnippet().getThumbnails().getHigh().getUrl(),
                "published",
                null);
    }

    private Episode mapVideoToEpisode(Video video, Show show) {
        return new Episode(
                video.id(),
                video.title(),
                video.description(),
                show != null ? show.id() : null,
                show != null ? show.title() : "YouTube Podcast",
                video.publishedAt(),
                video.url(),
                null,
                "published",
                null,
                null);
    }
}
