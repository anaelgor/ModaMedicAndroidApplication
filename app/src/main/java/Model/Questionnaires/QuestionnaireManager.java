package Model.Questionnaires;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Paths.get;


public class QuestionnaireManager {

    public static Questionnaire createQuestionnaireFromJSON(JSONObject jsonObject) {
        String title = null;
        try {
            title = (String) jsonObject.get("QuestionnaireText");
            String mongo_id = (String) jsonObject.get("_id");
            long questionnaire_id = new Long((Integer)jsonObject.get("QuestionnaireID"));
            JSONArray questionsJSON = (JSONArray) jsonObject.get("Questions");
            List<Question> questionsList =  parseQuestionsFromJSONArray(questionsJSON);
            Questionnaire result = new Questionnaire();
            result.setMongoID(mongo_id);
            result.setQuestionaireID(questionnaire_id);
            result.setTitle(title);
            result.setQuestions(questionsList);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static List<Question> parseQuestionsFromJSONArray(JSONArray questionsJSON) {
        List<Question> questions = new ArrayList<>();
        for (int i=0; i<questionsJSON.length(); i++) {
            Question question = null;
            try {
                question = parseQuestionFromJsonObject(questionsJSON.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            questions.add(question);
        }
        return questions;
    }

    private static Question parseQuestionFromJsonObject(JSONObject jsonObject) {
        long Question_id = 0;
        Question question = null;
        try {
            Question_id = new Long((Integer)jsonObject.get("QuestionID"));
            String type =  (String) jsonObject.get("Type");
            String text = (String) jsonObject.get("QuestionText");
            JSONArray answersJSON = (JSONArray) jsonObject.get("Answers");
            List<Answer> answersList =  parseAnswersFromJSONArray(answersJSON);
            question = new Question();
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
            else if (type.toUpperCase().equals("MULTI")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("Alone");
                List<Long> alone  = new ArrayList<>();
                for (int i=0; i<jsonArray.length(); i++) {
                    alone.add((new Long((Integer)jsonArray.get(i))));
                }
                question.setAlone(alone);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return question;
    }

    private static List<Answer> parseAnswersFromJSONArray(JSONArray answersJSON) {
        List<Answer> answers = new ArrayList<>();
        for (int i=0; i<answersJSON.length(); i++) {
            Answer answer = null;
            try {
                answer = parseAnswerFromJsonObject((answersJSON.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            answers.add(answer);
        }
        return answers;
    }

    private static Answer parseAnswerFromJsonObject(JSONObject jsonObject) {
        long ansID = 0;
        try {
            ansID = new Long((Integer) jsonObject.get("answerID"));
            String text = (String) jsonObject.get("answerText");
            return new Answer(ansID,text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
