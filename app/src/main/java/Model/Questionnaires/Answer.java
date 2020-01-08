package Model.Questionnaires;

import java.io.Serializable;

public class Answer implements Serializable {

    private long answerID;
    private String answerText;
    private int answerValue;

    public Answer(long answerID, String answerText) {
        this.answerID = answerID;
        this.answerText = answerText;
        this.answerValue = (int) answerID;
    }


    public long getAnswerID() {
        return answerID;
    }

    public void setAnswerID(long answerID) {
        this.answerID = answerID;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(int answerValue) {
        this.answerValue = answerValue;
    }
}
