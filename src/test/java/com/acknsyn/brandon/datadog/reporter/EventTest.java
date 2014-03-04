package com.acknsyn.brandon.datadog.reporter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class EventTest {
    private JsonParser jsonParser;

    @Before
    public void setup() {
        jsonParser = new JsonParser();
    }

    @Test
    public void testEvent_titleNull() {
        String expectedTitle = null;
        String expectedText = "text";

        boolean isException = false;
        try {
            Event event = new Event(expectedTitle, expectedText);
        } catch (NullPointerException npe) {
            isException = true;
        }

        assertTrue("should have thrown exception", isException);
    }

    @Test
    public void testEvent_textNull() {
        String expectedTitle = "title";
        String expectedText = null;

        boolean isException = false;
        try {
            Event event = new Event(expectedTitle, expectedText);
        } catch (NullPointerException npe) {
            isException = true;
        }

        assertTrue("should have thrown exception", isException);
    }

    @Test
    public void testEvent_titleText() {
        String expectedTitle = "title";
        String expectedText = "text";

        Event event = new Event(expectedTitle, expectedText);

        JsonObject jsonObject = (JsonObject) jsonParser.parse(event.toString());

        assertEquals("title should match", expectedTitle, jsonObject.getAsJsonPrimitive("title").getAsString());
        assertEquals("text should match", expectedText, jsonObject.getAsJsonPrimitive("text").getAsString());
        assertEquals("entry count should match", 2, jsonObject.entrySet().size());
    }

    @Test
    public void testEvent_allFieldsPopulated() {
        String expectedTitle = "title";
        String expectedText = "text";
        Long expectedTimestamp = 1L;
        Event.Priority expectedPriority = Event.Priority.LOW;
        String[] expectedTags = new String[]{"tag:one", "tag:two"};
        Event.AlertType expectedAlertType = Event.AlertType.ERROR;
        String expectedAggregationKey = "aggregationKey";
        Event.SourceType expectedSourceType = Event.SourceType.MY_APPS;
        int expectedNumberOfProperties = 8;

        Event event = new Event(
                expectedTitle,
                expectedText,
                expectedTimestamp,
                expectedPriority,
                expectedTags,
                expectedAlertType,
                expectedAggregationKey,
                expectedSourceType
        );

        System.out.println(event.toString());

        JsonObject jsonObject = (JsonObject) jsonParser.parse(event.toString());

        assertEquals("title should match", expectedTitle, jsonObject.getAsJsonPrimitive("title").getAsString());
        assertEquals("text should match", expectedText, jsonObject.getAsJsonPrimitive("text").getAsString());
        assertEquals("timestamp should match", expectedTimestamp, (Long) jsonObject.getAsJsonPrimitive("timestamp").getAsLong());
        assertEquals("priority should match", expectedPriority.toString(), jsonObject.getAsJsonPrimitive("priority").getAsString());

        for (int i = 0; i < expectedTags.length; i++) {
            assertEquals("tag[" + i + "] should match", expectedTags[i], jsonObject.getAsJsonArray("tags").get(i).getAsString());
        }

        assertEquals("alert type should match", expectedAlertType.toString(), jsonObject.getAsJsonPrimitive("alert_type").getAsString());
        assertEquals("aggregation key should match", expectedAggregationKey, jsonObject.getAsJsonPrimitive("aggregation_key").getAsString());
        assertEquals("source type should match", expectedSourceType.toString(), jsonObject.getAsJsonPrimitive("source_type").getAsString());
        assertEquals("number of elements should match", expectedNumberOfProperties, jsonObject.entrySet().size());
    }

}
