package Model.Questionnaires;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Questionnaire implements Serializable {

    private String mongoID;
    private long QuestionaireID;
    private String title;
    private List<Question> questions;

    public Questionnaire(String mongoID, long questionaireID, String title, List<Question> questions) {
        this.mongoID = mongoID;
        QuestionaireID = questionaireID;
        this.title = title;
        this.questions = questions;
    }

    public Questionnaire() {}

    public String getMongoID() {
        return mongoID;
    }

    public void setMongoID(String mongoID) {
        this.mongoID = mongoID;
    }

    public long getQuestionaireID() {
        return QuestionaireID;
    }

    public void setQuestionaireID(long questionaireID) {
        QuestionaireID = questionaireID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Questionnaire that = (Questionnaire) o;
        return QuestionaireID == that.QuestionaireID &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(QuestionaireID, title);
    }
}
