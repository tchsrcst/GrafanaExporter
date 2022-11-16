package com.github.mdvinyaninov.tools;

import java.net.URLEncoder;
import java.util.List;

public class RenderUrlBuilder {
    private static String host;
    private static String dashboard;
    private static String dashboardUID;
    private static final String CONTEXT = "/render/dashboard-solo/db";
    private static final String CONTEXT_UNQ = "/render/d-solo";
    private static String uri;
    private static String from;
    private static String to;
    private static long panelId;
    private static List<String> vars;
    private static long width = 500;
    private static long height = 300;
    private static String tz = "Europe Moscow";
    private static long timeout = 180;

    public RenderUrlBuilder host(String host) { this.host = host; return this; }
    public RenderUrlBuilder dashboard(String dashboard) { this.dashboard = dashboard; return this; }
    public RenderUrlBuilder dashboardUID(String dashboardUID) { this.dashboardUID = dashboardUID; return this; }
    public RenderUrlBuilder from(String from) { this.from = from; return this; }
    public RenderUrlBuilder to(String to) { this.to = to; return this; }
    public RenderUrlBuilder width(long width) { this.width = width; return this; }
    public RenderUrlBuilder height(long height) { this.height = height; return this; }
    public RenderUrlBuilder panelId(long panelId) { this.panelId = panelId; return this; }
    public RenderUrlBuilder vars(List<String> vars) { this.vars = vars; return this; }
    public RenderUrlBuilder tz(String tz) { this.tz = tz; return this; }
    public RenderUrlBuilder timeout(long timeout) { this.timeout = timeout; return this; }

    public String getURI() {return uri; }

    // http://grafana/render/d-solo/{dashboard_uid}/{dashboard_name}
    // &from={from}
    // &to={to}}
    // &var-{key1}={value1}
    // &var-{key2}={value2}
    // &panelId={panel_id}
    // &width=1000
    // &height=500
    // &tz=Europe%2FMoscow
    public RenderUrlBuilder build() throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append(host);

        if (dashboardUID != null && !dashboardUID.isEmpty()) {
            builder.append(CONTEXT_UNQ + "/" + dashboardUID + "/" + dashboard);
        }
        else {
            builder.append(CONTEXT + "/" + dashboard);
        }
        builder.append("?panelId=" + panelId);
        builder.append("&from=" + from);
        builder.append("&to=" + to);
        if (vars != null) {
            for (String var : vars) {
                builder.append("&" + URLEncoder.encode(var, "UTF-8"));
            }
        }
        builder.append("&width=" + width);
        builder.append("&height=" + height);
        builder.append("&tz=" + URLEncoder.encode(tz, "UTF-8"));
        builder.append("&timeout=" + timeout);
        uri = builder.toString();
        return this;
    }

    @Override
    public String toString() {
        return uri;
    }
}
