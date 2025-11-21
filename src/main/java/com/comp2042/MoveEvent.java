package com.comp2042;

public final class MoveEvent {
    private final EventSource eventSource;

    public MoveEvent(EventSource eventSource) {
        this.eventSource = eventSource;
    }
    
    public EventSource getEventSource() {
        return eventSource;
    }
}
