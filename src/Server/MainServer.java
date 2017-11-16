package Server;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class MainServer extends Application{


    //private AgentUpdater x = new AgentUpdater(); // Single test agent is created.
    private AgentUpdater x = new AgentUpdater(3); // 3 agents are created.
    private List<Circle> circles = new ArrayList<Circle>();
    //private Circle[] circles = new Circle[x.agentListSize()];

    public void start(Stage window) throws Exception {
        window.setTitle("Test Agents");
        Group root = new Group();

        //Circle c1 = new Circle(x.currentPositionX(), x.currentPositionY(), 25, Color.ORANGE); // An agent.
        // Create circles for each Agent.
        for (int i = 0; i < x.agentListSize(); i++) {
            //New circles in circle container.
            //circles[i] = new Circle(x.currentPositionX(), x.currentPositionY(), 20);
            circles.add(new Circle(x.getAgent(i).getPosX(), x.getAgent(i).getPosY(), 20, Color.ORANGE));
        }

        Scene myScene = new Scene(root, 600, 400);
        //root.getChildren().add();
        root.getChildren().addAll(circles);

        // Update with key press:
        myScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case RIGHT:
                    x.moveForward(1.5f);
                    break;
                default:
                        System.out.println("Something wrong with pressing");
                    break;
                }
            });

            // Use new Animation to update your agents.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                    //System.out.println("Change in X-axis.");
                    //c1.setTranslateX(x.currentPositionX());
                for(int i = 0; i < x.agentListSize(); i++){
                    circles.get(i).setTranslateX(x.currentPositionX(i));
                //circles[i].setTranslateX(x.currentPositionX());
                }
                x.moveForward(1.0f);
                x.amIAtGoal();
            }
        }.start();

        window.setScene(myScene);
        window.show();
    }



    public static void main(String[] args){
        launch(args);
    }
}