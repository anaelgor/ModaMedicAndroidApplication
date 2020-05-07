package View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.modamedicandroidapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Controller.AppController;
import Model.Questionnaires.Answer;
import Model.Questionnaires.Question;
import Model.Questionnaires.Questionnaire;
import Model.Utils.Constants;
import View.ViewUtils.BindingValues;

public class QuestionnaireActivity extends AbstractActivity {

    private static final String TAG = "QuestionnaireActivity";
    Questionnaire questionnaire;
    long currentQuestionID;
    Map<Long, List<Long>> questionsAnswers; //key: questionID, value: list of answerID
    Map<Long, Map<Long, Button>> answersButtons; //key:: answerID, value: answer Button
    private SeekBar answerEQ5TF = null;
    private boolean eq5Answered = false;
    private TextView eq5result = null;
    private ImageView eq5face = null;
    private Map<Long, String> medicineInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        questionsAnswers = new HashMap<>();
        answersButtons = new HashMap<>();
        Intent intent = getIntent();
        questionnaire = (Questionnaire) intent.getSerializableExtra(BindingValues.REQUESTED_QUESTIONNAIRE);
        showTitle();
        showQuestion(0);

    }

    private void showTitle() {
        TextView title = findViewById(R.id.questionnaire_title);
        title.setText(this.getString(R.string.questionnaire) + " " + questionnaire.getTitle());
        title.setTextSize(20);
        title.setBackground(getDrawable(R.drawable.custom_chosen_button));

    }

    private void showQuestion(final long ii) {
        final int i = safeLongToInt(ii);
        currentQuestionID = questionnaire.getQuestions().get(i).getQuestionID();
        answersButtons.put(currentQuestionID, new HashMap<Long, Button>());
        final LinearLayout layout = findViewById(R.id.lin_layout);
        String ques_TEXT = questionnaire.getQuestions().get(i).getQuestionText();
        TextView question_TV = new TextView(this);
        question_TV.setText(ques_TEXT);
        question_TV.setTextSize(20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        question_TV.setGravity(Gravity.CENTER);
        question_TV.setLayoutParams(params);
        layout.addView(question_TV);
        Question.Type type = questionnaire.getQuestions().get(i).getType();
        if (type.equals(Question.Type.SINGLE)) {
            TextView twoWeeksTV = BuildTwoWeeks();
            layout.addView(twoWeeksTV);
        }
        BuildQuestionByType(type, i);

        //next previous buttons
        FloatingActionButton nextButton = findViewById(R.id.nextButton);
        //setLocationOfButtonInRelativeLayout(nextButton,"next");

        FloatingActionButton prevButton = findViewById(R.id.prevButton);
        //setLocationOfButtonInRelativeLayout(prevButton,"previous");

        if (i < questionnaire.getQuestions().size() - 1) { // not last question


            setNextButtonActionForAllQuestionsExceptLast(ii, layout, nextButton);

            if (i > 0) { //not first question
                setPreviousButtonActionForAllQuestionsExceptFirst(layout, prevButton);
            } else { //first question
                setInvisible(prevButton);
            }
        } else { //last question in questionnaire
            //nextButton.setImageResource(android.R.drawable.ic_menu_send);
            setNextButtonActionForLastQuestion(ii, layout, nextButton);
            if (i == 0) //first question
                setInvisible(prevButton);
            else
                setPreviousButtonActionForAllQuestionsExceptFirst(layout, prevButton);

        }


    }

    private TextView BuildTwoWeeks() {
        String two_weeks = getString(R.string.consider_last_time);
        TextView twoWeeksTV = new TextView(this);
        twoWeeksTV.setText(two_weeks);
        twoWeeksTV.setTextSize(15);
        LinearLayout.LayoutParams two_weeks_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        two_weeks_params.setMargins(10, 10, 10, 10);
        twoWeeksTV.setGravity(Gravity.CENTER);
        twoWeeksTV.setLayoutParams(two_weeks_params);
        return twoWeeksTV;
    }

    @SuppressLint("RestrictedApi")
    private void setInvisible(FloatingActionButton prevButton) {
        prevButton.setVisibility(View.INVISIBLE);
    }

    private void setNextButtonActionForLastQuestion(long ii, LinearLayout layout, FloatingActionButton nextButton) {
        nextButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                boolean isEq5 = questionnaire.getQuestionaireID() == 6 && answerEQ5TF != null;
                if (isEq5) {
                    //special section for EQ5 special question
                    String answerNumber = String.valueOf(answerEQ5TF.getProgress());
                    if (!eq5Answered) {
                        Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        eq5Answered = false;
                        List<Long> answer = new ArrayList<>();
                        answer.add(Long.parseLong(answerNumber));
                        questionsAnswers.put(Long.parseLong("0"), answer);
                    }
                }

                if (!isEq5 && (questionsAnswers.get(ii) == null || questionsAnswers.get(ii).isEmpty())) {
                    Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                } else {
                    sendAnswersToServer();
                    layout.removeAllViews();
                    if (questionnaire.getQuestionaireID() == 5) {
                        long six = 6;
                        openQuestionnaireActivity("EQ-5 Special Question", six);
                    } else {
                        FloatingActionButton nextButton = findViewById(R.id.nextButton);
                        animateFullCircle(nextButton);
                        nextButton.setVisibility(View.INVISIBLE);
                        FloatingActionButton prevButton = findViewById(R.id.prevButton);
                        prevButton.setVisibility(View.INVISIBLE);
                        TextView thanksTV = new TextView(v.getContext());
                        thanksTV.setText(R.string.thanks);
                        thanksTV.setTextSize(20);
                        thanksTV.setGravity(Gravity.CENTER);
                        layout.addView(thanksTV);
                        TextView sentTV = new TextView(v.getContext());
                        sentTV.setText(R.string.sent_succesfully);
                        sentTV.setTextSize(20);
                        sentTV.setGravity(Gravity.CENTER);
                        layout.addView(sentTV);
                        Button backButton = new Button(v.getContext());
                        backButton.setText(R.string.back_to_home_page);
                        backButton.setWidth(20);
                        backButton.setHeight(10);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,100,0,0);
                        backButton.setLayoutParams(params);
                        backButton.setGravity(Gravity.CENTER);
                        backButton.setBackground(getDrawable(R.drawable.custom_system_button));
                        backButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backToHomePage(v);
                            }
                        });
                        layout.addView(backButton);
                    }
                }

            }
        });
    }

    private void persistLastAnsweredOnDeviceStorage(long questionaireID) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        long currentTime = System.currentTimeMillis();
        sharedPreferences.edit().putLong(Constants.LAST_TIME_ANSWERED_QUESTIONNAIRE+questionaireID,currentTime).apply();
    }

    private void backToHomePage(View v) {
        finish();
    //    Intent intent = new Intent(this, HomePageActivity.class);
        //startActivity(intent);
    }

    private void animateFullCircle(FloatingActionButton button) {
        float deg = button.getRotation() + 360F;
        button.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void setNextButtonActionForAllQuestionsExceptLast(long ii, LinearLayout layout, FloatingActionButton nextButton) {
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (questionsAnswers.get(ii) == null || questionsAnswers.get(ii).isEmpty()) {
                    Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                } else {
                    layout.removeAllViews();
                    showQuestion(++currentQuestionID);
                    animateFullCircle(nextButton);
                }

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setPreviousButtonActionForAllQuestionsExceptFirst(LinearLayout layout, FloatingActionButton prevButton) {
        prevButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.movingToPrevQuestion, Toast.LENGTH_SHORT).show();
                animateFullCircle(prevButton);
                layout.removeAllViews();
                showQuestion(--currentQuestionID);

            }
        });
    }


    private void setLocationOfButtonInRelativeLayout(FloatingActionButton button, String nextOrPrev) {
        RelativeLayout.LayoutParams params = null;
        switch (nextOrPrev) {
            case "next":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, Double.valueOf(getHeightOfScreen() * 0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
            case "previous":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(Double.valueOf(getWidthOfScreen() * 0.85).intValue(), Double.valueOf(getHeightOfScreen() * 0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
        }
    }

    private void sendAnswersToServer() {
        AppController appController = AppController.getController(this);
        appController.sendAnswersToServer(questionsAnswers, questionnaire.getQuestionaireID());
        persistLastAnsweredOnDeviceStorage(questionnaire.getQuestionaireID());

    }

    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    private void BuildQuestionByType(Question.Type type, int i) {
        if (type.equals(Question.Type.MULTI))
            buildMultiQuestion(i);
        else if (type.equals(Question.Type.SINGLE))
            buildSingleQuestion(i);
        else if (type.equals(Question.Type.VAS))
            buildVAS_Question(i);
        else if (type.equals(Question.Type.EQ5))
            buildEQ5Question(i);
        else
            System.out.println("please check type of Question");
    }

    @SuppressLint("SetTextI18n")
    private void buildEQ5Question(int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);
        float sizeBestWorst = 15;
        float subtextSize = 20;
        String worst = this.questionnaire.getQuestions().get(i).getWorst();
        String best = this.questionnaire.getQuestions().get(i).getBest();
        String subtext = getString(R.string.betweenZeroTo100);
        SeekBar seekBar = new SeekBar(this);
        seekBar.setPadding(0, getHeightOfScreen() / 6, 0, 0);
        seekBar.setMax(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(0);
        }
        int width = (int) (0.75 * getWidthOfScreen());
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    func(progress);
            }

            private void func(int progress) {
                eq5Answered = true;
                eq5result.setText(String.valueOf(progress));
                if (progress <= 33)
                    eq5face.setImageResource(R.drawable.yellowsadface);
                else if (progress < 67)
                    eq5face.setImageResource(R.drawable.yellowneutralface);
                else
                    eq5face.setImageResource(R.drawable.yellowhappyface);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });
        TextView bestTV = new TextView(this);
        bestTV.setText(worst + " " + best);
        bestTV.setTextSize(sizeBestWorst);
        bestTV.setGravity(Gravity.CENTER);
        bestTV.setPadding(0, 5, 0, 0);
        TextView subtextTV = new TextView(this);
        subtextTV.setText(subtext);
        subtextTV.setGravity(Gravity.CENTER);
        subtextTV.setTextSize(subtextSize);
        subtextTV.setPadding(0, 5, 0, 0);
        TextView max = new TextView(this);
        max.setGravity(Gravity.END);
        max.setText("100");
        TextView min = new TextView(this);
        min.setGravity(Gravity.START);
        min.setText("0");
        min.setLayoutParams(new LinearLayout.LayoutParams(width,height, (float) 0.5));
        max.setLayoutParams(new LinearLayout.LayoutParams(width,height, (float) 0.5));

        eq5result = new TextView(this);
        eq5result.setGravity(Gravity.CENTER);
        eq5result.setText("0");
        eq5result.setPadding(0, 20, 0, 0);

        eq5face = new ImageView(this);
        eq5face.setImageResource(R.drawable.whitesadface);
        eq5face.setPadding(0, 5, 0, 0);
        eq5face.setLayoutParams(new LinearLayout.LayoutParams(200,200));

        answerEQ5TF = seekBar;
        layout.addView(subtextTV);
        layout.addView(bestTV);
        layout.addView(answerEQ5TF);
        layout.addView(eq5face);
        layout.addView(eq5result);
        LinearLayout rl = new LinearLayout(this);
        rl.setOrientation(LinearLayout.HORIZONTAL);
        rl.addView(min);
        rl.addView(max);
        layout.addView(rl);


    }

    private void buildVAS_Question(final int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);
        final Map<Long, Integer> VAS_Colors = getColorsOfVAS();

        TextView best = new TextView(this);
        best.setText(questionnaire.getQuestions().get(i).getBest());
        setLabelsOfBestWorstConfiguration(best);
        layout.addView(best);

        int answersSize = this.questionnaire.getQuestions().get(i).getAnswers().size();
        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText() + "   ";
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0, 0, 0, 0);
            ans_Button.setTextSize(20);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            ans_Button.setBackground(getDrawable(R.drawable.custom_button));
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.VAS);
            if (chosen)
                ans_Button.setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
            else
                ans_Button.setBackgroundColor(VAS_Colors.get(finalAnswerID));
            Drawable emoji = null;
            if (ans.getAnswerID() < (answersSize/3))
                emoji = getDrawable(R.drawable.happyface);
            else if (ans.getAnswerID() < ((2 * answersSize )/ 3))
                emoji = getDrawable(R.drawable.neutralface);
            else
                emoji = getDrawable(R.drawable.sadface);

            Bitmap emoji_bitmap = ((BitmapDrawable) emoji).getBitmap();
            Drawable emoji_drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(emoji_bitmap, 80, 80, true));
            emoji_drawable.setBounds(0,0,80,80);
            ans_Button.setCompoundDrawables(emoji_drawable,null,null,null);

            setButtonConfigurationForVAS(ans_Button);
            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID);
                }

                private void chose(long chosenAnswerID, long questionID) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
                    } else { //user has changed his answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackgroundColor(VAS_Colors.get(prevAnswer));
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }

        TextView worstTV = new TextView(this);
        worstTV.setText(questionnaire.getQuestions().get(i).getWorst());
        setLabelsOfBestWorstConfiguration(worstTV);
        layout.addView(worstTV);


    }

    /**
     * @return map of answerID and color of button as integer
     */
    private Map<Long, Integer> getColorsOfVAS() {
        HashMap<Long, Integer> colors = new HashMap<>();
        long i = 0;
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS0, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS1, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS2, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS3, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS4, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS5, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS6, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS7, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS8, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS9, null));
        colors.put(i, ResourcesCompat.getColor(getResources(), R.color.colorVAS10, null));
        return colors;
    }

    private void buildSingleQuestion(int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0, 0, 0, 0);
            ans_Button.setTextSize(15);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.SINGLE);
            setButtonConfigurationForSingleAndMulti(ans_Button, chosen);
            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID);
                }

                private void chose(long chosenAnswerID, long questionID) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                    } else { //user has changed is answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackground(getDrawable(R.drawable.custom_button));
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }
    }

    private boolean checkChosen(long finalQuestionID, long finalAnswerID, Question.Type type) {
        if (questionsAnswers.containsKey(finalQuestionID)) {
            List<Long> prevAnsList = questionsAnswers.get(finalQuestionID);
            if (type.equals(Question.Type.SINGLE) || type.equals(Question.Type.VAS)) {
                long prevAnswer = prevAnsList.get(0);
                return (prevAnswer == finalAnswerID);
            }
            else if (type.equals(Question.Type.MULTI)) {
                for (int i=0; prevAnsList!=null && i<prevAnsList.size(); i++) {
                    long prevAnswer = prevAnsList.get(i);
                    if (prevAnswer == finalAnswerID)
                        return true;
                }
            }

        }
        return  false;
    }

    private void buildMultiQuestion(final int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);

        TextView multipleTV = new TextView(this);
        multipleTV.setText(R.string.multiple_choices);
        setLabelsOfBestWorstConfiguration(multipleTV);
        layout.addView(multipleTV);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {

            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            if (this.questionnaire.getQuestionaireID()==0 && i==1 && ans.getAnswerID() != 0) { //medicine question on Daily Questionnaire and not first answer
                if (medicineInfo == null) {
                    medicineInfo = new HashMap<>();
                    medicineInfo.put((long) 1,getString(R.string.basicMedicine));
                    medicineInfo.put((long) 2,getString(R.string.advancedMedicine));
                    medicineInfo.put((long) 3,getString(R.string.narcoticMedicine));
                }
                SpannableString ans_text = new SpannableString(text);
                ans_text.setSpan(new AbsoluteSizeSpan(60),0,text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                String description = medicineInfo.get(ans.getAnswerID());
                SpannableString description_text = new SpannableString(description);
                description_text.setSpan(new AbsoluteSizeSpan(40),0,description.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                CharSequence finalText = TextUtils.concat(ans_text, "\n", description_text);
                ans_Button.setText(finalText);
            }
            else {
                ans_Button.setText(text);
                ans_Button.setTextSize(20);
            }
            ans_Button.setPadding(50, 0, 50, 0);
           // ans_Button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.MULTI);
            setButtonConfigurationForSingleAndMulti(ans_Button, chosen);

            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID, v);
                }

                private void chose(long chosenAnswerID, long questionID, View v) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorChosenAnswer, null);
                    List<Long> alone = questionnaire.getQuestions().get(i).getAlone();
                    if (!questionsAnswers.containsKey(questionID)) { //first answer
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                    } else { //user has changed his answer or add new answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        int index_cancelledAnswer = sameAnswer(prevAnsList, chosenAnswerID);
                        if (index_cancelledAnswer == -1) { //new answer
                            if (AloneIsAlreadyChosen(alone, prevAnsList)) {
                                Toast.makeText(v.getContext(), R.string.choose_else, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (alone.contains(chosenAnswerID)) { //this answer should be chosen only alone
                                //remove all the rest
                                for (long prev : prevAnsList) {
                                    answersButtons.get(finalQuestionID).get(prev).setBackground(getDrawable(R.drawable.custom_button));
                                }
                                prevAnsList.clear();
                                Toast.makeText(v.getContext(), R.string.this_alone, Toast.LENGTH_SHORT).show();

                            }
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(chosenAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                        } else { //want to cancel exists answer
                            long prevAnswer = prevAnsList.get(index_cancelledAnswer);
                            prevAnsList.remove((index_cancelledAnswer));
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackground(getDrawable(R.drawable.custom_button));
                            if (prevAnsList.size() != 0) //there is still choices
                                questionsAnswers.put(questionID, prevAnsList);
                            else //empty list
                                questionsAnswers.remove(questionID);
                        }

                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }

    }



    private boolean AloneIsAlreadyChosen(List<Long> alone, List<Long> prevAnsList) {
        for (long a : alone) {
            if (prevAnsList.contains(a))
                return true;
        }
        return false;
    }

    private int sameAnswer(List<Long> prevAnsList, long chosenAnswerID) {
        for (int i = 0; i < prevAnsList.size(); i++) {
            long l = prevAnsList.get(i);
            if (chosenAnswerID == l)
                return i;
        }
        return -1;
    }

    private void setButtonConfigurationForVAS(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        int px = getPxOfDP(125);

        params.width = px;
        params.height = (int) (px * 0.25);
        b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        b.setLayoutParams(params);
    }

    /*
    now is implemented only by single, let's see later if we need it on multi
     */
    private void setButtonConfigurationForSingleAndMulti(Button b, boolean chosen) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 20, 10, 10);
        int width = getWidthOfScreen();
        b.setWidth(width);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
        if (chosen)
            b.setBackground(getDrawable(R.drawable.custom_chosen_button));
        else
            b.setBackground(getDrawable(R.drawable.custom_button));

    }

    private int getWidthOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void setLabelsOfBestWorstConfiguration(TextView t) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 0, 0, 0);
        t.setTextSize(15);
        t.setLayoutParams(params);
    }

    private int getPxOfDP(int i) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i,
                getResources().getDisplayMetrics()
        );
    }

    private void openQuestionnaireActivity(String questionnaire_name, Long questionnaire_id) {
        Log.i(TAG, "questionnaire " + questionnaire_name + " has been opened");
        AppController appController = AppController.getController(this);
        Questionnaire questionnaire = appController.getQuestionnaire(questionnaire_id);
        Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        finish();
        startActivity(intent);
    }

}
