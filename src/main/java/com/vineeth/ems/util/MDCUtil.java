package com.vineeth.ems.util;


import org.apache.logging.log4j.ThreadContext;

import java.util.UUID;

public class MDCUtil {
    private static final String CORRELATION_ID = "correlationId";

    public static void setupMDCContext() {
        ThreadContext.put(CORRELATION_ID, UUID.randomUUID().toString());
    }

    public static void clearMDCContext() {
        ThreadContext.clearAll();
    }
}
