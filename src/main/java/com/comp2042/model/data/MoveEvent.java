package com.comp2042.model.data;

public record MoveEvent (EventSource eventSource) {

    public EventSource getEventSource() {
        return eventSource;
    }
}