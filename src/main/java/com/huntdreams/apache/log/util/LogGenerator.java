package com.huntdreams.apache.log.util;

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
//            if (args.length != 2) {
//                System.err.println("Usage - java -jar LogGenerator.jar " +
//                        "<Location of Log File to be read> " +
//                        "<location of the log file in which logs needs to be updated>");
//                System.exit(0);
//            }

            //String location = args[1];
            String location = "/Users/noprom/Documents/Dev/Spark/servers/node-1/appserver-1/logs/debug.log";
            File f = new File(location);
            FileOutputStream writer = new FileOutputStream(f);
            // File read = new File(args[0]);
            // BufferedReader reader = new BufferedReader(new FileReader(read));

            String content = "64.242.88.10 - - [07/Mar/2004:16:05:49 -0800] \"GET /twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables HTTP/1.1\" 401 12846\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:06:51 -0800] \"GET /twiki/bin/rdiff/TWiki/NewUserTemplate?rev1=1.3&rev2=1.2 HTTP/1.1\" 200 4523\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:10:02 -0800] \"GET /mailman/listinfo/hsdivision HTTP/1.1\" 200 6291\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:11:58 -0800] \"GET /twiki/bin/view/TWiki/WikiSyntax HTTP/1.1\" 200 7352\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:20:55 -0800] \"GET /twiki/bin/view/Main/DCCAndPostFix HTTP/1.1\" 200 5253\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:23:12 -0800] \"GET /twiki/bin/oops/TWiki/AppendixFileSystem?template=oopsmore&param1=1.12&param2=1.12 HTTP/1.1\" 200 11382\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:24:16 -0800] \"GET /twiki/bin/view/Main/PeterThoeny HTTP/1.1\" 200 4924\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:29:16 -0800] \"GET /twiki/bin/edit/Main/Header_checks?topicparent=Main.ConfigurationVariables HTTP/1.1\" 401 12851\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:30:29 -0800] \"GET /twiki/bin/attach/Main/OfficeLocations HTTP/1.1\" 401 12851\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:31:48 -0800] \"GET /twiki/bin/view/TWiki/WebTopicEditTemplate HTTP/1.1\" 200 3732\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:32:50 -0800] \"GET /twiki/bin/view/Main/WebChanges HTTP/1.1\" 200 40520\n" +
                    "64.242.88.10 - - [07/Mar/2004:16:33:53 -0800] \"GET /twiki/bin/edit/Main/Smtpd_etrn_restrictions?topicparent=Main.ConfigurationVariables HTTP/1.1\" 401 12851";

            for (; ; ) {
                // System.out.println(reader.readLine());
                //writer.write((reader.readLine() + "\n").getBytes());
                writer.write(content.getBytes());
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
