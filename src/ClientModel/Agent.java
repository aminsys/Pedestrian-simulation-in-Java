package ClientModel;

import javafx.scene.paint.Color;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Purpose: A representation of an agent (pedestrian). It has X-Y start and goal positions
 * as well as speed. An agent will only move towards its goal destination, and it will
 * eventually try to avoid colliding with other agents of its kind.
 *
 * @author Amin Yassin
 * @version 1.0 03/01/2018
 *
 */
public class Agent{

    private float posX, posY, goalX, goalY, speed;

    /**
     * Constructor of an Agent (pedestrian). Y start and goal positions are randomized
     * between 120.0 and 570.0. Speed is also randomized between 0.0 to 1.0.
     *
     * @param color This color must only be either blue or orange.
     */
    public Agent(Color color){

        if(color == Color.BLUE){
            // Start at the left of the window.
            this.posX = 0.0f;
            this.goalX = 800.0f;
        }

        else if(color == Color.ORANGE){
            // Start at the right of the window.
            this.posX = 800.0f;
            this.goalX = 0.0f;
        }

        else {
            System.out.println("You have't entered neither blue nor orange color.");
            return; // Stop the instantiation of the object.
        }

        this.posY = ThreadLocalRandom.current().nextFloat() * 450.0f + 120.0f;
        this.goalY = ThreadLocalRandom.current().nextFloat() * 450.0f + 120.0f;
        this.speed = ThreadLocalRandom.current().nextFloat();

        System.out.println("(x, y, goalX, goalY): (" + this.posX + ' ' + this.posY + ' ' + this.goalX + ' ' + this.goalY + ')');
        System.out.println("Speed is: " + this.speed);
    }

    /**
     * Returns the X position of the Agent.
     *
     * @return the X position.
     */
    public float getPosX() {
        return this.posX;
    }

    /**
     * Returns the Y position of the Agent.
     *
     * @return the Y position.
     */
    public float getPosY(){
        return this.posY;
    }

    /**
     * Moves an Agent faster towards its goal destination.
     *
     */
    public void tryAvoidingCollision(){
        if(this.posX < this.goalX){
            this.posX += 0.5f;
        }
        if(this.posX > this.goalX){
            this.posX -= 0.5f;
        }
        if(this.posY < this.goalY){
            this.posY += 0.5f;
        }
        if(this.posY > this.goalY){
            this.posY -= 0.5f;
        }
    }

    /**
     * An agent checks its distance to goal and moves towards it.
     *
     */
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
