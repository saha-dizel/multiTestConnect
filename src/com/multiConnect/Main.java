package com.multiConnect;

import javax.swing.*;
import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        JFrame frame = new JFrame("da");
        JPanel panel = new JPanel();
        JTextArea text = new JTextArea();
        JScrollPane pane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        try {
            frame.setSize(600, 900);
            frame.add(pane);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            text.setBackground(Color.BLACK);
            text.setForeground(Color.GREEN);
            text.setFont(new Font("Consolas", Font.PLAIN, 16));
            text.setLineWrap(true);
            text.setWrapStyleWord(true);

            int connections = 0;

            ServerSocket test = new ServerSocket(25);
            System.out.println("Created server socket");
            text.setText("Created server socket\n");

            while (true) {
                Socket incoming = test.accept();

                connections++;

                System.out.println("Connection try number " + connections);
                System.out.println("Socket accepted");
                text.append("Connection try number " + connections + "\n");
                text.append("Socket accepted\n");

                Runnable runnable = new ThreadedEchoHandler(incoming, connections, text);
                Thread thread = new Thread(runnable);

                thread.start();
            }
        }
        catch (Exception e){
            System.out.println("Exception caught. Error.");
        }
    }
}
