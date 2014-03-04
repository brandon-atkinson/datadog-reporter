package com.acknsyn.brandon.datadog.reporter;

public interface LifecycleEventStrategy {
    Event event(Lifecycle lifecycle);
}
