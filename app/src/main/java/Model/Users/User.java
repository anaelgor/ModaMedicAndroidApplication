package Model.Users;

public class User {

    private String email;
    private String password;
    private String phoneNumber;
    private Gender gender;
    private boolean smoke;
    private String surgeryType;
    private String education;
    private int weight;
    private int height;
    private String bmi;
    private long birthday;

    public User(String email, String password, String phoneNumber, Gender gender, boolean smoke, String surgeryType, String education, int weight, int height, long birthday) {
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
    }

    private String calculateBMI(int height, int weight) {
        return String.valueOf(height/Math.pow(weight,2));
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
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


}
