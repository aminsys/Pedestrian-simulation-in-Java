package Server;

import Controller.AgentHandler;
import Controller.DataPackage;
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
 * <br>
 * Input fields are used to register the amount of agents desired to initiate before
 * initiating the agent handlers.
 * <br>
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
     * A costume data type to pack in different data types to be sent to AgentHandlers.
     */
    public DataPackage datapackageBlue = null, datapackageOrange = null;
    public static int counter = 0;

    /**
     * To store data received from handlers.
     */
    public float[] bluePositions, orangePositions;

    /**
     * To store the representations of agents in ArrayLists.
     */
    private ArrayList<Circle> circlesOrange = new ArrayList<>();
    private ArrayList<Circle> circlesBlue = new ArrayList<>();
    private ArrayList<ServerListener> connections = null;

    /**
     * Creating circlesOrange for the first time.
     */
    private boolean createCircles = true;


    /**
     * Here all parts of the application window are managed (Buttons, input fields, circles).
     * <br>
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
                    synchronized (bluePositions) {
                        for (int i = 0; i < bluePositions.length; i += 2) {
                            circlesBlue.get(c).setCenterX(bluePositions[i]);
                            circlesBlue.get(c).setCenterY(bluePositions[i + 1]);
                            c++;
                        }
                    }
                    c = 0;

                    synchronized (orangePositions) {
                        for (int i = 0; i < orangePositions.length; i += 2) {
                            circlesOrange.get(c).setCenterX(orangePositions[i]);
                            circlesOrange.get(c).setCenterY(orangePositions[i + 1]);
                            c++;
                        }
                    }
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
                synchronized (orangePositions) {
                    for (int i = 0; i < orangePositions.length; i += 2) {
                        System.out.println("Array Orange X " + orangePositions[i] + " array Orange Y " + orangePositions[i + 1]);
                        circlesOrange.add(new Circle(orangePositions[i], orangePositions[i + 1], 15, Color.ORANGE));
                    }
                }

                synchronized (bluePositions) {
                    for (int i = 0; i < bluePositions.length; i += 2) {
                        System.out.println("Array Blue X " + bluePositions[i] + " array Blue Y " + bluePositions[i + 1]);
                        circlesBlue.add(new Circle(bluePositions[i], bluePositions[i + 1], 10, Color.BLUE));
                    }
                }
               root.getChildren().addAll(circlesOrange);
               root.getChildren().addAll(circlesBlue);

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
            //aBlue = new AgentHandler("Blue", Integer.parseInt(client1Input.getText()), Color.BLUE);
            //aOrange = new AgentHandler("Orange", Integer.parseInt(client2Input.getText()), Color.ORANGE);
            datapackageBlue = new DataPackage("blue", Integer.parseInt(client1Input.getText()), Integer.parseInt(client2Input.getText()));
            datapackageOrange = new DataPackage("orange", Integer.parseInt(client2Input.getText()), Integer.parseInt(client1Input.getText()));

            bluePositions = new float[Integer.parseInt(client1Input.getText()) * 2];
            orangePositions = new float[Integer.parseInt(client2Input.getText()) * 2];

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
            //int clientCounter = 0;
            try {
                mServerSocket = new ServerSocket(1337);
                connections = new ArrayList<ServerListener>();

                while (counter < 2) {
                    System.out.println("Waiting for a connection...");
                    Socket s = mServerSocket.accept();
                    System.out.println("Connection established");
                    counter++;
                    // Start listening on connection:
                    mServerListener = new ServerListener(s);
                    connections.add(mServerListener);
                    mServerListener.start();
                }
                mServerSocket.close();

            }catch(IOException e){
                System.out.println("Server: Error: " + e);
                e.printStackTrace();
            }
            System.out.println("######## Server is shutdown ########");
        }
    }

    /**
     * Purpose: Receive position data from handlers that were connected via the Server class.
     * <br>
     * The data received are directly stored into global variables.
     *
     */
    private class ServerListener extends Thread {

        private boolean serverRunning = true;
        private ObjectInputStream dinObject = null;
        private ObjectOutputStream doutObject = null;
        private DataInputStream dinData = null;
        private DataOutputStream doutData = null;
        private Socket socket = null;

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
         * A method that sends to all connected handlers how many of them are connected.
         *
         * @param n
         */
        public void sendToAllHandlers(int n){
            try{
            doutData.writeInt(n);
            } catch(IOException e){
                System.err.println("Error - ServerListener: " + e);
                e.printStackTrace();
            }
        }

        /**
         * A run() function that receives array data containing positions from a handler
         * and saves it into a global variable. It also receives a string containing the
         * ID of the handler.
         *
         */
        @Override
        public void run() {
            String ID;

            try {

                dinObject = new ObjectInputStream(socket.getInputStream());
                doutObject = new ObjectOutputStream(socket.getOutputStream());
                dinData = new DataInputStream(socket.getInputStream());
                doutData = new DataOutputStream(socket.getOutputStream());

                // If the first handler is connected.
                if(counter == 1){
                    doutObject.writeUnshared(datapackageBlue);
                    System.out.println("Data package for blue is sent!");
                }

                // If the second handler has connected.
                else if(counter == 2){
                    // This data package contains all the information it needs about number of
                    // agents, their color, and the amount of agents that the other handler has.
                    doutObject.writeUnshared(datapackageOrange);
                    System.out.println("Data package for orange is sent!");
                }

                // Broadcast to all handlers the number of them connected.
                for (int i = 0; i < connections.size(); ++i){
                    connections.get(i).sendToAllHandlers(counter);
                }

                while (serverRunning) {
                    try {
                        ID = dinData.readUTF();
                        switch (ID){
                            case "blue":
                                synchronized (bluePositions){
                                    bluePositions = (float[]) dinObject.readUnshared();
                                    doutObject.writeUnshared(orangePositions);
                                }
                                break;

                            case "orange":
                                synchronized (orangePositions){
                                    orangePositions = (float[]) dinObject.readUnshared();
                                    doutObject.writeUnshared(bluePositions);
                                }
                                break;

                            default:
                                break;
                        }
                    }
                    catch(EOFException eof){
                        closeThread();
                        System.err.println("Error - ServerListener - readObject: " + eof);
                        eof.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        closeThread();
                        System.err.println("Error - ServerListener - readObject: " + e);
                        e.printStackTrace();
                    }
                }
                closeThread();
                System.out.println("####### Server listener is terminating #######");
            }
            catch (IOException e) {
                closeThread();
                System.err.println("Error - ServerListener: " + e);
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
                doutObject.close();
            }catch(IOException e){
                System.err.println("Error - closeThread - ServerListener: " + e);
                e.printStackTrace();
            }
        }
    }

} // GUIServer class