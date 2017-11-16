package ClientModel;

import java.util.concurrent.ThreadLocalRandom;

public class Agents {

    private float posX, posY, goalX, goalY;

    // An agent has start and goal positions:
    public Agents(float startX, float startY, float goalX, float goalY){
        this.posX = startX;
        this.posY = startY;
        this.goalX = goalX;
        this.goalY = goalY;
    }

    // Random start position: 1 - 10 at X, 10 - 310 at Y.
    // Random goal position: ~600 at X, 10 - 310 at Y.
    public Agents(){
        this.posX = ThreadLocalRandom.current().nextFloat() * 100.0f;
        this.posY = ThreadLocalRandom.current().nextFloat() * 300.0f + 10.0f;
        this.goalX = ThreadLocalRandom.current().nextFloat() + 600.0f;
        this.goalY = ThreadLocalRandom.current().nextFloat() * 300.0f + 10.0f;
        System.out.println("(x, y, goalX, goalY): (" + this.posX + ' ' + this.posY + ' ' + this.goalX + ' ' + this.goalY + ')');
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

    public void isGoal(){
        if(this.posX >= this.goalX-100){
            System.out.println("Almost at goal");
        }
    }

    /*public void moveForward(double x){
        setPosX(getPosX() + x);
    }

    public void moveRight(double y){
        setPosY(getPosY() + y);
    }*/
}
