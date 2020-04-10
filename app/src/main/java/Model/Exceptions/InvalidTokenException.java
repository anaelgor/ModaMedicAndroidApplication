package Model.Exceptions;

public class InvalidTokenException extends Exception {

    private String message;

    public InvalidTokenException()
    {
        super();
        this.message = "Token is valid only for 5 minutes";
    }
}
