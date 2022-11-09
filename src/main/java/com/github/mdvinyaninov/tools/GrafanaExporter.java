package com.github.mdvinyaninov.tools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.mdvinyaninov.tools.config.GraphConfig;
import com.github.mdvinyaninov.tools.config.RootConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrafanaExporter {
    private static final Logger LOGGER = LogManager.getLogger(GrafanaExporter.class);

    @Parameter(
            names = {"--help", "-h"},
            description = "Help"
    )
    private static Boolean help = false;
    @Parameter(
            names = {"--config", "-c"},
            description = "[Required] Path to GrafanaExporter XML configuration file"
    )
    private static String config = null;
    @Parameter(
            names = {"--from", "-f"},
            description = "[Required] Value to use as time range 'from' part for charts"
    )
    private static String from = null;
    @Parameter(
            names = {"--to", "-t"},
            description = "[Required] Value to use as time range 'to' part for charts"
    )
    private static String to = null;
    @Parameter(
            names = {"--destination", "-d"},
            description = "[Optional] Destination path for saving images files. Overrides destination specified in XML configuration file"
    )
    private static String destination = null;
    @Parameter(
            names = {"--prefix", "-p"},
            description = "[Optional] Prefix to add to all file names specified in XML configuration file"
    )
    private static String prefix = null;
    @Parameter(
            names = {"--concurrency"},
            description = "[Optional] Number of concurrent downloads. Default: 1"
    )
    private static Integer concurrency = 1;

    public static void run(String destination, String config, String prefix, String from, String to) throws Exception  {
        DateTimeType type = null;
        LocalDateTime dtFrom = null;
        LocalDateTime dtTo = null;
        Long epochFrom = null;
        Long epochTo = null;

        // testing parameters
        if (from.startsWith("now") && to.startsWith("now")) {
            type = DateTimeType.RELATIVE;
        }
        else {
            try {
                dtFrom = LocalDateTime.parse(from, Formats.DTF);
                dtTo = LocalDateTime.parse(to, Formats.DTF);
                type = DateTimeType.DATETIME;
            }
            catch (Exception ex) {

            }

            if (type == null) {
                try {
                    epochFrom = Long.parseLong(from);
                    epochTo = Long.parseLong(to);
                    type = DateTimeType.EPOCH;
                }
                catch (Exception ex) {

                }
            }
        }

        if (type == null) {
            LOGGER.info("Invalid 'from' \\ 'to' parameters.");
            System.exit(ResultCodes.FAIL_EXIT_CODE);
        }

        RootConfiguration root = RootConfiguration.loadConfig(config);
        LOGGER.debug(root);

        // override config if present
        if (destination != null) {
            // making dirs
            File out = new File(destination);
            if (!out.exists()) {
                out.mkdirs();
            }

            root.setDestination(destination);
        }

        int size = root.getGraphs().size();
        LOGGER.info("Graphs in config: {}", size);

        LOGGER.info("Initializing...");
        List<Worker> workers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(size);

        for (GraphConfig panel : root.getGraphs()) {
            Worker w = null;

            switch (type) {
                case RELATIVE:
                    w = new Worker(root, panel, prefix, from, to, latch);
                    break;
                case DATETIME:
                    w = new Worker(root, panel, prefix, dtFrom, dtTo, latch);
                    break;
                case EPOCH:
                    w = new Worker(root, panel, prefix, epochFrom, epochTo, latch);
                    break;
            }

            if (w != null) {
                workers.add(w);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(concurrency);

        LOGGER.info("Launching workers...");
        executor.invokeAll(workers);
        latch.await();
        LOGGER.info("Tasks completed.");

        int total = workers.size();
        int passed = 0;
        int failed = 0;

        for (Worker w : workers) {
            int res = w.getResult();

            if (res == 0)
                passed++;
            else
                failed++;
        }

        LOGGER.info(String.format("Total: %d Passed: %d Failed: %d", total, passed, failed));
    }

    public static void run(String config, String starttime, String endtime) throws Exception  {
        run(null, config, "", starttime, endtime);
    }

    public static void run(String outputPath, String configPath, String start, String end) throws Exception {
        run(outputPath, config, "", from, to);
    }

    public static void main(String[] args) {
        LOGGER.debug("Logging started.");
        GrafanaExporter a = new GrafanaExporter();
        JCommander c = JCommander.newBuilder().programName("GrafanaExporter").addObject(a).build();
        c.parse(args);

        if(!help.booleanValue()) {
            boolean set = true;

            if (from == null) {
                LOGGER.error("Required '--from' ('-f') argument is not specified.");
                set = false;
            }

            if (to == null) {
                LOGGER.error("Required '--to' ('-t') argument is not specified.");
                set = false;
            }

            if (config == null) {
                LOGGER.error("Required '--config' ('-c') argument is not specified.");
                set = false;
            }

            if(!set) {
                c.usage();
                System.exit(ResultCodes.FAIL_EXIT_CODE);
            }

            LOGGER.info("config: {}", config);
            LOGGER.info("from: {}", from);
            LOGGER.info("to: {}", to);
            LOGGER.info("destination: {}", destination);
            LOGGER.info("prefix: {}", prefix);
            LOGGER.info("concurrency: {}", concurrency);

            try {
                run(destination, config, prefix, from, to);
            }
            catch (Exception ex) {
                LOGGER.error("Error during execution.");
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
                System.exit(ResultCodes.FAIL_EXIT_CODE);
            }

            System.exit(ResultCodes.SUCCESS_EXIT_CODE);
        }
        else {
            c.usage();
            System.exit(ResultCodes.HELP_EXIT_CODE);
        }
    }
}
