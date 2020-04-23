package Model.Users;

import java.util.List;

import Model.Questionnaires.Questionnaire;

public class User {

    private String email;
    private String password;
    private String phoneNumber;
    private String gender;
    private String smoke;
    private String surgeryType;
    private String education;
    private int weight;
    private int height;
    private String bmi;
    private long birthday;
    private String code;
    private int verificationQuestion;
    private String verificationAnswer;
    private long surgeryDate;
    private List<Questionnaire> questionnaires;
    private String firstName;
    private String lastName;

    public User(String email, String password, String phoneNumber, String gender, String smoke,
                String surgeryType, String education, int weight, int height, long birthday,
                String code, int verificationQuestion, String verificationAnswer, long surgeryDate,
                List<Questionnaire> questionnairesIDS, String first, String last){
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.smoke = smoke;
        this.surgeryType = surgeryType;
        this.education = education;
        this.weight = weight;
        this.height = height;
        this.birthday = birthday;
        this.bmi = calculateBMI(height,weight);
        this.code = code;
        this.verificationAnswer = verificationAnswer;
        this.verificationQuestion = verificationQuestion;
        this.questionnaires = questionnairesIDS;
        this.surgeryDate = surgeryDate;
        this.firstName = first;
        this.lastName = last;
    }

    private String calculateBMI(int height, int weight) {
        double height_double = ((double)height / 100);
        return String.valueOf(((double)weight/Math.pow(height_double,2)));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getSurgeryType() {
        return surgeryType;
    }

    public void setSurgeryType(String surgeryType) {
        this.surgeryType = surgeryType;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBmi() {
        return bmi;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getVerificationQuestion() {
        return verificationQuestion;
    }

    public void setVerificationQuestion(int verificationQuestion) {
        this.verificationQuestion = verificationQuestion;
    }

    public String getVerificationAnswer() {
        return verificationAnswer;
    }

    public void setVerificationAnswer(String verificationAnswer) {
        this.verificationAnswer = verificationAnswer;
    }

    public long getSurgeryDate() {
        return surgeryDate;
    }

    public void setSurgeryDate(long surgeryDate) {
        this.surgeryDate = surgeryDate;
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


}
