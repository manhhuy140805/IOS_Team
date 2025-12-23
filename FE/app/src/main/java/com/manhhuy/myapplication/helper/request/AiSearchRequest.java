package com.manhhuy.myapplication.helper.request;

public class AiSearchRequest {
    private String query;

    public AiSearchRequest() {
    }

    public AiSearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
