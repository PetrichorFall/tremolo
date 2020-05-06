package com.ttd.common;

public class FFmpgTest {

    private String ffmpgEXE;

    public FFmpgTest(String ffmpgEXE) {
        this.ffmpgEXE = ffmpgEXE;
    }

    public static void main(String[] args) {
        FFmpgTest fFmpgTest = new FFmpgTest("E:\\ffmpg\\ffmpeg\\bin\\ffmpg.exe");

    }
}
