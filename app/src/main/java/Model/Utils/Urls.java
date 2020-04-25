package Model.Utils;

public final class Urls {
    public static final String urlPostSteps = "auth/patients/metrics/steps";
    public static final String urlPostDistance = "auth/patients/metrics/distance";
    public static final String urlPostCalories = "auth/patients/metrics/calories";
    public static final String urlPostSleep = "auth/patients/metrics/sleep";
    public static final String urlPostActivity = "auth/patients/metrics/activity";
    public static final String urlPostWeather = "auth/patients/metrics/weather";
    public static final String urlGetMissingDates = "auth/patients/metrics/getMissingDates?"; //param: days
    public static final String getUrlGetMissingDatesDaysParam = "days=";
    public static final String urlHasBeenAnswered = "auth/patients/answers/answeredQuestionnaire?"; // params: days, questionnaireID
    public static final String getUrlHasBeenAnsweredDaysParam = "days=";
    public static final String getUrlHasBeenAnsweredQuestionnaireIDParam = "&questionnaireID=";
    public static final String urlGetUserQuestionnaires = "auth/usersAll/getUserQuestionnaire";
    public static final String urlGetQuestionnaireByID = "questionnaires/getQuestionnaire/"; //add id as param
    public static final String urlPostAnswersOfQuestionnaireByID = "auth/patients/answers/sendAnswers";
    public static final String urlOfLogin = "users/login";
    public static final String urlOfForgotPassword = "users/forgotPassword";
    public static final String urlOfGetVerificationQuestion = "users/getVerificationQuestion?QuestionID="; //id of question is param
    public static final String urlOfCheckVerificationAnswer = "users/checkVerification";
    public static final String urlOfSetNewPassword = "users/passwordChangeCheck/changePassword";
    public static final String urlOfGetSpecialToken = "auth/usersAll/askChangePassword";
    public static final String urlOfGetAllVerificationQuestions = "users/getVerifications";
    public static final String urlOfGetAllQuestionnaires = "questionnaires/all";
    public static final String urlOfRegister = "users/patientRegister";
    public static final String urlOfGetSurgeryDate = "auth/usersAll/getDateOfSurgery";
    public static final String urlOfSetSurgeryDate = "auth/usersAll/ChangeDateOfSurgery";
    public static final String urlOfSetNewQuestionnaires = "auth/usersAll/changeUserQuestionnaire";
}