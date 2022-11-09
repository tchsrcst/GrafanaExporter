package com.github.mdvinyaninov.tools;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Formats {
    public static final ZoneId ZONE = ZoneId.systemDefault();
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZONE);
}
