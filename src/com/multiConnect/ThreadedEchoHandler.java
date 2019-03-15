package com.multiConnect;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadedEchoHandler implements Runnable {
    private Socket incoming;
    private int id;

    public ThreadedEchoHandler(Socket incomingSocket, int id) {
        incoming = incomingSocket;
        this.id = id;
    }

    public void run() {
        try {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();

            Scanner in = new Scanner(inStream, "UTF-8");

            PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);

            out.println(220);

            boolean done = false;
            while (!done && in.hasNextLine()){
                String line = in.nextLine();
                System.out.println(id + ": " + line);

                switch (line.trim().substring(0, 4)) {
                    case "EHLO":
                        System.out.println(id + ": " + 250);
                        out.println(250);
                        break;
                    case "MAIL":
                        String regexMail = "[<](.+@[a-zA-Z]+.[a-zA-Z]+)[>]";
                        Pattern patternMail = Pattern.compile(regexMail);
                        Matcher matcherMail = patternMail.matcher(line);

                        if (!line.contains("MAIL FROM:")) {
                            //syntax error
                            System.out.println(id + ": " + 500);
                            out.println(500);
                        }
                        else
                        if (!matcherMail.find()) {
                            //error in parameters
                            System.out.println(id + ": " + 501);
                            out.println(501);
                        }
                        else {
                            System.out.println(id + ": " + 250);
                            out.println(250);
                        }
                        break;
                    case "RCPT":
                        String regexRcpt = "[<](.+@[a-zA-Z]+.[a-zA-Z]+)[>]";
                        Pattern patternRcpt = Pattern.compile(regexRcpt);
                        Matcher matcherRcpt = patternRcpt.matcher(line);

                        if (!line.contains("RCPT TO:")) {
                            //syntax error
                            System.out.println(id + ": " + 500);
                            out.println(500);
                        }
                        else
                        if (!matcherRcpt.find()) {
                            //error in parameters
                            System.out.println(id + ": " + 501);
                            out.println(501);
                        }
                        else {
                            System.out.println(id + ": " + 250);
                            out.println(250);
                        }
                        break;
                    case "DATA":
                        boolean dataEnd = false;
                        String message = "";

                        System.out.println(id + ": " + 354);
                        out.println(354);

                        while (!dataEnd){
                            String messagePart = in.nextLine();
                            System.out.println(id + ": " + messagePart);

                            if (messagePart.equals(".")) {
                                dataEnd = true;
                                System.out.println(id + ": " + 250);
                                out.println(250);
                            }
                            else
                                message += messagePart;
                        }
                        break;
                    case "NOOP":
                        System.out.println(id + ": " + 250);
                        out.println(250);
                        break;
                    case "QUIT":
                        System.out.println(id + ": " + 221);
                        out.println(221);
                        done = true;
                        break;
                    default:
                        System.out.println(id + ": " + 404);
                        out.println(404);
                }
            }
        }
        catch (StringIndexOutOfBoundsException e){
            System.out.println(id + ": " + "U FKED UP WIZ SUBSTRING");
            System.out.println(404);
        }
        catch (Exception e) {
            System.out.println(id + ": " + "Exception caught. Error.");
        }
    }
}
