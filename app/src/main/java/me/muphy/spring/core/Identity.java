package me.muphy.spring.core;

import java.util.concurrent.atomic.AtomicLong;

public interface Identity {
    long id = Id.id.incrementAndGet();

    default String getIdentity() {
        return getClass().getSimpleName() + "@" + id;
    }

    interface Id {
        AtomicLong id = new AtomicLong();
    }
}
