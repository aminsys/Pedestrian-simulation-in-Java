package ClientModel;

import java.util.concurrent.ThreadLocalRandom;

public class Agents {

    private float posX, posY, goalX, goalY, speed;

    // An agent has start, goal positions, and speed:
    public Agents(float startX, float startY, float goalX, float goalY, float speed){
        this.posX = startX;
        this.posY = startY;
        this.goalX = goalX;
        this.goalY = goalY;
        this.speed = speed;
    }

    // Random start position: 1 - 10 at X, 10 - 310 at Y.
    // Random goal position: ~600 at X, 10 - 310 at Y.
    public Agents(){
        this.posX = ThreadLocalRandom.current().nextFloat() * 100.0f;
        this.posY = ThreadLocalRandom.current().nextFloat() * 300.0f + 10.0f;
        this.goalX = ThreadLocalRandom.current().nextFloat() + 600.0f;
        this.goalY = ThreadLocalRandom.current().nextFloat() * 300.0f + 10.0f;
        this.speed = ThreadLocalRandom.current().nextFloat() * 10.0f;
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

    public void moveToGoal(){
        if(this.posX < this.goalX){
            this.posX += this.speed;
        }
        if(this.posY < this.goalY){
            this.posY += this.speed;
        }
        if(this.posY > this.goalY){
            this.posY -= this.speed;
        }
    }

    public void distanceDifference(Agents agent){
        float x = agent.getPosX();
        float y = agent.getPosY();
        float differenceX = Math.abs(x - this.posX);
        float differenceY = Math.abs(y - this.posY);

        if(differenceX < 20 && differenceY < 20){
            this.speed += 2;
            agent.speed -= 1;
        }
    }

    /*public void moveForward(double x){
        setPosX(getPosX() + x);
    }

    public void moveRight(double y){
        setPosY(getPosY() + y);
    }*/
}
