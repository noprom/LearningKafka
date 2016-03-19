package com.huntdreams.logs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ApacheLogGenerator
 * 模拟产生Apache日志
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 3/19/16 9:28 AM.
 */
public class ApacheLogGenerator {

    public static void main(String[] args) {
        try {
            String location = "/Users/noprom/Documents/Dev/Kafka/Pro/LearningKafka/src/main/resources/apache.access.log";
            File f = new File(location);
            FileOutputStream writer = new FileOutputStream(f);

            String content = "64.242.88.10 - - [07/Mar/2004:16:05:49 -0800] \"GET /twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables HTTP/1.1\" 401 12846\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:06:51 -0800] \"GET /twiki/bin/rdiff/TWiki/NewUserTemplate?rev1=1.3&rev2=1.2 HTTP/1.1\" 200 4523\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:10:02 -0800] \"GET /mailman/listinfo/hsdivision HTTP/1.1\" 200 6291\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:11:58 -0800] \"GET /twiki/bin/view/TWiki/WikiSyntax HTTP/1.1\" 200 7352\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:20:55 -0800] \"GET /twiki/bin/view/Main/DCCAndPostFix HTTP/1.1\" 200 5253";
            while (true) {
                writer.write(content.getBytes());
                writer.flush();
                Thread.sleep(5000);//每隔5秒产生一次日志
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
