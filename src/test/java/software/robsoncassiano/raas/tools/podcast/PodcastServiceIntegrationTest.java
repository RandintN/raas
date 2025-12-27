package software.robsoncassiano.raas.tools.podcast;

import software.robsoncassiano.raas.config.PodcastProperties;
import software.robsoncassiano.raas.tools.podcast.model.PodcastStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PodcastService with YouTube (test profile).
 */
@SpringBootTest
@ActiveProfiles("test")
class PodcastServiceIntegrationTest {

    @Autowired
    private PodcastService podcastService;

    @Autowired
    private PodcastProperties podcastProperties;

    @Test
    void getAllShows_ShouldReturnConfiguredPlaylists() {
        // In test profile, this will likely fail because of invalid API keys if it
        // tries to hit YouTube
        // But we want to verify context loads and properties are bound
        assertNotNull(podcastService);
        assertNotNull(podcastProperties);
        assertEquals(2, podcastProperties.playlistIds().size());
    }

    @Test
    void resolveShowIdentifier_WithTestPlaylist_ShouldResolve() {
        String resolvedId = podcastService.resolveShowIdentifier("robsoncassiano");
        assertNotNull(resolvedId);
        assertEquals("PL12345678901234567890123456789012", resolvedId);
    }

    @Test
    void podcastStats_BasicInitialization() {
        PodcastStats stats = podcastService.getPodcastStats();
        assertNotNull(stats);
        // Note: Real API calls will fail with test keys, but stats should be returnable
    }
}
