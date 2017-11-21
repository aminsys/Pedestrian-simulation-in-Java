package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable{

    //public static void main(String[] args){}

    @Override
    public void run() {

        ServerSocket mServerSocket;
        boolean running = true;
        ServerListener mServerListener;

        ServerThread ms = new ServerThread();
        try{
            mServerSocket = new ServerSocket(1337);
            while(running) {
                System.out.println("Starting server from GUI");
                Socket mSocket = mServerSocket.accept();
                System.out.println("Connection established with a client on port: " + mServerSocket.getLocalPort());
                mServerListener = new ServerListener(mSocket);
                new Thread(mServerListener).start();
                running = false;
            }
        }catch(IOException e){
            System.out.println("Error at initializing or listening to server.");
            e.printStackTrace();
        }
    }
}
