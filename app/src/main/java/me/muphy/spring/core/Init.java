package me.muphy.spring.core;

import me.muphy.spring.common.Result;

public interface Init {
    default Result create() {
        return Result.ok();
    }

    default Result start() {
        return Result.ok();
    }
}
