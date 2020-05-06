package com.ttd.common.idWork;

public interface RandomCodeStrategy {
    void init();

    int prefix();

    int next();

    void release();
}
