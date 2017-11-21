package Controller;

import ClientModel.Agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AgentHandler implements Serializable {

    private List<Agents> agentList = new ArrayList<Agents>();

    public AgentHandler(int amount){
        for (int i = 0; i < amount; i++){
            Agents a = new Agents();
            agentList.add(a);
        }
        System.out.println("Amount of agents created: " + agentList.size());
    }

    public float getAgentsXPosition(int i){
        return agentList.get(i).getPosX();
    }

    public float getAgentsYPosition(int i){
        return agentList.get(i).getPosY();
    }

    // This is for the circles:
    /*public void getAgentPositions(float[] posX, float[] posY){
        for (int i = 0; i < agentList.size(); i++){
            posX[i] = agentList.get(i).getPosX();
            posY[i] = agentList.get(i).getPosY();
        }
    }*/

    public void moveForward(){
        for(int i = 0; i < agentList.size(); i++){
            agentList.get(i).moveToGoal();
        }
    }

    public void checkCollisions(){
        Agents a, b, c;
        a = agentList.get(0);
        b = agentList.get(1);
        c = agentList.get(2);

        a.distanceDifference(b);
        a.distanceDifference(c);
        b.distanceDifference(c);
    }
}
