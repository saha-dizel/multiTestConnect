package com.multiConnect;

import javax.swing.*;
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
    private JTextArea text;

    public ThreadedEchoHandler(Socket incomingSocket, int id, JTextArea text) {
        incoming = incomingSocket;
        this.id = id;
        this.text = text;
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
                text.append(id + ": " + line + "\n");

                switch (line.trim().substring(0, 4)) {
                    case "EHLO":
                        System.out.println(id + ": " + 250);
                        text.append(id + ": " + 250 + "\n");
                        out.println(250);
                        break;
                    case "MAIL":
                        String regexMail = "[<](.+@[a-zA-Z]+.[a-zA-Z]+)[>]";
                        Pattern patternMail = Pattern.compile(regexMail);
                        Matcher matcherMail = patternMail.matcher(line);

                        if (!line.contains("MAIL FROM:")) {
                            //syntax error
                            System.out.println(id + ": " + 500);
                            text.append(id + ": " + 500 + "\n");
                            out.println(500);
                        }
                        else
                        if (!matcherMail.find()) {
                            //error in parameters
                            System.out.println(id + ": " + 501);
                            text.append(id + ": " + 501 + "\n");
                            out.println(501);
                        }
                        else {
                            System.out.println(id + ": " + 250);
                            text.append(id + ": " + 250 + "\n");
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
                            text.append(id + ": " + 500 + "\n");
                            out.println(500);
                        }
                        else
                        if (!matcherRcpt.find()) {
                            //error in parameters
                            System.out.println(id + ": " + 501);
                            text.append(id + ": " + 501 + "\n");
                            out.println(501);
                        }
                        else {
                            System.out.println(id + ": " + 250);
                            text.append(id + ": " + 250 + "\n");
                            out.println(250);
                        }
                        break;
                    case "DATA":
                        boolean dataEnd = false;
                        String message = "";

                        System.out.println(id + ": " + 354);
                        text.append(id + ": " + 354 + "\n");
                        out.println(354);

                        while (!dataEnd){
                            String messagePart = in.nextLine();
                            System.out.println(id + ": " + messagePart);
                            text.append(id + ": " + messagePart + "\n");

                            if (messagePart.equals(".")) {
                                dataEnd = true;
                                System.out.println(id + ": " + 250);
                                text.append(id + ": " + 250 + "\n");
                                System.out.println(message);
                                out.println(250);
                            }
                            else
                                message += messagePart;
                        }
                        break;
                    case "NOOP":
                        System.out.println(id + ": " + 250);
                        text.append(id + ": " + 250 + "\n");
                        out.println(250);
                        break;
                    case "QUIT":
                        System.out.println(id + ": " + 221);
                        text.append(id + ": " + 221 + "\n");
                        out.println(221);
                        done = true;
                        break;
                    default:
                        System.out.println(id + ": " + 404);
                        text.append(id + ": " + 404 + "\n");
                        out.println(404);
                }
            }

            incoming.close();
        }
        catch (StringIndexOutOfBoundsException e){
            System.out.println(id + ": " + "U FKED UP WIZ SUBSTRING");
            text.append(id + ": " + "U FKED UP WIZ SUBSTRING" + "\n");
            System.out.println(404);
        }
        catch (Exception e) {
            System.out.println(id + ": " + "Exception caught. Error.");
            text.append(id + ": " + "Exception caught. Error." + "\n");
        }
    }
}
