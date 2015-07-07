package org.jquizmobile.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class StartScreenActivity extends AppCompatActivity {

    View startScreenCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        startScreenCardView = findViewById(R.id.start_screen_card);
    }

    public void onStartButtonClicked(View v) {
        QuestionsActivity.launch(this, startScreenCardView, getString(R.string.question_transition));
    }

    public void onHighscoreButtonClicked(View v) {
        HighscoreActivity.launch(this, startScreenCardView, getString(R.string.question_transition));
    }
}
