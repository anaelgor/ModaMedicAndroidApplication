package Model.Questionnaires;

class Answer {

    private String answerID;
    private String answerText;
    private int answerValue;

    public Answer(String answerID, String answerText) {
        this.answerID = answerID;
        this.answerText = answerText;
        this.answerValue = Integer.parseInt(answerID);
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
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
