package Server;

public interface Movement {
    void moveForward(float x);
    /*void moveBackward(double x);
    void moveUpward(double y);
    void moveDownward(double y);*/
    float currentPositionX(int i);
    float currentPositionY();
    void amIAtGoal();
}
