package Server;

import ClientModel.Agents;

import java.util.ArrayList;
import java.util.List;

public class AgentUpdater implements Movement {

    private List<Agents> agentContainer = new ArrayList<Agents>();
    private Agents testAgent;


    // Adding agents into a list:
    public AgentUpdater(int amount) {
        for( int i = 0; i < amount; i++){
            agentContainer.add(new Agents());
        }
        System.out.println("Agent list size: " + agentContainer.size());
    }

    public Agents getAgent(int i){
        return this.agentContainer.get(i);
    }

    // Single test agent.
    /*public AgentUpdater(){
        this.testAgent = new Agents(10.0f, 20.0f);
    }*/

    @Override
    public void moveForward(float x) {
        for (int i = 0; i < this.agentContainer.size(); i++){
            Agents a = this.agentContainer.get(i);
            a.setPosX(a.getPosX() + x);
        }
    }

    // Implement other movements:

    @Override
    public float currentPositionX(int i){
        return this.agentContainer.get(i).getPosX();
    }

    @Override
    public float currentPositionY(){
        return this.testAgent.getPosY();
    }

    public int agentListSize(){
        return this.agentContainer.size();
    }

    // Check if agents reached their goals.
    public void amIAtGoal(){
        for(int i = 0; i < agentContainer.size(); i++){
            Agents a = this.agentContainer.get(i);
            a.isGoal();
        }
    }
}
