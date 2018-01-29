package Controller;

/**
 * Purpose: This class serves as a representation of a program that can be
 * run independently from the the server program.
 *
 * @Author Amin Yassin
 * @Version 2.0 29/01/2018
 */
public class ClientProgram {

    /**
     * A main method that starts independently from the server's program.
     * <br>
     * An instance of an agent handler starts with the start of this program.
     *
     * @param args A default argument for the main function.
     *
     */
    public static void main(String[] args){

        System.out.println("An AgentHandler program has started!");
        new AgentHandler().start();
    }
}
