package org.jquizmobile.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.firebase.client.Firebase;
import org.jquizmobile.app.question.Answer;
import org.jquizmobile.app.question.Question;
import static org.jquizmobile.app.QuestionsActivity.QUESTION_ID;
import static org.jquizmobile.app.question.QuestionsParser.MAX_QUESTIONS_NUMBER;

import java.util.ArrayList;
import java.util.List;


public class FinalScreenActivity extends AppCompatActivity {

    private List<Question> questions;

    private int totalScore;

    private Firebase firebaseProfiles;

    private LinearLayout resultsLayout;

    private TextView scoreLabel;

    private View finalScreenCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);
        resultsLayout = (LinearLayout) findViewById(R.id.resultsLayout);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        finalScreenCardView = findViewById(R.id.final_screen_card);
        fillQuestions();
        fillResultsLayout();
        Firebase.setAndroidContext(this);
        firebaseProfiles = new Firebase("https://incandescent-fire-9197.firebaseio.com/profiles");
    }

    @Override
    public void onBackPressed() {
        launchStartActivity();
    }

    private void launchStartActivity() {
        StartScreenActivity.launch(this, finalScreenCardView, getString(R.string.question_transition));
    }

    private void fillQuestions() {
        questions = new ArrayList<Question>();
        Bundle bundle = getIntent().getExtras();
        for (int i = 0; i < MAX_QUESTIONS_NUMBER; i++) {
            Question question = (Question) bundle.get(QUESTION_ID + i);
            if (question != null) {
                questions.add(question);
            }
        }
    }

    private void fillResultsLayout() {
        for (Question question : questions) {
            resultsLayout.addView(getQuestionText(question));
            resultsLayout.addView(getQuestionDescriptionText(question));
            int selectedCorrectAnswersNumber = 0;
            int correctAnswersNumber = 0;
            for (Answer answer : question.getAnswers()) {
                if (answer.isCorrect()) {
                    correctAnswersNumber++;
                    if (answer.isSelected()) {
                        selectedCorrectAnswersNumber++;
                    }
                }
                resultsLayout.addView(
                        question.isMultiple()
                                ? getAnswerCheckBox(answer)
                                : getAnswerRadioButton(answer)
                );

            }
            if (selectedCorrectAnswersNumber == correctAnswersNumber) {
                totalScore += question.getDifficulty();
            }
            resultsLayout.addView(getDivider());
        }
        scoreLabel.setText(getResources().getString(R.string.score) + " " + totalScore);
    }

    private TextView getQuestionText(Question question) {
        TextView questionText = new TextView(this);
        questionText.setText(Html.fromHtml(question.getQuestionText()));
        questionText.setTextColor(getResources().getColor(R.color.primary_text_color));
        questionText.setGravity(Gravity.START);
        return questionText;
    }

    private TextView getQuestionDescriptionText(Question question) {
        TextView questionDescriptionText = new TextView(this);
        questionDescriptionText.setText(Html.fromHtml(question.getDescription()));
        questionDescriptionText.setTextColor(getResources().getColor(R.color.primary_text_color));
        questionDescriptionText.setGravity(Gravity.START);
        return questionDescriptionText;
    }

    private View getDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(getResources().getColor(R.color.primary_divider_color));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        layoutParams.setMargins(
                0,
                Math.round(getResources().getDimension(R.dimen.divider_margin_top)),
                0,
                Math.round(getResources().getDimension(R.dimen.divider_margin_top))
        );
        divider.setLayoutParams(layoutParams);
        return divider;
    }

    private RadioButton getAnswerRadioButton(Answer answer) {
        RadioButton answerRadio = new RadioButton(this);
        answerRadio.setText(answer.getAnswerText());
        answerRadio.setChecked(answer.isSelected());
        answerRadio.setEnabled(false);
        if (answer.isSelected()) {
            answerRadio.setTextColor(getAnswerTextColor(answer.isCorrect()));
        }
        return answerRadio;
    }

    private CheckBox getAnswerCheckBox(Answer answer) {
        CheckBox answerCheckBox = new CheckBox(this);
        answerCheckBox.setText(answer.getAnswerText());
        answerCheckBox.setChecked(answer.isSelected());
        answerCheckBox.setEnabled(false);
        if (answer.isSelected()) {
            answerCheckBox.setTextColor(getAnswerTextColor(answer.isCorrect()));
        } else if (answer.isCorrect()){
            answerCheckBox.setTextColor(getResources().getColor(R.color.incorrect_answer));
        }
        return answerCheckBox;
    }

    private int getAnswerTextColor(boolean isAnswerCorrect) {
        return getResources().getColor(isAnswerCorrect ? R.color.correct_answer : R.color.incorrect_answer);
    }

    public void onTryAgainButtonClick(View view) {
        QuestionsActivity.launch(this, finalScreenCardView, getString(R.string.question_transition));
    }

    public void onShareButtonClick(View view) {
        //todo
    }
}