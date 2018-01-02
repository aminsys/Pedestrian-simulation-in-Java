package Controller;

import ClientModel.Agent;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/*
* 1. I wished to be able to send the Color of the group, but for now
* I will just send a string identifying the group.
* */

public class AgentHandler extends Thread {

    private List<Agent> agentList = new ArrayList<>();
    private Color color;
    private String ID;
    float[] arrayPos;


    public AgentHandler(String ID, int amount, Color color){
        for (int i = 0; i < amount; i++){
            Agent a = new Agent(color);
            agentList.add(a);
        }
        this.color = color;
        this.ID = ID;
        System.out.println("Amount of agents created: " + agentList.size());
    }


    public void collisionCheck(){
        for(int i = 0; i < agentList.size() - 1; i++){
            for(int j = i+1; j < agentList.size(); j++){
                if(Math.abs(agentList.get(i).getPosX() - agentList.get(j).getPosX()) < 20){
                    agentList.get(j).changeDirection();
                }
                if(Math.abs(agentList.get(i).getPosY() - agentList.get(j).getPosY()) < 20){
                    agentList.get(j).changeDirection();
                }
            }
        }
    }

    public Color getColor(){
        return color;
    }

    public String getID(){
        return this.ID;
    }


    @Override
    public void run() {

        System.out.println("Thread started!");
        final String localhost = "127.0.0.1";
        final int PORT = 1337;
        boolean running = true;
        DataInputStream dinBoolean = null;
        DataOutputStream dData = null;
        ObjectOutputStream doutObject = null;
        Socket clientSocket = null;

        try {
            clientSocket = new Socket(localhost, PORT);
            doutObject = new ObjectOutputStream(clientSocket.getOutputStream());
            dinBoolean = new DataInputStream(clientSocket.getInputStream());
            dData = new DataOutputStream(clientSocket.getOutputStream());

            while (running) {

                Thread.sleep(100);
                dData.writeUTF(getID()); // ID of agent handler.
                fillList(agentList);
                doutObject.writeUnshared(arrayPos);
                //System.out.println("Data sent!");

            }
            closeConnections(clientSocket, doutObject, dinBoolean, dData);
            System.out.println("########AgentHandler is Terminating########");

        } catch (IOException e) {
            closeConnections(clientSocket, doutObject, dinBoolean, dData);
            System.out.println("Error - AgentHandler: " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void closeConnections(Socket s, ObjectOutputStream obj, DataInputStream din, DataOutputStream dout){
        try{
            s.close();
        }catch(IOException e){
            System.out.println("Error - CloseConnection - AgentHandler: " + e);
            e.printStackTrace();
        }
    }

    private void fillList(List<Agent> a){
        int c = 0;
        arrayPos = new float[a.size() * 2];
        for(int i = 0; i < a.size(); i++){
            arrayPos[c] = a.get(i).getPosX();
            arrayPos[c+1] = a.get(i).getPosY();
            collisionCheck();
            a.get(i).moveToGoal();
            c += 2;
        }
    }


}
