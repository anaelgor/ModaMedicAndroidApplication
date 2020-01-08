package Model.Exceptions;

public class ServerFalse extends Exception {
    private String message;
    public ServerFalse() {
        super();
    }

    /**
     * create new exeption type
     * @param message the message
     */
    public ServerFalse(String message)
    {
        super(message);
        this.message = message;
    }
}