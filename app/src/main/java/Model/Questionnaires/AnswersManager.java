package Model.Questionnaires;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AnswersManager {

    public static JSONObject createJsonAnswersOfQuestsionnaire
            (Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        JSONObject result = new JSONObject();
        try {
            result.put("QuestionnaireID",questionnaireID);
            result.put("ValidDate",new Date().getTime());
            JSONArray totalAnswers = new JSONArray();
            for (Long key : questionsAndAnswers.keySet()) {
                JSONObject question = new JSONObject();
                question.put("QuestionID",key);
                JSONArray answers = new JSONArray();
                for (Long answer : questionsAndAnswers.get(key))
                    answers.put(answer);
                question.put("AnswerID",answers);
                totalAnswers.put(question);
            }
            result.put("Answers",totalAnswers);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println(result.toString());
        return result;
    }
}
