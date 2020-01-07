package Model.Questionnaires;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Paths.get;


public class QuestionnaireManager {

    public static Questionnaire createQuestionnaireFromJSON(JSONObject jsonObject) {
        String title = (String) jsonObject.get("QuestionnaireText");
        String mongo_id = (String) jsonObject.get("_id");
        long questionnaire_id = (Long) jsonObject.get("QuestionnaireID");
        JSONArray questionsJSON = (JSONArray) jsonObject.get("Questions");
        List<Question> questionsList =  parseQuestionsFromJSONArray(questionsJSON);
        Questionnaire result = new Questionnaire();
        result.setMongoID(mongo_id);
        result.setQuestionaireID(questionnaire_id);
        result.setTitle(title);
        result.setQuestions(questionsList);
        return result;
    }




    public static JSONObject jsonObject(Questionnaire questionnaire) {
        //todo: will be implemented only if we need to add questionnaires.

        return null;
    }


    private static List<Question> parseQuestionsFromJSONArray(JSONArray questionsJSON) {
        List<Question> questions = new ArrayList<>();
        for (Object o: questionsJSON) {
            if (o instanceof JSONObject) {
                Question question = parseQuestionFromJsonObject((JSONObject)o);
                questions.add(question);
            }
        }
        return questions;
    }

    private static Question parseQuestionFromJsonObject(JSONObject jsonObject) {
        long Question_id = (Long) jsonObject.get("QuestionID");
        String type =  (String) jsonObject.get("Type");
        String text = (String) jsonObject.get("QuestionText");
        JSONArray answersJSON = (JSONArray) jsonObject.get("Answers");
        List<Answer> answersList =  parseAnswersFromJSONArray(answersJSON);
        Question question = new Question();
        question.setQuestionID(Question_id);
        question.setQuestionText(text);
        question.setType(type);
        question.setAnswers(answersList);
        //for specific questions type
        if (type.toUpperCase().equals("VAS")) {
            String best = (String) jsonObject.get("Best");
            String worst = (String) jsonObject.get("Worst");
            question.setBest(best);
            question.setWorst(worst);
        }

        return question;
    }

    private static List<Answer> parseAnswersFromJSONArray(JSONArray answersJSON) {
        List<Answer> answers = new ArrayList<>();
        for (Object o: answersJSON) {
            if (o instanceof JSONObject) {
                Answer answer = parseAnswerFromJsonObject((JSONObject)o);
                answers.add(answer);
            }
        }
        return answers;
    }

    private static Answer parseAnswerFromJsonObject(JSONObject jsonObject) {
        long ansID = (Long) jsonObject.get("answerID");
        String text = (String) jsonObject.get("answerText");
        return new Answer(ansID,text);
    }

}
