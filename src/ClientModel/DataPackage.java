package ClientModel;

import java.io.Serializable;

/**
 * This class serves as a structure that contains initial information
 *
 */

public class DataPackage implements Serializable {
    private int amount, amountOfOtherAgents;
    private String ID;

    public DataPackage(String ID, int amount, int other){
        this.ID = ID;
        this.amount = amount;
        this.amountOfOtherAgents = other;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmountOfOtherAgents() { return amountOfOtherAgents; }

    public String getID() {
        return ID;
    }
}
