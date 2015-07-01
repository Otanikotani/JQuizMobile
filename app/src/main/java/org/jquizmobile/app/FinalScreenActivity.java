package org.jquizmobile.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import org.jquizmobile.app.question.Answer;
import org.jquizmobile.app.question.Question;
import static org.jquizmobile.app.QuestionsActivity.QUESTION_ID;
import static org.jquizmobile.app.question.QuestionsParser.MAX_QUESTIONS_NUMBER;

import java.util.ArrayList;
import java.util.List;


public class FinalScreenActivity extends AppCompatActivity {

    private List<Question> questions;

    private LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);
        resultsLayout = (LinearLayout) findViewById(R.id.resultsLayout);
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
            for (Answer answer : question.getAnswers()) {
                resultsLayout.addView(
                        question.isMultiple()
                                ? getAnswerCheckBox(answer)
                                : getAnswerRadioButton(answer)
                );
            }
            resultsLayout.addView(getDivider());
        }
    }

    private TextView getQuestionHeader(Question question) {
        TextView questionHeader = new TextView(this);
        questionHeader.setText((questions.indexOf(question) + 1) + ". " + question.getQuestionText());
        questionHeader.setTextColor(0xFF000000);
        questionHeader.setGravity(Gravity.START);
        return questionHeader;
    }

    private View getDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(0xFFB6B6B6);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(0, 5, 0, 5);
        divider.setLayoutParams(layoutParams);
        return divider;
    }

    private RadioButton getAnswerRadioButton(Answer answer) {
        RadioButton answerRadio = new RadioButton(this);
        answerRadio.setText(answer.getAnswerText());
        answerRadio.setChecked(answer.isSelected());
        answerRadio.setEnabled(false);
        if (answer.isSelected()) {
            answerRadio.setTextColor(answer.isCorrect() ? 0xFF4CAF50 : 0xFFF44336);
        }
        return answerRadio;
    }

    private CheckBox getAnswerCheckBox(Answer answer) {
        CheckBox answerCheckBox = new CheckBox(this);
        answerCheckBox.setText(answer.getAnswerText());
        answerCheckBox.setChecked(answer.isSelected());
        answerCheckBox.setEnabled(false);
        answerCheckBox.setTextColor(0xFF000000);
        return answerCheckBox;
    }
}