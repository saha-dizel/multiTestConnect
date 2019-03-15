package com.multiConnect;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        try {
            int connections = 0;

            ServerSocket test = new ServerSocket(25);
            System.out.println("Created server socket");

            while (true) {
                Socket incoming = test.accept();

                connections++;

                System.out.println("Connection try number " + connections);
                System.out.println("Socket accepted");

                Runnable runnable = new ThreadedEchoHandler(incoming, connections);
                Thread thread = new Thread(runnable);

                thread.start();
            }
        }
        catch (Exception e){
            System.out.println("Exception caught. Error.");
        }
    }
}
