import com.github.mdvinyaninov.tools.RenderUrlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class URLBuilderTests {
    private static Logger LOGGER = LogManager.getLogger(URLBuilderTests.class);

    @Test
    void url() {
        LocalDateTime start = LocalDateTime.of(2020, 01, 28, 14, 00, 00);
        LocalDateTime end = LocalDateTime.of(2020, 01, 28, 14, 20, 00);

        Long s = start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long e = end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        try {
            RenderUrlBuilder b = new RenderUrlBuilder()
                    .host("http://localhost:9080")
                    .dashboard("dashboard")
                    .dashboardUID("AQ==")
                    .panelId(1)
                    .from(s.toString())
                    .to(e.toString())
                    .width(400)
                    .height(200)
                    .tz("Europe Moscow")
                    .timeout(100)
                    .build();

            String uri = b.build().toString();

            LOGGER.info(uri);
            Assertions.assertEquals("http://localhost:9080/render/d-solo/AQ==/dashboard?panelId=1&from=1580209200&to=1580210400&width=400&height=200&tz=Europe+Moscow&timeout=100",
                    uri
            );
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
