package org.jquizmobile.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.*;
import org.apache.commons.io.IOUtils;
import org.jquizmobile.app.question.Answer;
import org.jquizmobile.app.question.Question;
import org.jquizmobile.app.question.QuestionsParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;


public class QuestionsActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(QuestionsActivity.class);

    public static final String QUESTION_ID = "question_id_";

    private List<Question> questions;

    private int currentQuestionIndex;

    private View.OnClickListener onAnswerClickListener;

    private TextView questionView;

    private LinearLayout questionsLayout;

    private Button answerButton;

    private TextView questionsNumberView;

    private RadioGroup answersGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        questionView = (TextView) findViewById(R.id.questionView);
        questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        answerButton = (Button) findViewById(R.id.answerButton);
        onAnswerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAnswerSelected()) {
                    answerButton.setEnabled(true);
                }
            }
        };
        questionsNumberView = (TextView) findViewById(R.id.questionsNumberView);
        loadQuestions();
        drawQuestion();
    }

    @Override
    public void onBackPressed() {
        Intent questionsActivity = new Intent(this, QuestionsActivity.class);
        startActivity(questionsActivity);
    }

    public void onAnswerButtonClick(View view) {
        addAnswersToSelected();
        currentQuestionIndex++;
        if (!hasNextQuestion()) {
            Intent finalScreenActivity = new Intent(this, FinalScreenActivity.class);
            for (Question question : questions) {
                int questionIndex = questions.indexOf(question);
                finalScreenActivity.putExtra(QUESTION_ID + questionIndex, question);
            }
            startActivity(finalScreenActivity);
        } else {
            drawQuestion();
        }
    }

    private void loadQuestions() {
        StringWriter stringWriter = new StringWriter();
        try {
            IOUtils.copy(getResources().openRawResource(R.raw.questions), stringWriter, "utf-8");
            JSONObject questionsObject = new JSONObject(stringWriter.toString());
            QuestionsParser questionsParser = new QuestionsParser(questionsObject);
            questions = questionsParser.getRandomQuestions();
            if (questions == null || questions.isEmpty()) {
                //todo
            }
        } catch (IOException e) {
            logger.error("Error while trying to access question file.");
        } catch (JSONException e) {
            logger.error("Error while trying to create json question object.");
        }
    }

    private boolean isAnswerSelected() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.isMultiple()) {
            int childCount = questionsLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                CheckBox child = (CheckBox) questionsLayout.getChildAt(i);
                if (child.isChecked()) {
                    return true;
                }
            }
        } else {
            return answersGroup.getCheckedRadioButtonId() > -1;
        }
        return false;
    }

    private boolean hasNextQuestion() {
        return currentQuestionIndex < questions.size();
    }

    private void drawQuestion() {
        answerButton.setEnabled(false);
        questionsNumberView.setText((currentQuestionIndex + 1) + "/" + questions.size());
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionView.setText(Html.fromHtml(currentQuestion.getQuestionText()));
        questionsLayout.removeAllViews();
        if (currentQuestion.isMultiple()) {
            loadMultipleAnswersChoice(currentQuestion);
        } else {
            loadSingleAnswerChoice(currentQuestion);
        }
    }

    private void loadSingleAnswerChoice(Question currentQuestion) {
        if (answersGroup != null) {
            answersGroup.removeAllViews();
            answersGroup = null;
        }
        answersGroup = new RadioGroup(this);
        answersGroup.setId(R.id.answersGroupId);
        for (Answer answer : currentQuestion.getAnswers()) {
            RadioButton answerRadioButton = new RadioButton(this);
            answerRadioButton.setText(answer.getAnswerText());
            answerRadioButton.setOnClickListener(onAnswerClickListener);
            answersGroup.addView(answerRadioButton);
        }
        questionsLayout.addView(answersGroup);
    }

    private void loadMultipleAnswersChoice(Question question) {
        for (Answer answer : question.getAnswers()) {
            CheckBox answerCheckBox = new CheckBox(this);
            answerCheckBox.setText(answer.getAnswerText());
            answerCheckBox.setOnClickListener(onAnswerClickListener);
            questionsLayout.addView(answerCheckBox);
        }
    }

    private void addAnswersToSelected() {
        Question question = questions.get(currentQuestionIndex);
        if (question.isMultiple()) {
            addSelectedMultipleAnswers(question);
        } else {
            addSelectedSingleAnswer(question);
        }
    }

    private void addSelectedSingleAnswer(Question question) {
        int selectedAnswerRadioId = answersGroup.getCheckedRadioButtonId();
        String selectedAnswerText = ((RadioButton) findViewById(selectedAnswerRadioId)).getText().toString();
        question.makeAnswerSelected(selectedAnswerText);
    }

    private void addSelectedMultipleAnswers(Question question) {
        for (int i = 0; i < questionsLayout.getChildCount(); i++) {
            CheckBox answerCheckBox = (CheckBox) questionsLayout.getChildAt(i);
            if (answerCheckBox.isChecked()) {
                question.makeAnswerSelected(answerCheckBox.getText().toString());
            }
        }
    }
}
