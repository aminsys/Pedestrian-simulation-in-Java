package ClientModel;

import Controller.AgentHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args){
        String localhost = "127.0.0.1";
        final int PORT = 1337;
        boolean running = true;

        AgentHandler auClient = new AgentHandler(2);

        try {
            Socket clientSocket = new Socket(localhost, PORT);
            DataInputStream din = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
            while(running){
                running = false;
            }

            clientSocket.close();
            System.out.println("Client is exiting!");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
