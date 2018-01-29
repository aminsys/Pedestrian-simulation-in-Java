package ClientModel;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Purpose: A representation of an agent (pedestrian). It has X-Y start and
 * goal positions as well as predetermined speed. An agent will only move
 * towards its goal destination, and it will try to avoid colliding with
 * other agents, either of its same colour or of the opposite colour.
 *
 * @author Amin Yassin
 * @version 2.0 29/01/2018
 *
 */
public class Agent{

    private float posX, posY, goalX, goalY, speed;

    /**
     * Constructor of an Agent (pedestrian). Y start and goal positions are
     * randomized between 120.0 and 570.0. Speed is also randomized between
     * 0.1 to 1.0.
     *
     * @param colorID This color string must only be either blue or orange.
     *
     */
    public Agent(String colorID){

        if(colorID.equalsIgnoreCase("blue")){
            // Start at the left of the window.
            this.posX = 0.0f;
            this.goalX = 812.0f;
        }

        else if(colorID.equalsIgnoreCase("orange")){
            // Start at the right of the window.
            this.posX = 800.0f;
            this.goalX = -12.0f;
        }

        else {
            System.out.println("You have't entered " +
                    "neither blue nor orange color.");
            return; // Stop the instantiation of the object.
        }

        this.posY = ThreadLocalRandom.current().nextFloat() * 450.0f + 120.0f;
        this.goalY = ThreadLocalRandom.current().nextFloat() * 450.0f + 120.0f;
        this.speed = ThreadLocalRandom.current().nextFloat() + 0.1f;

        System.out.println("(x, y, goalX, goalY): (" + this.posX + ' ' +
                this.posY + ' ' + this.goalX + ' ' + this.goalY + ')');
        System.out.println("Speed is: " + this.speed);
    }

    /**
     * Returns the X position of the Agent.
     *
     * @return The current X position.
     *
     */
    public float getPosX() {
        return this.posX;
    }

    /**
     * Sets the new X start positions of an agent that has reached its goal
     * previously.
     *
     * @param posX The new X start position.
     *
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * Sets the new Y start position of an agent that has has reached its
     * goal previously.
     *
     * @param posY The new Y start position.
     *
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }

    /**
     * Returns the Y position of the Agent.
     *
     * @return The current Y position.
     *
     */
    public float getPosY(){
        return this.posY;
    }

    /**
     * Sets the Y goal position of an agent that has reached its goal
     * previously.
     *
     * @param goalY The new Y goal position.
     *
     */
    public void setGoalY(float goalY) {
        this.goalY = goalY;
    }

    /**
     * A method the changes the direction of an agent relative to other
     * agents that are in a collision direction to it.
     * <br>
     * The naming of this method is correct only if the left side of the
     * application window is considered as a reference point.
     *
     */
    public void moveDownAndBack(){
        this.posY += 2.5f;
        this.posX -= this.speed;
    }

    /**
     * A method the changes the direction of an agent relative to other
     * agents that are in a collision direction to it.
     * <br>
     * The naming of this method is correct only if the left side of the
     * application window is considered as a reference point.
     *
     */
    public void moveUpAndBack(){
        this.posY -= 2.5f;
        this.posX -= this.speed;
    }

    /**
     * A method the changes the direction of an agent relative to other
     * agents that are in a collision direction to it.
     * <br>
     * The naming of this method is correct only if the left side of the
     * application window is considered as a reference point.
     *
     */
    public void moveUpAndForth(){
        this.posY -= 2.5f;
        this.posX += this.speed;
    }

    /**
     * A method the changes the direction of an agent relative to other
     * agents that are in a collision direction to it.
     * <br>
     * The naming of this method is correct only if the left side of the
     * application window is considered as a reference point.
     *
     */
    public void moveDownAndForth(){
        this.posY += 2.5f;
        this.posX += this.speed;
    }

    /**
     * An agent checks its distance to goal and moves towards it.
     *
     */
    public void moveToGoal(){
        if(this.posX < this.goalX){
            this.posX += this.speed * 2.0f;
        }
        if(this.posX > this.goalX){
            this.posX -= this.speed * 2.0f;
        }
        if(this.posY < this.goalY){
            this.posY += this.speed * 2.0f;
        }
        if(this.posY > this.goalY){
            this.posY -= this.speed * 2.0f;
        }
    }


    /*public void moveUp(){ this.posY--; }

    public void moveLeft(){
        this.posX -= this.speed;
    }

    public void moveRight(){
        this.posX += this.speed;
    }

    public void moveDown() { this.posY++; }*/

} // Agent
