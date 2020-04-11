package View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class QuestionnaireActivity extends AbstractActivity {

    Questionnaire questionnaire;
    long currentQuestionID;
    Map<Long,List<Long>> questionsAnswers; //key: questionID, value: list of answerID
    Map<Long,Map<Long,Button>> answersButtons; //key:: answerID, value: answer Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        questionsAnswers = new HashMap<>();
        answersButtons = new  HashMap<>();
        Intent intent = getIntent();
        questionnaire = (Questionnaire) intent.getSerializableExtra(BindingValues.REQUESTED_QUESTIONNAIRE);
        System.out.println("xxx");
        showTitle();
        showQuestion(0);

    }

    private void showTitle() {
        TextView title = findViewById(R.id.questionnaire_title);
        title.setText(this.getString(R.string.questionnaire) + " " + questionnaire.getTitle());
        title.setTextSize(20);
    }

    private void showQuestion(final long ii) {
        final int i = safeLongToInt(ii);
        currentQuestionID = questionnaire.getQuestions().get(i).getQuestionID();
        answersButtons.put(currentQuestionID,new HashMap<Long, Button>());
        final LinearLayout layout = findViewById(R.id.lin_layout);
        String ques_TEXT = questionnaire.getQuestions().get(i).getQuestionText();
        TextView question_TV = new TextView(this);
        question_TV.setText(ques_TEXT);
        question_TV.setTextSize(30);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        question_TV.setGravity(Gravity.CENTER);
        question_TV.setLayoutParams(params);
        layout.addView(question_TV);
        BuildQuestionByType(questionnaire.getQuestions().get(i).getType(), i);

        //next previous buttons
        FloatingActionButton nextButton = findViewById(R.id.nextButton);
        //setLocationOfButtonInRelativeLayout(nextButton,"next");

        FloatingActionButton prevButton = findViewById(R.id.prevButton);
        //setLocationOfButtonInRelativeLayout(prevButton,"previous");

        if (i<questionnaire.getQuestions().size()-1){ // not last question


            setNextButtonActionForAllQuestionsExceptLast(ii, layout, nextButton);

            if (i>0) { //not first question
                setPreviousButtonActionForAllQuestionsExceptFirst(layout,prevButton);
            }
            else { //first question
                setInvisible(prevButton);
            }
        }
        else { //last question in questionnaire
            //nextButton.setImageResource(android.R.drawable.ic_menu_send);
            setNextButtonActionForLastQuestion(ii, layout, nextButton);
            setPreviousButtonActionForAllQuestionsExceptFirst(layout,prevButton);

        }


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
                if (questionsAnswers.get(ii) == null || questionsAnswers.get(ii).isEmpty()) {
                    Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                } else {
                    sendAnswersToServer();
                    layout.removeAllViews();
                    FloatingActionButton nextButton = findViewById(R.id.nextButton);
                    animateFullCircle(nextButton);
                    nextButton.setVisibility(View.INVISIBLE);
                    FloatingActionButton prevButton = findViewById(R.id.prevButton);
                    prevButton.setVisibility(View.INVISIBLE);
                    TextView thanksTV = new TextView(v.getContext());
                    thanksTV.setText(R.string.thanks);
                    thanksTV.setTextSize(30);
                    layout.addView(thanksTV);
                    TextView sentTV = new TextView(v.getContext());
                    sentTV.setText(R.string.sent_succesfully);
                    sentTV.setTextSize(30);
                    layout.addView(sentTV);
                    Button backButton = new Button(v.getContext());
                    backButton.setText(R.string.back_to_home_page);
                    backButton.setWidth(20);
                    backButton.setHeight(10);
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            backToHomePage(v);
                        }
                    });
                    layout.addView(backButton);



                }
            }
        });
    }

    private void backToHomePage(View v) {
        Intent intent = new Intent(this,HomePageActivity.class);
        startActivity(intent);
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
                    Toast.makeText(v.getContext(),R.string.answerTheQuestion,Toast.LENGTH_SHORT).show();
                }
                else {
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
                Toast.makeText(v.getContext(),R.string.movingToPrevQuestion,Toast.LENGTH_SHORT).show();
                animateFullCircle(prevButton);
                layout.removeAllViews();
                showQuestion(--currentQuestionID);

            }
        });
    }


    private void setLocationOfButtonInRelativeLayout(FloatingActionButton button, String nextOrPrev) {
        RelativeLayout.LayoutParams params = null;
        switch (nextOrPrev) {
            case  "next":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, Double.valueOf(getHeightOfScreen()*0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
            case "previous":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(Double.valueOf(getWidthOfScreen()*0.85).intValue(), Double.valueOf(getHeightOfScreen()*0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
        }
    }

    private void sendAnswersToServer() {
        AppController appController = AppController.getController(this);
        appController.sendAnswersToServer(questionsAnswers,questionnaire.getQuestionaireID());
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
        else
            System.out.println("please check type of Question");
    }

    private void buildVAS_Question(final int i) {
        LinearLayout  layout =  findViewById(R.id.lin_layout);
        final Map<Long, Integer> VAS_Colors = getColorsOfVAS();



        TextView best = new TextView(this);
        best.setText(questionnaire.getQuestions().get(i).getBest());
        setLabelsOfBestWorstConfiguration(best);
        layout.addView(best);


        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0,0,0,0);
            ans_Button.setTextSize(20);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            ans_Button.setBackgroundColor(VAS_Colors.get(finalAnswerID));
            setButtonConfiguration(ans_Button);
            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID);
                }

                private void chose(long chosenAnswerID, long questionID) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);
                    int color = ResourcesCompat.getColor(getResources(),R.color.colorChosenAnswer, null);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID,tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackgroundColor(color);
                    }
                    else { //user has changed his answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID,prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackgroundColor(VAS_Colors.get(prevAnswer));
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackgroundColor(color);
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID,ans_Button);
            layout.addView(ans_Button);
        }

        TextView worstTV= new TextView(this);
        worstTV.setText(questionnaire.getQuestions().get(i).getWorst());
        setLabelsOfBestWorstConfiguration(worstTV);
        layout.addView(worstTV);




    }

    /**
     *
     * @return map of answerID and color of button as integer
     */
    private Map<Long, Integer> getColorsOfVAS() {
        HashMap<Long,Integer> colors = new HashMap<>();
        long i=0;
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS0, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS1, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS2, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS3, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS4, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS5, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS6, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS7, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS8, null));
        colors.put(i++,ResourcesCompat.getColor(getResources(),R.color.colorVAS9, null));
        colors.put(i,ResourcesCompat.getColor(getResources(),R.color.colorVAS10, null));
        return colors;
    }

    private void buildSingleQuestion(int i) {
        LinearLayout  layout =  findViewById(R.id.lin_layout);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0,0,0,0);
            ans_Button.setTextSize(20);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            final int reg_color = ResourcesCompat.getColor(getResources(),R.color.colorRegularAnswer, null);
            ans_Button.setBackgroundColor(reg_color);
            setButtonConfigurationForSingleAndMulti(ans_Button);

            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID);
                }

                private void chose(long chosenAnswerID, long questionID) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);
                    int color = ResourcesCompat.getColor(getResources(),R.color.colorChosenAnswer, null);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID,tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackgroundColor(color);
                    }
                    else { //user has changed is answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID,prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackgroundColor(reg_color);
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackgroundColor(color);
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID,ans_Button);
            layout.addView(ans_Button);
        }
    }

    private void buildMultiQuestion(final int i) {
        LinearLayout  layout =  findViewById(R.id.lin_layout);

        TextView multipleTV= new TextView(this);
        multipleTV.setText(R.string.multiple_choices);
        setLabelsOfBestWorstConfiguration(multipleTV);
        layout.addView(multipleTV);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0,0,0,0);
            ans_Button.setTextSize(20);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            final int reg_color = ResourcesCompat.getColor(getResources(),R.color.colorRegularAnswer, null);
            ans_Button.setBackgroundColor(reg_color);
            setButtonConfigurationForSingleAndMulti(ans_Button);

            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID, v);
                }

                private void chose(long chosenAnswerID, long questionID, View v) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);
                    int color = ResourcesCompat.getColor(getResources(),R.color.colorChosenAnswer, null);
                    List<Long> alone =  questionnaire.getQuestions().get(i).getAlone();
                    if (!questionsAnswers.containsKey(questionID)) { //first answer
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID,tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackgroundColor(color);
                    }
                    else { //user has changed his answer or add new answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        int index_cancelledAnswer = sameAnswer(prevAnsList,chosenAnswerID);
                        if (index_cancelledAnswer == -1) { //new answer
                            if (AloneIsAlreadyChosen(alone,prevAnsList)) {
                                Toast.makeText(v.getContext(),R.string.choose_else,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if (alone.contains(chosenAnswerID)) { //this answer should be chosen only alone
                                //remove all the rest
                                for (long prev : prevAnsList) {
                                    answersButtons.get(finalQuestionID).get(prev).setBackgroundColor(reg_color);
                                }
                                prevAnsList.clear();
                                Toast.makeText(v.getContext(),R.string.this_alone,Toast.LENGTH_SHORT).show();

                            }
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID,prevAnsList);
                            answersButtons.get(finalQuestionID).get(chosenAnswerID).setBackgroundColor(color);
                        }
                        else { //want to cancel exists answer
                            long prevAnswer = prevAnsList.get(index_cancelledAnswer);
                            prevAnsList.remove((index_cancelledAnswer));
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackgroundColor(reg_color);
                            if (prevAnsList.size() != 0) //there is still choices
                                questionsAnswers.put(questionID,prevAnsList);
                            else //empty list
                                questionsAnswers.remove(questionID);
                        }

                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID,ans_Button);
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
        for (int i=0; i<prevAnsList.size(); i++) {
            long l = prevAnsList.get(i);
            if (chosenAnswerID == l)
                return i;
        }
        return -1;
    }

    private void setButtonConfiguration(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        int px = getPxOfDP(125);

        params.width = px;
        params.height = (int) (px*0.25);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
    }

    /*
    now is implemented only by single, let's see later if we need it on multi
     */
    private void setButtonConfigurationForSingleAndMulti(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        int width = getWidthOfScreen();
        b.setWidth(width);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
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
        t.setPadding(0,0,0,0);
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

}
