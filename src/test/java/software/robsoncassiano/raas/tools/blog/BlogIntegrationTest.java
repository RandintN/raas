package software.robsoncassiano.raas.tools.blog;

import software.robsoncassiano.raas.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the Blog service
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BlogIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private BlogService blogService;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void blogService_ShouldBeCreated() {
        assertNotNull(blogService);
    }
}
