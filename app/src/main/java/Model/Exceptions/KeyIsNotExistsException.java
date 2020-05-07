package Model.Exceptions;



public class KeyIsNotExistsException extends Exception {
    private String message;
    public KeyIsNotExistsException() {
        super();
    }

    /**
     * create new exeption type
     * @param message the message
     */
    public KeyIsNotExistsException(String message)
    {
        super(message);
        this.message = message;
    }
}