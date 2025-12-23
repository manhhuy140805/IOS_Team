package com.manhhuy.myapplication.helper.response;

import java.util.List;

public class AiSearchResponse {
    private String explanation;
    private boolean foundMatch; // true nếu tìm thấy phù hợp, false nếu chỉ là gợi ý
    private PageResponse<EventResponse> events;

    public AiSearchResponse() {
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isFoundMatch() {
        return foundMatch;
    }

    public void setFoundMatch(boolean foundMatch) {
        this.foundMatch = foundMatch;
    }

    public PageResponse<EventResponse> getEvents() {
        return events;
    }

    public void setEvents(PageResponse<EventResponse> events) {
        this.events = events;
    }
}
