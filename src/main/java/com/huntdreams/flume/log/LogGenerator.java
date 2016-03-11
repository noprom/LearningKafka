package com.huntdreams.flume.log;

import java.io.*;

/**
 * LogGenerator
 * 模拟日志产生
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 16/3/11 上午11:24.
 */
public class LogGenerator {

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.err.println("Usage - java -jar LogGenerator.jar " +
                        "<Location of Log File to be read> " +
                        "<location of the log file in which logs needs to be updated>");
                System.exit(0);
            }

            String location = args[1];
            File f = new File(location);
            FileOutputStream writer = new FileOutputStream(f);
            File read = new File(args[0]);
            BufferedReader reader = new BufferedReader(new FileReader(read));

            for (; ; ) {
                System.out.println(reader.readLine());
                writer.write((reader.readLine() + "\n").getBytes());
                writer.flush();
                Thread.sleep(500);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
