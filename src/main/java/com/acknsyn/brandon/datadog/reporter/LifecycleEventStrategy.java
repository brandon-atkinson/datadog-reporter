package com.acknsyn.brandon.datadog.reporter;

public interface LifecycleEventStrategy {
    Event event(Lifecycle lifecycle);

    LifecycleEventStrategy NO_EVENTS = new LifecycleEventStrategy() {
        public Event event(Lifecycle lifecycle) {
            return null;
        }
    };
}
