package com.comp2042;

public record MoveEvent (EventSource eventSource) {

    public EventSource getEventSource() {
        return eventSource;
    }
}
