package cn.kingsgame.log.util;

import java.io.*;

/**
 * LogGenerator
 * 金石日志模拟生成器
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/11 下午5:00.
 */
public class LogGenerator {

    private String sampleLogFile; // 样例log文件
    private String sampleLog; // 样例log文件内容
    private String sampleEventFile; // 事件log文件
    private String sampleEvent; // 样例事件文件内容

    private String objLogPath; // 目标日志路径
    private String objEventPath; // 目标事件路径

    public LogGenerator() {
        String rootPath = this.getClass().getResource("/").getPath();
        this.sampleLogFile = rootPath + "20160209.log";
        this.sampleEventFile = rootPath + "20160301_13_hk12.event";

        this.objLogPath = "/Users/noprom/Documents/Dev/Spark/servers/node-1/appserver-1/logs/log.log";
        this.objEventPath = "/Users/noprom/Documents/Dev/Spark/servers/node-1/appserver-2/logs/event.log";
    }

    /**
     * 加载样例log内容
     *
     * @return
     */
    public String getSampleLog() {
        if (sampleLog == null) {
            sampleLog = fileContent(sampleLogFile);
        }
        return sampleLog;
    }

    /**
     * 加载样例事件内容
     *
     * @return
     */
    public String getSampleEvent() {
        if (sampleEvent == null) {
            sampleEvent = fileContent(sampleEventFile);
        }
        return sampleEvent;
    }

    public String getObjLogPath() {
        return objLogPath;
    }

    public String getObjEventPath() {
        return objEventPath;
    }

    /**
     * 读取一个文件的内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    private String fileContent(String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String str = null;
            while ((str = reader.readLine()) != null) {
                builder.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        LogGenerator logGenerator = new LogGenerator();
        String sampleLog = logGenerator.getSampleLog();
        String sampleEvent = logGenerator.getSampleEvent();
        try {
            FileWriter logWriter = new FileWriter(logGenerator.getObjLogPath());
            FileWriter eventWriter = new FileWriter(logGenerator.getObjEventPath());
            while (true) {
                logWriter.write(sampleLog + "\n");
                eventWriter.write(sampleEvent + "\n");

                System.out.println(sampleLog);
                System.out.println(sampleEvent);
                logWriter.flush();
                eventWriter.flush();
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}