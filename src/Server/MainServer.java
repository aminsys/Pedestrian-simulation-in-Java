package Server;

import ClientModel.Agents;
import Controller.AgentUpdater;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class MainServer extends Application{

    private AgentUpdater a1;
    private Agents agentTest;

    public void start(Stage window) throws Exception {
        window.setTitle("Test Agents");
        Group root = new Group();

        Scene myScene = new Scene(root, 600, 400);
        agentTest = new Agents();
        a1 = new AgentUpdater(myScene, agentTest);
        // Use new Animation to update your agents.

        window.setScene(myScene);
        window.show();
    }



    public static void main(String[] args){
        launch(args);
    }
}