package com.twinspires.qa.core.enums;

public enum ProgramRating {
    RUN_STYLE("Run Style"),
    EARLY_PACE_1("Early Pace 1"),
    EARLY_PACE_2("Early Pace 2"),
    LATE_PACE("Late Pace"),
    DAYS_OFF("Days Off"),
    BEST_SPEED("Best Speed"),
    AVERAGE_SPEED("Avg Speed"),
    AVERAGE_DISTANCE("Avg Distance"),
    LAST_SPEED("Last Speed"),
    LAST_CLASS("Last Class"),
    AVERAGE_CLASS("Average Class"),
    PRIME_POWER("Prime Power");

    private String text;

    ProgramRating (String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ProgramRating fromString(String text) {
        if (text != null) {
            for (ProgramRating b : ProgramRating.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
