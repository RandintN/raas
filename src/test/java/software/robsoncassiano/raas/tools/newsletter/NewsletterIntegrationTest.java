package software.robsoncassiano.raas.tools.newsletter;

import software.robsoncassiano.raas.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify Beehiiv service and tools are properly wired in
 * Spring context
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class NewsletterIntegrationTest {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private NewsletterService newsletterService;

    @Autowired(required = false)
    private NewsletterTools newsletterTools;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void newsletterService_ShouldBeCreated() {
        assertNotNull(newsletterService,
                "NewsletterService should be created when API key and publications are configured");
    }

    @Test
    void newsletterTools_ShouldBeCreated() {
        assertNotNull(newsletterTools, "NewsletterTools should be created when NewsletterService is available");
    }

    @Test
    void mcpToolsAreScannable() {
        // Verify that our MCP tools are discoverable by the Spring AI MCP framework
        assertTrue(applicationContext.containsBean("newsletterTools"));

        NewsletterTools tools = applicationContext.getBean("newsletterTools", NewsletterTools.class);
        assertNotNull(tools);
    }

    @Test
    void newsletterService_ShouldHaveCorrectConfiguration() {
        assertNotNull(newsletterService);
    }
}
