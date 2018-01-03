package Controller;

import ClientModel.Agent;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Purpose: A thread class that can be started via the server simulator.
 * AgentHandler manages a group of agents that have the same color.
 * An AgentHandler checks for collisions, updates the group's positions, and
 * sends position data over a socket to the server.
 *
 * @author Amin Yassin
 * @version 1.0 03/01/2018
 *
 */
public class AgentHandler extends Thread {

    private List<Agent> agentList = new ArrayList<>();
    private String handlerID;
    float[] xyPositions;


    /**
     * Constructor of the AgentHandler.
     *
     * @param handlerID A string that identifies the AgentHandler.
     * @param amount Specifies the amount of agents to be created.
     * @param color Pass this parameter to respective agent in Agent class.
     *
     */
    public AgentHandler(String handlerID, int amount, Color color){
        for (int i = 0; i < amount; i++){
            Agent a = new Agent(color);
            agentList.add(a);
        }
        this.handlerID = handlerID;
        System.out.println("Amount of agents created: " + agentList.size());
    }

    /**
     * A getter function that returns the ID of the handler.
     *
     * @return A string representation of the AgentHandler.
     *
     */
    public String getHandlerID(){
        return this.handlerID;
    }

    /**
     * Checks the distance difference between agents in the same group. The function is
     * optimized so that agents that were already checked aren't checked again.
     *
     */
    public void collisionChecker(){
        for(int i = 0; i < agentList.size() - 1; i++){
            for(int j = i+1; j < agentList.size(); j++){
                if(Math.abs(agentList.get(i).getPosX() - agentList.get(j).getPosX()) < 30.0f){
                    agentList.get(j).tryAvoidingCollision();
                }
                if(Math.abs(agentList.get(i).getPosY() - agentList.get(j).getPosY()) < 30.0f){
                    agentList.get(j).tryAvoidingCollision();
                }
            }
        }
    }

    /**
     * Run method for the class. Connection to the server is established here.
     * Values are sent from this function to the server.
     *
     */
    @Override
    public void run() {

        System.out.println("Agent Handler started!");
        final String localhost = "127.0.0.1";
        final int PORT = 1337;
        boolean running = true;
        //DataInputStream dinBoolean = null;
        DataOutputStream dData = null;
        ObjectOutputStream doutObject = null;
        Socket clientSocket = null;

        try {
            clientSocket = new Socket(localhost, PORT);
            doutObject = new ObjectOutputStream(clientSocket.getOutputStream());
            //dinBoolean = new DataInputStream(clientSocket.getInputStream());
            dData = new DataOutputStream(clientSocket.getOutputStream());

            while (running) {

                Thread.sleep(100);
                dData.writeUTF(getHandlerID()); // handlerID of agent handler.
                fillList(agentList);
                doutObject.writeUnshared(xyPositions);
            }
            closeConnections(clientSocket, doutObject, dData);
            System.out.println("######## AgentHandler is Terminating ########");

        } catch (IOException e) {
            closeConnections(clientSocket, doutObject, dData);
            System.out.println("Error - AgentHandler: " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Error - AgentHandler: " + e);
            e.printStackTrace();
        }
    }

    /**
     * To close all the socket and data streams if an exceptions occurs or the
     * run() function's while loop has finished.
     *
     * @param socket The socket corresponding to its handler.
     * @param obj The object stream that the positions are sent through.
     * @param dout A stream to send the ID of the handler to the server.
     *
     */
    private void closeConnections(Socket socket, ObjectOutputStream obj, DataOutputStream dout){
        try{
            socket.close();
            obj.close();
            dout.close();
        }catch(IOException e){
            System.out.println("Error - CloseConnection - AgentHandler: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Fills the position array with the current positions of all the agents before updating them.
     * Also, before updating the positions, a collision check is made.
     *
     * @param agents A list containing all the agents a handler has.
     *
     */
    private void fillList(List<Agent> agents){
        int c = 0;
        xyPositions = new float[agents.size() * 2];
        for(int i = 0; i < agents.size(); i++){
            xyPositions[c] = agents.get(i).getPosX();
            xyPositions[c+1] = agents.get(i).getPosY();
            collisionChecker();
            agents.get(i).moveToGoal();
            c += 2;
        }
    }


}
