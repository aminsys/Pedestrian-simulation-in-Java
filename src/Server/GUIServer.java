package Server;

import Controller.AgentHandler;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class GUIServer extends Application{


    //private float[] posX = new float[3], posY = new float[3];
    private Circle[] circles = new Circle[3];
    private AgentHandler au1;
    private ServerThread st1 = null;

    public void start(Stage window) throws Exception {

        // Creating agents and agent updater:
        au1 = new AgentHandler(3);
        //au1.getAgentPositions(this.posX, this.posY);

        // Setting window and scene:
        window.setTitle("Test Agents");
        Group root = new Group();
        Scene myScene = new Scene(root, 600, 400);

        // Creating circles:
        for(int i = 0; i < 3; i++){
            circles[i] = new Circle(au1.getAgentsXPosition(i), au1.getAgentsYPosition(i), 20, Color.RED);
        }
        root.getChildren().addAll(circles);

        // Show the window with scene:
        window.setScene(myScene);
        window.show();

        Button btn1 = new Button("Start SERVER");
        Button startBtn = new Button("Start sim");
        Button stopBtn = new Button("Stop sim");
        btn1.setLayoutX(40.0);
        btn1.setLayoutY(330.0);
        startBtn.setLayoutY(150);
        startBtn.setLayoutY(360);
        stopBtn.setLayoutX(260);
        stopBtn.setLayoutY(360);
        root.getChildren().addAll(btn1, startBtn, stopBtn);

        btn1.setOnAction( e -> {
            st1 = new ServerThread();
            new Thread(st1).start();
            btn1.setDisable(true);
        });

        AnimationTimer at = new AnimationTimer() {

            private long lastUpdate = 0;
            private float x, y;

            @Override
            public void handle(long now) {
                // Implementing some sort of a delay:
                if(now - lastUpdate >= 300_000_000){
                    // Update positions:
                    au1.moveForward();
                    au1.checkCollisions();
                    for(int i = 0; i < 3; i++){
                        x = au1.getAgentsXPosition(i);
                        y = au1.getAgentsYPosition(i);
                        circles[i].setTranslateX(x);
                        circles[i].setTranslateY(y);
                    }
                    lastUpdate = now;
                }
            }
        };

        startBtn.setOnAction( e -> {
            at.start();
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        });

        stopBtn.setOnAction( e -> {
            at.stop();
            stopBtn.setDisable(true);
            startBtn.setDisable(false);
        });

    }

    public static void main(String[] args){
        launch(args);
    }
}