package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable {

    private Socket sLSocket;

    public ServerListener(Socket socket){
        this.sLSocket = socket;
    }

    @Override
    public void run() {
        boolean running = true;

        try {
            DataInputStream din = new DataInputStream(this.sLSocket.getInputStream());
            //DataOutputStream dout = new DataOutputStream(this.sLSocket.getOutputStream());

            while(running){
                float x = din.readFloat();
                System.out.println("From client: " + x);
                running = false;
            }

            sLSocket.close();
            System.out.println("Server Listener is closing.");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
