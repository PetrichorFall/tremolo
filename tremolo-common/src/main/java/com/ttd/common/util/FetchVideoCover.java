package com.ttd.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FetchVideoCover {

    private  String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    /**
     * 对视频进行截图
     * @param videoInputPath 视频路径
     * @param coverOutputPath 图片的路径
     */
    public void getConvert(String videoInputPath, String coverOutputPath) throws IOException {
        //		ffmpeg.exe -ss 00:00:01 -i spring.mp4 -vframes 1 bb.jpg

        List<String> command = new java.util.ArrayList<String>();
        command.add(ffmpegEXE);

        // 指定截取第1秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);

        for (String c : command) {
            System.out.print(c + " ");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public String getFfmpegEXE() {
        return ffmpegEXE;
    }

    public void setFfmpegEXE(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public FetchVideoCover() {
    }
}
