package Controller;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Map;

import Model.HttpRequests;
import Model.Login;
import Model.QuestionnaireSenderAndReceiver;
import Model.Questionnaires.Questionnaire;
import Model.SensorData;

public class AppController {

    private static AppController appController;
    private Activity activity;
    private HttpRequests httpRequests;
    private SensorData sensorData;

    private static final String TAG = "AppController";


    private AppController(Activity activity) {
        this.activity = activity;
        this.httpRequests = HttpRequests.getInstance();
        this.sensorData = new SensorData(activity);
    }

    public static AppController getController(Activity activity){
        if (appController == null){
            appController = new AppController(activity);
        }
        return appController;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SendSensorData(){
        sensorData.collectData(this.activity);
        sensorData.sendData();
    }

    public Questionnaire getQuestionnaire(Long questionnaire_id) {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaireById(questionnaire_id, httpRequests);
    }

    public void sendAnswersToServer(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        QuestionnaireSenderAndReceiver.sendAnswers(questionsAndAnswers,questionnaireID, httpRequests);
    }

    public Map<Long, String> getUserQuestionnaires(String username) {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaires(username, httpRequests);
    }

    public boolean login(String username, String password, Activity activity) {
        return Login.login(username,password,activity, httpRequests);
    }
}
