package org.jquizmobile.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.jquizmobile.app.question.Question;
import static org.jquizmobile.app.QuestionsActivity.QUESTION_ID;
import static org.jquizmobile.app.question.QuestionsParser.MAX_QUESTIONS_NUMBER;

import java.util.ArrayList;
import java.util.List;


public class FinalScreenActivity extends AppCompatActivity {

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);
        fillQuestions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startScreenActivity = new Intent(this, StartScreenActivity.class);
        startActivity(startScreenActivity);
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
}
