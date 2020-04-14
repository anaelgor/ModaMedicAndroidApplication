package Model.Questionnaires;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    public enum Type {
        VAS,
        SINGLE,
        MULTI,
        EQ5
    }

    private Type type;
    private long questionID;
    private String questionText;
    private List<Answer> answers;
    private String best;
    private String worst;
    private List<Long> alone;

    public Question(String type, long questionID, String questionText, List<Answer> answers) {
        this.type = getCorrectType(type);
        this.questionID = questionID;
        this.questionText = questionText;
        this.answers = answers;
        this.best = null;
        this.worst = null;
        this.alone = null;
    }

    public Question(){}

    public List<Long> getAlone() {
        return alone;
    }

    public void setAlone(List<Long> alone) {
        this.alone = alone;
    }

    private Type getCorrectType(String type) {
        switch(type.toUpperCase()) {
            case ("VAS"):
                return Type.VAS;
            case("MULTI"):
                return Type.MULTI;
            case("SINGLE"):
                return Type.SINGLE;
            case ("EQ5"):
                return Type.EQ5;
        }
        return null;
    }

    public Type getType() {
        return type;
    }


    public String getBest() {
        return best;
    }

    public void setBest(String best) {
        this.best = best;
    }

    public String getWorst() {
        return worst;
    }

    public void setWorst(String worst) {
        this.worst = worst;
    }

    public void setType(String type) {
        this.type = getCorrectType(type);
    }

    public long getQuestionID() {
        return questionID;
    }

    public void setQuestionID(long questionID) {
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
