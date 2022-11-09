import com.github.mdvinyaninov.tools.GrafanaExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SampleTests {
    private static Logger LOGGER = LogManager.getLogger(SampleTests.class);
    private static final String CONFIG_PATH = "C:\\local\\tasks\\grafana_exporter";
    private static final String[] CONFIGS = {
              "sample-utilization.xml"
            , "sample-procstats.xml"
    };

    private static final String START = "2022-11-02T12:41:00";
    private static final String END   = "2022-11-02T15:58:00";
    private static final String STORE_PATH = "C:\\local\\tasks\\grafana_exporter\\grf\\";

    private static final String TASK = "maxcap2";
    private static final String PREFIX = "0_";
    private static final String OUTPUT_PATH = STORE_PATH + File.separator + TASK;

    @Test
    void fetch() {

        try {
            for (int i = 0; i < CONFIGS.length; i++) {
                LOGGER.info("{}: {}", i, CONFIGS[i]);
                final String configPath = CONFIG_PATH + File.separator + CONFIGS[i];

                GrafanaExporter.run(OUTPUT_PATH, configPath, PREFIX, START, END);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
