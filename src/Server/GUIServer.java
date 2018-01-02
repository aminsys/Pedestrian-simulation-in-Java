package Server;

import Controller.AgentHandler;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class GUIServer extends Application{

    public ReadWriteLock lock = new ReentrantReadWriteLock();

    float[] arrayBlue, arrayOrange;

    public ArrayList<Circle> circlesOrange = new ArrayList<>();
    public ArrayList<Circle> circlesBlue = new ArrayList<>();

    private boolean createCircles = true; // Creating circlesOrange for the first time.

    // Create threads that handles agents:
    private AgentHandler aOrange, aBlue;

    public void start(Stage window) throws Exception {

        // Setting window and scene:
        window.setTitle("Pedestrian Simulation");
        Group root = new Group();
        Scene myScene = new Scene(root, 800, 600);

        // Show the window with scene:
        window.setScene(myScene);
        window.show();

        // Buttons to control the simulation window.
        Button startServerBtn = new Button("Start SERVER");
        Button startBtn = new Button("Start sim");
        startBtn.setDisable(true);
        Button stopBtn = new Button("Stop sim");
        Button startAgents = new Button("Start Agents");
        startServerBtn.setLayoutX(20.0);
        startServerBtn.setLayoutY(30.0);
        startBtn.setLayoutX(150);
        startBtn.setLayoutY(30);
        stopBtn.setLayoutX(250);
        stopBtn.setLayoutY(30);
        startAgents.setLayoutX(20.0);
        startAgents.setLayoutY(70.0);

        // Labels and input fields:
        final Label client1 =  new Label();
        final Label client2 = new Label();
        final TextField client1Input = new TextField();
        final TextField client2Input = new TextField();

        client1.setText("Enter blue agents: ");
        client1.setLayoutX(400);
        client1.setLayoutY(30);
        client2.setText("Enter orange agents: ");
        client2.setLayoutX(400);
        client2.setLayoutY(70);

        client1Input.setLayoutX(550);
        client1Input.setLayoutY(30);
        client2Input.setLayoutX(550);
        client2Input.setLayoutY(70);

        // Add all elements (fields, labels, etc) to the root object.
        root.getChildren().addAll(startServerBtn, startBtn, stopBtn, startAgents, client1, client2, client1Input, client2Input);

        // The animation of the agents happens here.
        AnimationTimer at = new AnimationTimer() {

            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Implementing some sort of a delay - Be nice to resources:
                if(now - lastUpdate >= 300_000_000){
                    int c = 0;
                    lock.readLock().lock();
                    //if(arrayBlue.length != 0){
                        for(int i = 0; i < arrayBlue.length; i += 2){
                            circlesBlue.get(c).setCenterX(arrayBlue[i]);
                            circlesBlue.get(c).setCenterY(arrayBlue[i+1]);
                            c++;
                            //System.out.println("Drawing blue");
                        }
                    //}
                    c = 0;

                    //if(arrayOrange.length != 0){
                        for(int i = 0; i < arrayOrange.length; i += 2){
                            circlesOrange.get(c).setCenterX(arrayOrange[i]);
                            circlesOrange.get(c).setCenterY(arrayOrange[i+1]);
                            c++;
                            //System.out.println("Drawing orange");
                        }
                    //}
                    lock.readLock().unlock();
                    lastUpdate = now;
                }
            }
        };

        // Start the server and then listener from here.
        startServerBtn.setOnAction( e -> {
            new Server().start(); // Runs only one time.
            System.out.println("Server Started");
            startServerBtn.setDisable(true);
            startBtn.setDisable(false);
        });

        // Start the simulation here.
        startBtn.setOnAction( e -> {
            // Creating circlesOrange:
            if (createCircles) {
               lock.readLock().lock();
               for (int i = 0; i < arrayOrange.length; i += 2) {
                   System.out.println("Array Orange X " + arrayOrange[i] + " array Orange Y " + arrayOrange[i+1]);
                   circlesOrange.add(new Circle(arrayOrange[i], arrayOrange[i+1], 15, Color.ORANGE));
               }
               for(int i = 0; i < arrayBlue.length; i += 2){
                   System.out.println("Array Blue X " + arrayBlue[i] + " array Blue Y " + arrayBlue[i+1]);
                   circlesBlue.add(new Circle(arrayBlue[i], arrayBlue[i+1], 10, Color.BLUE));
               }
               root.getChildren().addAll(circlesOrange);
               root.getChildren().addAll(circlesBlue);
               lock.readLock().unlock();

            }
            System.out.println("Blue circle size: " + circlesBlue.size());
            createCircles = false; // With this, circles aren't going to be created again.
            at.start();
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        });

        // To stop/   the simulation: Need to check on how to pause the threads too, client's especially.
        stopBtn.setOnAction( e -> {
            at.stop();
            stopBtn.setDisable(true);
            startBtn.setDisable(false);
        });

        startAgents.setOnAction( e -> {
            aBlue = new AgentHandler("Blue", Integer.parseInt(client1Input.getText()), Color.BLUE);
            aOrange = new AgentHandler("Orange", Integer.parseInt(client2Input.getText()), Color.ORANGE);

            arrayBlue = new float[Integer.parseInt(client1Input.getText()) * 2];
            arrayOrange = new float[Integer.parseInt(client2Input.getText()) * 2];
            aBlue.start();
            aOrange.start();

            startAgents.setDisable(true);
            client1Input.setDisable(true);
            client2Input.setDisable(true);
        });

    } // Start

    public static void main(String[] args){
        launch(args);
    }

    //========================== Server & Listener classes ==========================//
    // A server class that waits for clients and starts a listening thread:
    private class Server extends Thread{

        private ServerSocket mServerSocket;
        private ServerListener mServerListener;

        @Override
        public void run() {
            int clientCounter = 0;
            try {
                mServerSocket = new ServerSocket(1337);

                while (clientCounter < 2) {
                    System.out.println("Waiting for a connection...");
                    Socket s = mServerSocket.accept();
                    System.out.println("Connection established");
                    // Start listening on connection:
                    mServerListener = new ServerListener(s);
                    mServerListener.start();
                    clientCounter++;
                }
            }catch(IOException e){
                System.out.println("Server: Error: " + e);
                e.printStackTrace();
            }
            System.out.println("########Server no longer running########");
        }
    }

    // private Server Listener class that listens to data from clients:
    private class ServerListener extends Thread {

        boolean serverRunning = true;
        ObjectInputStream dinObject = null;
        DataInputStream dinData = null;
        DataOutputStream doutBoolean = null;
        Socket socket = null;

        // Local variables to receive data. It's too much, I know...
        String ID;

        public ServerListener(Socket s){
            this.socket = s;
        }

        public void run() {
            try {

                dinObject = new ObjectInputStream(socket.getInputStream());
                dinData = new DataInputStream(socket.getInputStream());
                doutBoolean = new DataOutputStream(socket.getOutputStream());

                while (serverRunning) {
                    try {
                        // Being nice towards the processor:
                        // If there's no delay, the circlesOrange go astray.
                        Thread.sleep(100);

                        //receiveOk = dinData.readBoolean();
                        if((ID = dinData.readUTF()) != null) {
                            if (ID.equals(aBlue.getID())) {
                                lock.writeLock().lock();
                                arrayBlue = (float[]) dinObject.readUnshared();
                                lock.writeLock().unlock();
                                System.out.println("******** BLUEBERRY PIE! ********" + arrayBlue.length);

                            } else if (ID.equals(aOrange.getID())) {
                                lock.writeLock().lock();
                                arrayOrange = (float[]) dinObject.readUnshared();
                                lock.writeLock().unlock();
                                System.out.println("******** ORANGE JUÍCE! ********" + arrayOrange.length);
                            } else {
                                System.out.println("Waiting for data from Handler.");
                            }
                        }

                        else{
                            System.out.println("Nothing was sent to server.");
                        }
                    }
                    catch(EOFException eof){
                        closeThread();
                        System.out.println("Error - ServerListener - readObject: " + eof);
                        eof.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        closeThread();
                        System.out.println("Error - ServerListener - readObject: " + e);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        closeThread();
                        System.out.println("Error - ServerListener - Sleep Thread: " + e);
                        e.printStackTrace();
                    }
                }
                closeThread();
                System.out.println("#######Server listener is terminating#######");
            }
            catch (IOException e) {
                closeThread();
                System.out.println("Error - ServerListener: " + e);
                e.printStackTrace();
            }
        } // run
        private void closeThread(){
            serverRunning = false;
            try{
                socket.close();
                dinObject.close();
                dinData.close();
                doutBoolean.close();
            }catch(IOException e){
                System.out.println("Error - closeThread - ServerListener: " + e);
                e.printStackTrace();
            }
        }
    } //class ServerListener

} // GUIServer