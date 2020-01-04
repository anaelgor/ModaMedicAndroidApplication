package Model.Questionnaires;

import java.util.List;

public class Questionnaire {

    private String mongoID;
    private String QuestionaireID;
    private String title;
    private List<Question> questions;

    public Questionnaire(String mongoID, String questionaireID, String title, List<Question> questions) {
        this.mongoID = mongoID;
        QuestionaireID = questionaireID;
        this.title = title;
        this.questions = questions;
    }

    public String getMongoID() {
        return mongoID;
    }

    public void setMongoID(String mongoID) {
        this.mongoID = mongoID;
    }

    public String getQuestionaireID() {
        return QuestionaireID;
    }

    public void setQuestionaireID(String questionaireID) {
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
}
