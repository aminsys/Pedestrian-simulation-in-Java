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
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Purpose: The GUI server controls the simulation.
 * Buttons are used to: start a server, initiate agent handlers, start a simulation,
 * and pause it.
 * Input fields are used to register the amount of agents desired to initiate before
 * initiating the agent handlers.
 * The GUI server has an inner Server class that waits for TWO agent handlers to
 * connect, and another inner Server Listener class that receives data from
 * respective handler.
 *
 * @author Amin Yassin
 * @version 1.0 03/01/2018
 *
 */
public class GUIServer extends Application{

    /**
     * To prevent reading/ writing at the same time.
     */
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * To store data received from handlers.
     */
    public float[] bluePositions, orangePositions;

    /**
     * To store the representations of agents in ArrayLists.
     */
    private ArrayList<Circle> circlesOrange = new ArrayList<>();
    private ArrayList<Circle> circlesBlue = new ArrayList<>();

    /**
     * Creating circlesOrange for the first time.
     */
    private boolean createCircles = true;

    private AgentHandler aOrange, aBlue;

    /**
     * Here all parts of the application window are managed (Buttons, input fields, circles).
     * The animation of the circles are also controlled here.
     *
     * @param window
     * @throws Exception
     */
    public void start(Stage window) throws Exception {

        // Setting window and scene:
        window.setTitle("Pedestrian Simulation");
        Group root = new Group();
        Scene myScene = new Scene(root, 800, 600);

        // Show the window with scene:
        window.setScene(myScene);
        window.show();

        // Line for elegency:
        Line line1 = new Line(0, 110, 800, 110);

        // Buttons to control the simulation window.
        Button startServer = new Button("Start SERVER");
        Button startSim = new Button("Start sim");
        startSim.setDisable(true);
        Button stopSim = new Button("Stop sim");
        stopSim.setDisable(true);
        Button initiateAgents = new Button("Start Agents");
        initiateAgents.setDisable(true);

        startServer.setLayoutX(20.0);
        startServer.setLayoutY(30.0);
        startSim.setLayoutX(150);
        startSim.setLayoutY(30);
        stopSim.setLayoutX(250);
        stopSim.setLayoutY(30);
        initiateAgents.setLayoutX(20.0);
        initiateAgents.setLayoutY(70.0);

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
        root.getChildren().addAll(line1, startServer, startSim,
                stopSim, initiateAgents, client1, client2, client1Input, client2Input);

        // The animation of the agents happens here.
        AnimationTimer at = new AnimationTimer() {

            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Implementing some sort of a delay - Be nice to resources:
                if(now - lastUpdate >= 250_000_000){
                    int c = 0;
                    lock.readLock().lock();
                    for(int i = 0; i < bluePositions.length; i += 2){
                        circlesBlue.get(c).setCenterX(bluePositions[i]);
                        circlesBlue.get(c).setCenterY(bluePositions[i+1]);
                        c++;
                    }
                    c = 0;

                    for(int i = 0; i < orangePositions.length; i += 2){
                        circlesOrange.get(c).setCenterX(orangePositions[i]);
                        circlesOrange.get(c).setCenterY(orangePositions[i+1]);
                        c++;
                    }
                    lock.readLock().unlock();
                    lastUpdate = now;
                }
            }
        };

        // Start the server and then listener from here.
        startServer.setOnAction( e -> {
            new Server().start(); // Runs only one time.
            System.out.println("Server Started");
            startServer.setDisable(true);
            initiateAgents.setDisable(false);
        });

        // Start the simulation here.
        startSim.setOnAction( e -> {
            // Creating circlesOrange:
            if (createCircles) {
               lock.readLock().lock();
               for (int i = 0; i < orangePositions.length; i += 2) {
                   System.out.println("Array Orange X " + orangePositions[i] + " array Orange Y " + orangePositions[i+1]);
                   circlesOrange.add(new Circle(orangePositions[i], orangePositions[i+1], 15, Color.ORANGE));
               }
               for(int i = 0; i < bluePositions.length; i += 2){
                   System.out.println("Array Blue X " + bluePositions[i] + " array Blue Y " + bluePositions[i+1]);
                   circlesBlue.add(new Circle(bluePositions[i], bluePositions[i+1], 10, Color.BLUE));
               }
               root.getChildren().addAll(circlesOrange);
               root.getChildren().addAll(circlesBlue);
               lock.readLock().unlock();

            }
            System.out.println("Blue circle size: " + circlesBlue.size());
            createCircles = false; // With this, circles aren't going to be created again.
            at.start();
            startSim.setDisable(true);
            stopSim.setDisable(false);
        });

        // To stop/   the simulation: Need to check on how to pause the threads too, client's especially.
        stopSim.setOnAction( e -> {
            at.stop();
            stopSim.setDisable(true);
            startSim.setDisable(false);
        });

        // To create agent instances, and handler threads over the network before starting the simulation.
        initiateAgents.setOnAction( e -> {
            aBlue = new AgentHandler("Blue", Integer.parseInt(client1Input.getText()), Color.BLUE);
            aOrange = new AgentHandler("Orange", Integer.parseInt(client2Input.getText()), Color.ORANGE);

            bluePositions = new float[Integer.parseInt(client1Input.getText()) * 2];
            orangePositions = new float[Integer.parseInt(client2Input.getText()) * 2];
            aBlue.start();
            aOrange.start();

            initiateAgents.setDisable(true);
            client1Input.setDisable(true);
            client2Input.setDisable(true);
            startSim.setDisable(false);
        });

    } // Start()

    /**
     * A main method to start the application.
     *
     * @param args
     */
    public static void main(String[] args){
        launch(args);
    }

    /**
     * Purpose: To listen to a port and wait for two handlers to connection.
     *
     */
    private class Server extends Thread{

        private ServerSocket mServerSocket;
        private ServerListener mServerListener;

        /**
         * A run() function that listens to a port and waits for two handlers.
         *
         */
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
            System.out.println("######## Server is shutdown ########");
        }
    }

    /**
     * Purpose: Receive position data from handlers that were connected via the Server class.
     * The data received are directly stored into global variables.
     *
     */
    private class ServerListener extends Thread {

        boolean serverRunning = true;
        ObjectInputStream dinObject = null;
        DataInputStream dinData = null;
        Socket socket = null;
        String ID;

        /**
         * A constructor for the Server Listener class.
         *
         * @param socket The socket corresponding to the handler's.
         *
         */
        public ServerListener(Socket socket){
            this.socket = socket;
        }

        /**
         * A run() function that receives array data containing positions from a handler
         * and saves it into a global variable. It also receives a string containing the
         * ID of the handler.
         *
         */
        @Override
        public void run() {
            try {

                dinObject = new ObjectInputStream(socket.getInputStream());
                dinData = new DataInputStream(socket.getInputStream());

                while (serverRunning) {
                    try {
                        Thread.sleep(100);

                        if((ID = dinData.readUTF()) != null) {
                            if (ID.equals(aBlue.getHandlerID())) {
                                lock.writeLock().lock();
                                bluePositions = (float[]) dinObject.readUnshared();
                                lock.writeLock().unlock();
                            } else if (ID.equals(aOrange.getHandlerID())) {
                                lock.writeLock().lock();
                                orangePositions = (float[]) dinObject.readUnshared();
                                lock.writeLock().unlock();
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
                System.out.println("####### Server listener is terminating #######");
            }
            catch (IOException e) {
                closeThread();
                System.out.println("Error - ServerListener: " + e);
                e.printStackTrace();
            }
        } // run

        /**
         * To close the socket and all streams in case an exception occurs or the while
         * loop has ended for some reason.
         *
         */
        private void closeThread(){
            serverRunning = false;
            try{
                socket.close();
                dinObject.close();
                dinData.close();
                //doutBoolean.close();
            }catch(IOException e){
                System.out.println("Error - closeThread - ServerListener: " + e);
                e.printStackTrace();
            }
        }
    }

} // GUIServer class