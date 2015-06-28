package org.jquizmobile.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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


public class QuestionActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(QuestionActivity.class);

    private List<Question> questions;

    private int questionIndex;

    private TextView questionView;

    private LinearLayout questionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        questionView = (TextView) findViewById(R.id.questionView);
        questionsLayout = (LinearLayout) findViewById(R.id.questionsLayout);
        StringWriter stringWriter = new StringWriter();
        try {
            IOUtils.copy(getResources().openRawResource(R.raw.questions), stringWriter, "utf-8");
            JSONObject questionsObject = new JSONObject(stringWriter.toString());
            QuestionsParser questionsParser = new QuestionsParser(questionsObject);
            questions = questionsParser.parse();
        } catch (IOException e) {
            logger.error("Error while trying to access question file.");
        } catch (JSONException e) {
            logger.error("Error while trying to create json question object.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadQuestion();
    }

    public void onAnswerClick(View view) {
        questionIndex++;
        if (hasNextQuestion()) {
            loadQuestion();
        }
    }

    private boolean hasNextQuestion() {
        return questionIndex < questions.size();
    }

    private void loadQuestion() {
        Question question = questions.get(questionIndex);
        questionView.setText(question.getQuestionText());
        questionsLayout.removeAllViews();
        if (question.isMultiple()) {
            loadMultipleAnswersChoice(question);
        } else {
            loadSingleAnswerChoice(question);
        }
    }

    private void loadSingleAnswerChoice(Question question) {
        RadioGroup answersGroup = new RadioGroup(this);
        for (Answer answer : question.getAnswers()) {
            RadioButton answerRadioButton = new RadioButton(this);
            answerRadioButton.setText(answer.getAnswerText());
            answersGroup.addView(answerRadioButton);
        }
        questionsLayout.addView(answersGroup);
    }

    private void loadMultipleAnswersChoice(Question question) {
        for (Answer answer : question.getAnswers()) {
            CheckBox answerCheckBox = new CheckBox(this);
            answerCheckBox.setText(answer.getAnswerText());
            questionsLayout.addView(answerCheckBox);
        }
    }
}
