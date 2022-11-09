package com.github.mdvinyaninov.tools;

import com.github.mdvinyaninov.tools.config.GraphConfig;
import com.github.mdvinyaninov.tools.config.RootConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Worker implements Callable<Integer> {
    private static final ZoneId ZONE = ZoneId.systemDefault();
    private static Logger LOGGER = LogManager.getLogger(Worker.class);
    private static final String EXTENSION = ".png";

    private int attempts;
    private CountDownLatch latch;
    private String requestURI;
    private String storePath;
    private String folder;
    private String file;
    private CloseableHttpClient httpclient;
    private String auth;
    private String name;
    private int result;

    // string date time
    Worker(RootConfiguration root_config, GraphConfig panel_config, String prefix, LocalDateTime starttime, LocalDateTime endtime,
           CountDownLatch latch) {
        final Long startTimeEpoch = starttime.atZone(ZONE).toInstant().toEpochMilli();
        final Long endTimeEpoch = endtime.atZone(ZONE).toInstant().toEpochMilli();
        _init(root_config, panel_config, prefix, startTimeEpoch.toString(), endTimeEpoch.toString(), latch);
    }

    // epoch millis
    Worker(RootConfiguration root_config, GraphConfig panel_config, String prefix, Long starttime, Long endtime,
           CountDownLatch latch) {
        _init(root_config, panel_config, prefix, starttime.toString(), endtime.toString(), latch);
    }

    // relative
    Worker(RootConfiguration root_config, GraphConfig panel_config, String prefix, String starttime, String endtime,
           CountDownLatch latch) {
        _init(root_config, panel_config, prefix, starttime, endtime, latch);
    }

    private void _init(RootConfiguration root_config, GraphConfig panel_config, String prefix, String start, String end,
                       CountDownLatch latch){
        this.latch = latch;
        this.auth = "Bearer " + root_config.getApikey();
        this.attempts = root_config.getDownloadAttempts();
        this.name = panel_config.getName();

        try {
            RenderUrlBuilder builder = new RenderUrlBuilder()
                    .host(root_config.getHost())
                    .dashboard(root_config.getDashboard())
                    .dashboardUID(root_config.getDashboardUID())
                    .panelId(panel_config.getPanelId())
                    .width(panel_config.getWidth())
                    .height(panel_config.getHeight())
                    .tz(root_config.getTimezone())
                    .vars(panel_config.getVars())
                    .timeout(root_config.getTimeout());

            builder = builder
                    .from(start)
                    .to(end);

            builder = builder.build();

            requestURI = builder.getURI();
            storePath = root_config.getDestination();
            folder = panel_config.getFolder();
            file = prefix + panel_config.getName() + EXTENSION;

            httpclient = HTTPClientFactory.getClient();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Integer call() {
        result = -1;

        LOGGER.info("Downloading '{}' graph ({})...", name, latch.getCount());

        try {
            for (int i = 1; i <= attempts; i++) {
                LOGGER.info("Attempt {} out of {} for '{}'...", i, attempts, name);
                long start = System.currentTimeMillis();
                HttpGet req = new HttpGet(requestURI);
                req.addHeader("Authorization", auth);
                LOGGER.info(req.toString());
                Handler h = new Handler(storePath, folder, file);
                httpclient.execute(req, h);
                result = h.getResult();

                long duration = System.currentTimeMillis() - start;
                LOGGER.info("Action took {} ms.", duration);

                if (result == 0) {
                    break;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            latch.countDown();
        }

        return result;
    }

    public int getResult() {
        return result;
    }
}