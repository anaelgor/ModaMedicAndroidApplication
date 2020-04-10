package Controller;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Map;

import Model.ConnectedDevices;
import Model.Exceptions.InvalidTokenException;
import Model.Exceptions.WrongAnswerException;
import Model.Metrics.SensorData;
import Model.Notifications.NotificationsManager;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Users.Login;
import Model.Utils.HttpRequests;

public class AppController {

    private static AppController appController;
    private Activity activity;
    private HttpRequests httpRequests;
    private SensorData sensorData;

    private static final String TAG = "AppController";


    private AppController(Activity activity) {
        this.activity = activity;
        this.httpRequests = HttpRequests.getInstance(activity.getApplicationContext());
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
        sensorData.sendData(activity.getApplicationContext());
    }

    public Questionnaire getQuestionnaire(Long questionnaire_id) {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaireById(questionnaire_id, httpRequests);
    }

    public void sendAnswersToServer(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        QuestionnaireSenderAndReceiver.sendAnswers(questionsAndAnswers,questionnaireID, httpRequests);
    }

    public Map<Long, String> getUserQuestionnaires() {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaires(httpRequests);
    }

    public boolean login(String username, String password, Activity activity) {
        return Login.login(username,password,activity, httpRequests);
    }

    public void checkIfBandIsConnected(){
        ConnectedDevices.checkIfBTIsOn(activity);
    }

    public void setNotifications(Context context) {
        NotificationsManager notificationsManager = new NotificationsManager(context);
        notificationsManager.setNotifications();
    }

    public void setMetricsTask(Context context) {
        sensorData.setMetricsTask(context);
    }

    public void setLocationTrackerTask(Context context) {
        sensorData.setLocationTrackerTask(context);
    }

    public String getVerificationQuestion(String username) {
        return Login.getVerificationQuestion(username, httpRequests);
    }

    public boolean checkVerificationOfAnswerToUserQuestion(String username,String answer, long date) throws WrongAnswerException {
        return Login.checkVerificationOfAnswerToUserQuestion(username,date,answer,httpRequests);
    }

    public boolean setNewPasswordForLoggedOutUser(String newPassword) throws InvalidTokenException {
        return Login.setNewPasswordForLoggedOutUser(newPassword, httpRequests);
    }

    public boolean askForChangePassword() {
        return Login.askForChangePassword(httpRequests);
    }
}
