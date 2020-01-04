package Model.Questionnaires;

import java.util.List;

class Question {

    enum Type {
        VAS,
        SINGLE,
        MULTI
    }

    private Type type;
    private String questionID;
    private String questionText;
    private List<Answer> answers;

    public Question(String type, String questionID, String questionText, List<Answer> answers) {
        this.type = getCorrectType(type);
        this.questionID = questionID;
        this.questionText = questionText;
        this.answers = answers;
    }

    private Type getCorrectType(String type) {
        switch(type.toUpperCase()) {
            case ("VAS"):
                return Type.VAS;
            case("MULTI"):
                return Type.MULTI;
            case("SINGLE"):
                return Type.SINGLE;
        }
        return null;
    }

    public Type getType() {
        return type;
    }

    public void setType(String type) {
        this.type = getCorrectType(type);
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
