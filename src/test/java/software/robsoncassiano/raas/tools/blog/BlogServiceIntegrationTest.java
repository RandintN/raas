package software.robsoncassiano.raas.tools.blog;

import software.robsoncassiano.raas.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify Blog service and tools are properly wired in
 * Spring context
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BlogServiceIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private BlogService blogService;

    @Autowired(required = false)
    private BlogTools blogTools;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void blogService_ShouldBeCreated() {
        assertNotNull(blogService, "BlogService should be created when RSS URL is configured");
    }

    @Test
    void blogTools_ShouldBeCreated() {
        assertNotNull(blogTools, "BlogTools should be created when BlogService is available");
    }

    @Test
    void mcpToolsAreScannable() {
        assertTrue(applicationContext.containsBean("blogTools"));
        BlogTools tools = applicationContext.getBean("blogTools", BlogTools.class);
        assertNotNull(tools);
    }
}
