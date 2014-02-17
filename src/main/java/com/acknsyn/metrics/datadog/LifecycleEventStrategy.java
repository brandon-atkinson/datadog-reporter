package com.acknsyn.metrics.datadog;

public interface LifecycleEventStrategy {
    Event event(Lifecycle lifecycle);

    LifecycleEventStrategy NO_EVENTS = new LifecycleEventStrategy() {
        public Event event(Lifecycle lifecycle) {
            return null;
        }
    };
}
