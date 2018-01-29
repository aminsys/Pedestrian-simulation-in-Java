package ClientModel;

import java.io.Serializable;

/**
 * Purpose: This class serves as a structure that contains initial information
 * to be sent to the agent handlers from the Server class.
 *
 * @Author Amin Yassin
 * @Version 2.0 29/01/2018
 */
public class DataPackage implements Serializable {
    private int amount, amountOfOtherAgents;
    private String ID;

    /**
     * A constructor of the class. Once it's constructed, it will contain the
     * colour ID of a set of agetns, their amount, and the amount of the
     * opposite agents.
     *
     * @param ID A colour ID in the form of a string.
     * @param amount Amount of the agents that is desired to be created.
     * @param other The amount of the opposite agents that will be created.
     */
    public DataPackage(String ID, int amount, int other){
        this.ID = ID;
        this.amount = amount;
        this.amountOfOtherAgents = other;
    }

    /**
     * A method to return the number of agents.
     *
     * @return amount of agents.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * A method to return the number of agents of opposite colour.
     *
     * @return amount of opposite agents.
     */
    public int getAmountOfOtherAgents() { return amountOfOtherAgents; }

    /**
     * A method to return the colour ID of a set of agents.
     *
     * @return a string that represents the colour ID of a set of agents.
     */
    public String getID() {
        return ID;
    }
}
