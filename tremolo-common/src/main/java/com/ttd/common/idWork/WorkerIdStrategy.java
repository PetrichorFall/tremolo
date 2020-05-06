package com.ttd.common.idWork;

public interface WorkerIdStrategy {
    void initialize();

    long availableWorkerId();

    void release();
}
