package ClientModel;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class Agent implements Serializable{

    private float posX, posY, goalX, goalY, speed;
    private transient Color color;

    // Random start position: 1 - 10 at X, 10 - 310 at Y.
    // Random goal position: ~600 at X, 10 - 310 at Y.
    public Agent(Color color){

        this.color = color;
        if(color == Color.BLUE){
            // Start at the left of the window.
            this.posX = 0.0f;
            this.goalX = 800.0f;
            this.posY = ThreadLocalRandom.current().nextFloat() * 200.0f + 100.0f;
            this.goalY = ThreadLocalRandom.current().nextFloat() * 200.0f + 150.0f;
        }

        else if(color == Color.ORANGE){
            // Start at the right of the window.
            this.posX = 800.0f;
            this.goalX = 0.0f;
            this.posY = ThreadLocalRandom.current().nextFloat() * 200.0f + 100.0f;
            this.goalY = ThreadLocalRandom.current().nextFloat() * 200.0f + 150.0f;
        }

        else {
            System.out.println("You have't entered neither blue nor orange color.");
            return; // Stop the instantiation of the object.
        }

        //this.posY = ThreadLocalRandom.current().nextFloat() * 200.0f + 100.0f;
        //this.speed = ThreadLocalRandom.current().nextFloat() * 2.0f;
        this.speed = 2.0f;
        System.out.println("(x, y, goalX, goalY): (" + this.posX + ' ' + this.posY + ' ' + this.goalX + ' ' + this.goalY + ')');
        System.out.println("Speed is: " + this.speed);
    }

    public void setPosX(float x){
        this.posX = x;
    }

    public float getPosX() {
        return this.posX;
    }

    public void setPosY(float y) {
        this.posY = y;
    }

    public float getPosY(){
        return this.posY;
    }

    public float getSpeed(){
        return this.speed;
    }

    public void changeDirection(){
        if(this.posX < this.goalX){
            this.posX += 0.1f;
        }
        if(this.posX > this.goalX){
            this.posX -= 0.1f;
        }
        if(this.posY < this.goalY){
            this.posY += 0.1f;
        }
        if(this.posY > this.goalY){
            this.posY -= 0.1f;
        }
    }

    public void moveToGoal(){
        if(this.posX < this.goalX){
            this.posX += this.speed;
        }
        if(this.posX > this.goalX){
            this.posX -= this.speed;
        }
        if(this.posY < this.goalY){
            this.posY += this.speed;
        }
        if(this.posY > this.goalY){
            this.posY -= this.speed;
        }
    }

} // Agent
