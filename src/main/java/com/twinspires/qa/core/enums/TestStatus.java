package com.twinspires.qa.core.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalwinder.singh on 7/12/17.
 */
public enum TestStatus {
    // Status IDs come from qTest statuses (inspect status dropdown for id values)
    PASSED((long) 601),                 // Passed
    PASSED_W_MAPPING((long) 1203443),   // Passed w/Mapping
    FAILED((long) 602),                 // Failed
    FAILED_W_DEFECTS((long) 1303647),   // Failed w/Defects
    WORKAROUND((long) 1255747),         // Disabled
    INCOMPLETE((long) 603),             // Incomplete
    BLOCKED((long) 604),                // Blocked
    TOGGLED_OFF((long) 1197126);        // Disabled

    private Long status;

    private static final Map<Long, TestStatus> lookup = new HashMap<Long, TestStatus>();

    static {
        for (TestStatus t : TestStatus.values()) {
            lookup.put(t.getStatus(), t);
        }
    }

    private TestStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public String getStatusFlag() {
        return ("@" + status + "@");
    }

    public static TestStatus get(Long status) {
        try {
            return lookup.get(status);
        } catch (Exception e) {}
        return null;
    }

    /**
     * Checks message to see if it begins with custom status flag
     * @param message the message in which to check for the flag
     * @return the TestStatus flag that is found
     */
    public static TestStatus hasStatusFlag(String message) {
        long statusId;
        int end;
        if(message.startsWith("@")) {
            end = message.indexOf("@", 1);
            statusId = Long.parseLong(message.substring(1, end));
            return get(statusId);
        }
        return null;
    }
}
