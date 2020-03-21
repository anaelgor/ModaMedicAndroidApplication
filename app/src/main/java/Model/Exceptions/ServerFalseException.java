package Model.Exceptions;

public class ServerFalseException extends Exception {
    private String message;
    public ServerFalseException() {
        super();
    }

    /**
     * create new exeption type
     * @param message the message
     */
    public ServerFalseException(String message)
    {
        super(message);
        this.message = message;
    }
}