package org.jquizmobile.app;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.jquizmobile.app.question.Answer;
import org.jquizmobile.app.question.Question;
import static org.jquizmobile.app.QuestionsActivity.QUESTION_ID;
import static org.jquizmobile.app.question.QuestionsParser.MAX_QUESTIONS_NUMBER;

import java.util.ArrayList;
import java.util.List;


public class FinalScreenActivity extends AppCompatActivity {

    private List<Question> questions;

    private int totalScore;

    private LinearLayout resultsLayout;

    private TextView scoreLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);
        resultsLayout = (LinearLayout) findViewById(R.id.resultsLayout);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        fillQuestions();
        fillResultsLayout();
    }

    @Override
    public void onBackPressed() {
        //do nothing
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
            resultsLayout.addView(getQuestionHeader(question));
            resultsLayout.addView(getDivider());
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
        resultsLayout.addView(getTryAgainButton());
        scoreLabel.setText(getResources().getString(R.string.score) + " " + totalScore);
    }

    private TextView getQuestionHeader(Question question) {
        TextView questionHeader = new TextView(this);
        questionHeader.setText((questions.indexOf(question) + 1) + ". " + question.getQuestionText());
        questionHeader.setTextColor(getResources().getColor(R.color.primary_text_color));
        questionHeader.setGravity(Gravity.START);
        return questionHeader;
    }

    private View getDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(getResources().getColor(R.color.primary_divider_color));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
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
        Intent questionsActivity = new Intent(this, QuestionsActivity.class);
        startActivity(questionsActivity);
    }

    private Button getTryAgainButton() {
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(getResources().getColor(R.color.primary_button_text_color));
        button.setText(R.string.try_again);
        button.setGravity(Gravity.CENTER_HORIZONTAL);
        button.setLayoutParams(
                new ViewGroup.LayoutParams(
                        Math.round(getResources().getDimension(R.dimen.button_width)),
                        Math.round(getResources().getDimension(R.dimen.button_height))
                )
        );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgainButtonClick(view);
            }
        });
        return button;
    }
}