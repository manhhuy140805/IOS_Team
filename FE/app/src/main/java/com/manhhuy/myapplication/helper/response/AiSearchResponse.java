package com.manhhuy.myapplication.helper.response;

import java.util.List;

public class AiSearchResponse {
    private String explanation;
    private PageResponse<EventResponse> events;

    public AiSearchResponse() {
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public PageResponse<EventResponse> getEvents() {
        return events;
    }

    public void setEvents(PageResponse<EventResponse> events) {
        this.events = events;
    }
}
