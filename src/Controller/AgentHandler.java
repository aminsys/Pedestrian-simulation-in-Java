package Controller;

import ClientModel.Agent;
import ClientModel.DataPackage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Purpose: A class that represents an instance of agent handler.
 * An agent handler is where all agents of the same colour are handled;
 * Collision checking amongst the same agents and against the others,
 * updating actual positions of the corresponding agents, respawn at new
 * positions, etc.
 * <br>
 * The positions of the agents are sent via this class, also, it receives the
 * positions of the opposite agents' positions.
 *
 * @author Amin Yassin
 * @version 2.0 29/01/2018
 *
 */
public class AgentHandler extends Thread {

    private List<Agent> agentList = new ArrayList<>();
    private String handlerID;
    private float[] xyPositions;
    private float[] oppositeXY;

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
     * Checks the distance difference between agents in the same group, and
     * thus, if one is in collision direction, it tries to make space for the
     * other agent to pass by.
     * The function is optimized so that agents that were already checked
     * aren't checked again.
     *
     */
    private void checkCollisionSameGroup(){

        int c = 0;
        float x, y;

        if(getHandlerID().equalsIgnoreCase("orange")) {
            for (int i = 0; i < xyPositions.length - 2; i += 2) {
                for (int j = i + 2; j < xyPositions.length; j += 2) {
                    x = xyPositions[i] - xyPositions[j];
                    y = xyPositions[i+1] - xyPositions[j+1];

                    if(Math.abs(x) < 50.0f && Math.abs(y) < 50.0f){
                        if(Math.signum(y) == -1){
                            agentList.get(c).moveUpAndForth();
                        }
                        else if(Math.signum(y) == 1){
                            agentList.get(c).moveDownAndForth();
                        }
                    }
                }
                c++;
            }
        }

        if(getHandlerID().equalsIgnoreCase("blue")){
            for (int i = 0; i < xyPositions.length - 2; i += 2) {
                for (int j = i + 2; j < xyPositions.length; j += 2) {
                    x = xyPositions[i] - xyPositions[j];
                    y = xyPositions[i+1] - xyPositions[j+1];

                    if(Math.abs(x) < 50.0f && Math.abs(y) < 50.0f){
                        if(Math.signum(y) == -1){
                            agentList.get(c).moveUpAndBack();
                        }
                        else if(Math.signum(y) == 1){
                            agentList.get(c).moveDownAndBack();
                        }
                    }
                }
                c++;
            }
        }
    }

    /**
     * This method checks all of opposite agents' positions and once they are
     * in range of each other, they try avoiding collision by stepping to the
     * right or left depending on orientation.
     *
     */
    private void checkCollisionOtherGroup(){
        float x, y;
        int c = 0;
        if(getHandlerID().equalsIgnoreCase("orange")){
            for(int i = 0; i < xyPositions.length; i += 2){
                for(int j = 0; j < oppositeXY.length; j += 2){
                    x = xyPositions[i] - oppositeXY[j];
                    y = xyPositions[i+1] - oppositeXY[j+1];

                    if(Math.abs(x) < 40.0f && Math.abs(y) < 40.0f){
                        if(Math.signum(y) == -1){
                            agentList.get(c).moveUpAndBack();
                        }
                        else if(Math.signum(y) == 1){
                            agentList.get(c).moveDownAndBack();
                        }
                    }
                }
                agentList.get(c).moveToGoal();
                c++;
            }
        }

        if(getHandlerID().equalsIgnoreCase("blue")){
            for(int i = 0; i < xyPositions.length ; i += 2){
                for(int j = 0; j < oppositeXY.length; j += 2){
                    x = xyPositions[i] - oppositeXY[j];
                    y = xyPositions[i+1] - oppositeXY[j+1];

                    if(Math.abs(x) < 40.0f && Math.abs(y) < 40.0f){
                        if(Math.signum(y) == -1){
                            agentList.get(c).moveUpAndForth();
                        }
                        else if(Math.signum(y) == 1){
                            agentList.get(c).moveDownAndForth();
                        }
                    }
                }
                agentList.get(c).moveToGoal();
                c++;
            }
        }
    }

    /**
     * Run method for the class. Connection to the server is established here.
     * Once connection is established, this instance receives a data package
     * containing information about this instance and the opposite instance
     * of the same class; number of agents for each instance, colour etc.
     * Coordinates are then sent from this function to the server and
     * opposite agents' coordinates are received to check for collisions.
     *
     */
    @Override
    public void run() {

        System.out.println("Agent Handler started!");
        final String localhost = "127.0.0.1";
        final int PORT = 1337;
        boolean running = true;
        DataOutputStream dData = null;
        DataInputStream dinData = null;
        ObjectOutputStream doutObject = null;
        ObjectInputStream dinObject = null;
        Socket clientSocket = null;
        int numberOfHandlersConnected = 0;

        try {
            clientSocket = new Socket(localhost, PORT);
            doutObject = new ObjectOutputStream(clientSocket.getOutputStream());
            dinObject = new ObjectInputStream(clientSocket.getInputStream());
            dData = new DataOutputStream(clientSocket.getOutputStream());
            dinData = new DataInputStream(clientSocket.getInputStream());

            // Receive data package from Server ================================
            DataPackage datapackage = (DataPackage) dinObject.readUnshared();
            for (int i = 0; i < datapackage.getAmount(); i++){
                Agent a = new Agent(datapackage.getID());
                agentList.add(a);
            }
            this.handlerID = datapackage.getID();
            System.out.println("Amount of agents created: " + agentList.size());
            oppositeXY = new float[datapackage.getAmountOfOtherAgents() * 2];
            //==================================================================
            // Initiate arrays to hold positions for the first time.
            fillList(agentList);

            while (running) {

                Thread.sleep(50);
                if(numberOfHandlersConnected == 2) {
                    checkCollisionSameGroup();
                    checkCollisionOtherGroup();
                    fillList(agentList);
                    dData.writeUTF(getHandlerID());
                    doutObject.writeUnshared(xyPositions);

                    // Receive positions of the other agents:
                    oppositeXY = (float[]) dinObject.readUnshared();
                }
                else {
                    // This block is only entered twice.
                    numberOfHandlersConnected = dinData.readInt();
                    System.out.println("Number of handlers: " +
                            numberOfHandlersConnected);
                }
            }
            closeConnections(clientSocket, doutObject, dinObject, dData, dinData);
            System.out.println("######## AgentHandler is Terminating ########");

        } catch (IOException e) {
            closeConnections(clientSocket, doutObject, dinObject, dData, dinData);
            System.err.println("Error - AgentHandler: " + e);
            e.printStackTrace();
        }  catch (ClassNotFoundException e) {
            closeConnections(clientSocket, doutObject, dinObject, dData, dinData);
            System.err.println("Error - AgentHandler: " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            closeConnections(clientSocket, doutObject, dinObject, dData, dinData);
            e.printStackTrace();
        }
    }

    /**
     * To close all the socket and data streams if an exceptions occurs or the
     * run() function's while loop has finished.
     *
     * @param socket The socket corresponding to its handler.
     * @param doutObj The object stream that the positions are sent through.
     * @param dinObj The object stream that receives positions from server.
     * @param dout A stream to send the ID of the handler to the server.
     * @param din A stream to receive data from server.
     *
     */
    private void closeConnections(Socket socket, ObjectOutputStream doutObj,
                                  ObjectInputStream dinObj,
                                  DataOutputStream dout,
                                  DataInputStream din){
        try{
            socket.close();
            doutObj.close();
            dinObj.close();
            dout.close();
            din.close();
        }catch(IOException e){
            System.err.println("Error - CloseConnection - AgentHandler: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Fills the position array with the current positions of all the relevant
     * agents before updating them.
     * Also, if an agent has reached its goal, new start and goal positions
     * will be assigned to the same agent; As to simulate the spawning of a
     * new agent.
     *
     * @param agents A list containing all the agents a handler instance has.
     *
     */
    private void fillList(List<Agent> agents){
        int c = 0;
        float x;
        xyPositions = new float[agents.size() * 2];
        for(int i = 0; i < agents.size(); i++){

            x = agents.get(i).getPosX();

            // Check if goal reached.
            if(x < -10.0f &&
                    getHandlerID().equalsIgnoreCase("orange")){
                agents.get(i).setPosX(800.0f);
                agents.get(i).setPosY(ThreadLocalRandom.current().nextFloat()
                        * 450.f + 120.0f);
                agents.get(i).setGoalY(ThreadLocalRandom.current().nextFloat
                        () * 450.f + 120.0f);
            }

            // Check if goal reached.
            else if(x > 810.0f &&
                    getHandlerID().equalsIgnoreCase("blue")){
                agents.get(i).setPosX(0.0f);
                agents.get(i).setPosY(ThreadLocalRandom.current().nextFloat()
                        * 450.f + 120.0f);
                agents.get(i).setGoalY(ThreadLocalRandom.current().nextFloat
                        () * 450.f + 120.0f);
            }

            xyPositions[c] = agents.get(i).getPosX();
            xyPositions[c+1] = agents.get(i).getPosY();
            c += 2;
        }
    }


}
