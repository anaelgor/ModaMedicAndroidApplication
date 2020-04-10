package Model.Exceptions;

public class WrongAnswerException extends Exception {
    private String message;

    public WrongAnswerException()
    {
        super();
        this.message = "Wrong Answer";
    }
}
