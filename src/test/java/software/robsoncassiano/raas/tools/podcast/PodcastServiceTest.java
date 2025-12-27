package software.robsoncassiano.raas.tools.podcast;

import software.robsoncassiano.raas.config.PodcastProperties;
import software.robsoncassiano.raas.tools.podcast.model.Episode;
import software.robsoncassiano.raas.tools.podcast.model.PodcastStats;
import software.robsoncassiano.raas.tools.podcast.model.Show;
import software.robsoncassiano.raas.tools.youtube.YouTubeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PodcastService
 */
@ExtendWith(MockitoExtension.class)
class PodcastServiceTest {

    private PodcastService podcastService;
    private PodcastProperties podcastProperties;

    @Mock
    private YouTubeService youtubeService;

    @BeforeEach
    void setUp() {
        podcastProperties = new PodcastProperties(
                Duration.ofMinutes(30),
                Map.of("robsoncassiano", "playlist-123"));
        podcastService = new PodcastService(youtubeService, podcastProperties);
    }

    @Test
    void constructor_ShouldInitialize() {
        assertNotNull(podcastService);
    }

    @Test
    void resolveShowIdentifier_WithPlaylistName_ShouldResolveToConfiguredId() {
        // Clear cache
        ReflectionTestUtils.setField(podcastService, "cache", new ConcurrentHashMap<>());

        String result = podcastService.resolveShowIdentifier("robsoncassiano");
        assertEquals("playlist-123", result);
    }

    @Test
    void resolveShowIdentifier_WithNullOrEmpty_ShouldReturnNull() {
        assertNull(podcastService.resolveShowIdentifier(null));
        assertNull(podcastService.resolveShowIdentifier(""));
        assertNull(podcastService.resolveShowIdentifier("   "));
    }

    @Test
    void resolveShowIdentifier_WithUnknownName_ShouldReturnAsIs() {
        // Clear cache
        ReflectionTestUtils.setField(podcastService, "cache", new ConcurrentHashMap<>());

        String result = podcastService.resolveShowIdentifier("unknown-show");
        assertEquals("unknown-show", result);
    }

    /**
     * Test Show model functionality
     */
    @Test
    void show_IsActive_ShouldDetectPublishedStatus() {
        Show publishedShow = new Show("1", "Test Show", "Description",
                "Author", "https://example.com", null, "published", LocalDateTime.now());
        assertTrue(publishedShow.isActive());

        Show draftShow = new Show("2", "Test Show", "Description",
                "Author", "https://example.com", null, "draft", LocalDateTime.now());
        assertFalse(draftShow.isActive());
    }

    @Test
    void show_HasArtwork_ShouldDetectArtworkUrl() {
        Show showWithArtwork = new Show("1", "Test Show", "Description",
                "Author", "https://example.com", "https://example.com/art.jpg", "published", LocalDateTime.now());
        assertTrue(showWithArtwork.hasArtwork());

        Show showWithoutArtwork = new Show("2", "Test Show", "Description",
                "Author", "https://example.com", null, "published", LocalDateTime.now());
        assertFalse(showWithoutArtwork.hasArtwork());
    }

    @Test
    void show_GetShortDescription_ShouldTruncateLongDescriptions() {
        String longDescription = "a".repeat(250);
        Show show = new Show("1", "Test Show", longDescription,
                "Author", "https://example.com", null, "published", LocalDateTime.now());

        String shortDesc = show.getShortDescription();
        assertTrue(shortDesc.length() <= 203); // 200 + "..."
        assertTrue(shortDesc.endsWith("..."));
    }

    /**
     * Test Episode model functionality
     */
    @Test
    void episode_IsPublished_ShouldDetectPublishedStatus() {
        Episode published = new Episode("1", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", null, null);
        assertTrue(published.isPublished());

        Episode draft = new Episode("2", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "draft", null, null);
        assertFalse(draft.isPublished());
    }

    @Test
    void episode_IsScheduled_ShouldDetectScheduledStatus() {
        Episode scheduled = new Episode("1", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "scheduled", null, null);
        assertTrue(scheduled.isScheduled());

        Episode published = new Episode("2", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", null, null);
        assertFalse(published.isScheduled());
    }

    @Test
    void episode_HasAudio_ShouldDetectAudioUrl() {
        Episode withAudio = new Episode("1", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), "https://example.com/audio.mp3", null, "published", null, null);
        assertTrue(withAudio.hasAudio());

        Episode withoutAudio = new Episode("2", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", null, null);
        assertFalse(withoutAudio.hasAudio());
    }

    @Test
    void episode_GetEpisodeIdentifier_ShouldFormatCorrectly() {
        Episode withSeasonAndNumber = new Episode("1", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", 2, 5);
        assertEquals("S2E5", withSeasonAndNumber.getEpisodeIdentifier());

        Episode withNumberOnly = new Episode("2", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", null, 10);
        assertEquals("Episode 10", withNumberOnly.getEpisodeIdentifier());

        Episode withoutNumbers = new Episode("3", "Test", "Desc", "showId", "Test Show",
                LocalDateTime.now(), null, null, "published", null, null);
        assertEquals("", withoutNumbers.getEpisodeIdentifier());
    }

    /**
     * Test PodcastStats model functionality
     */
    @Test
    void podcastStats_HasEpisodes_ShouldDetectEpisodeCount() {
        PodcastStats withEpisodes = new PodcastStats(2, 50, LocalDateTime.now(), "Latest Episode",
                10, 2, 2.5, List.of());
        assertTrue(withEpisodes.hasEpisodes());

        PodcastStats withoutEpisodes = new PodcastStats(2, 0, null, null,
                0, 0, 0.0, List.of());
        assertFalse(withoutEpisodes.hasEpisodes());
    }

    @Test
    void podcastStats_GetFormattedAverageEpisodesPerMonth_ShouldFormatCorrectly() {
        PodcastStats stats = new PodcastStats(2, 50, LocalDateTime.now(), "Latest Episode",
                10, 2, 2.567, List.of());
        assertEquals("2.6", stats.getFormattedAverageEpisodesPerMonth());
    }

    @Test
    void podcastStats_GetMostActiveShow_ShouldFindShowWithMostEpisodes() {
        List<PodcastStats.ShowSummary> summaries = List.of(
                new PodcastStats.ShowSummary("Show A", 10, LocalDateTime.now()),
                new PodcastStats.ShowSummary("Show B", 25, LocalDateTime.now()),
                new PodcastStats.ShowSummary("Show C", 15, LocalDateTime.now()));

        PodcastStats stats = new PodcastStats(3, 50, LocalDateTime.now(), "Latest Episode",
                10, 2, 2.5, summaries);
        assertEquals("Show B", stats.getMostActiveShow());
    }

    @Test
    void podcastStats_GetMostActiveShow_WithEmptySummaries_ShouldReturnNA() {
        PodcastStats stats = new PodcastStats(0, 0, null, null,
                0, 0, 0.0, List.of());
        assertEquals("N/A", stats.getMostActiveShow());
    }
}
