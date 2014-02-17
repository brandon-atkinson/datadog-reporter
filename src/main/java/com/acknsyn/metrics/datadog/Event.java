package com.acknsyn.metrics.datadog;

public class Event {

    private final String json;

    public Event(String title, String text) {
        this(title, text, null, null, null, null, null, null);
    }

    public Event(String title, String text, Long timestamp, Priority priority, String[] tags, AlertType alertType,
                 String aggregationKey, SourceType sourceType) {
        json = generateJson(title, text, timestamp, priority, tags, alertType, aggregationKey, sourceType);
    }

    private String generateJson(String title, String text, Long timestamp, Priority priority, String[] tags,
                             AlertType alertType, String aggregationKey, SourceType sourceType) {
        if (aggregationKey != null && aggregationKey.length() > 100) {
            throw new IllegalArgumentException("aggregationKey cannot exceed 100 characters");
        }

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        sb.append("\"title\":\"").append(title).append('"');
        sb.append(",\"text\":\"").append(text).append('"');

        if (timestamp != null) {
            sb.append(",\"timestamp\":").append(timestamp.toString()).append('"');
        }

        if (priority != null) {
            sb.append(",\"timestamp\":").append(priority.toString()).append('"');
        }

        if (tags != null && tags.length > 0) {
            sb.append(",\"tags\":");
            for (int i = 0; i < tags.length; i++) {
                sb.append('"').append(tags[i]).append('"');
            }
        }

        if (alertType != null) {
            sb.append(",\"alert_type\":\"").append(alertType.toString()).append('"');
        }

        if (aggregationKey != null) {
            sb.append(",\"aggregation_key\":\"").append(aggregationKey).append('"');
        }

        if (sourceType != null) {
            sb.append(",\"source_type\":\"").append(sourceType.toString()).append('"');
        }

        sb.append('}');

        return sb.toString();
    }

    public String toString() {
        return json;
    }

    public static enum Priority {
        NORMAL("normal"), LOW("low");

        private final String value;

        private Priority(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static enum AlertType {
        ERROR("error"), WARNING("warning"), INFO("info"), SUCCESS("success");

        private final String value;

        private AlertType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static enum SourceType {
        USER("user"), MY_APPS("my apps");

        private final String value;

        private SourceType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
