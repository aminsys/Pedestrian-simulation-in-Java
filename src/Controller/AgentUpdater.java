package Controller;

import ClientModel.Agents;
import javafx.scene.Scene;


public class AgentUpdater {

    private Scene mScene;
    private Agents agent;

    public AgentUpdater(Scene ms, Agents agent){
        this.mScene = ms;
        this.agent = agent;

        // Update with key press:
        this.mScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case RIGHT:
                    System.out.println(this.agent.getPosX());
                    break;
                default:
                    System.out.println("Something wrong with pressing");
                    break;
            }
        });

    }

    public void updatePosition(Agents agent, float x){
        this.agent.setPosX(this.agent.getPosX() + x);
    }

    public float currentPosition(Agents agent){
        return this.agent.getPosX();
    }




}
