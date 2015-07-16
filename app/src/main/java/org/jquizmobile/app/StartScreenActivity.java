package org.jquizmobile.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;


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

    public static void launch(Activity activity, View sharedElement, String transitionName) {
        List<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
        View statusBar = activity.findViewById(android.R.id.statusBarBackground);
        pairs.add(Pair.create(sharedElement, transitionName));
        pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));

        ActivityOptionsCompat transitionOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.get(0), pairs.get(1));
        Intent intent = new Intent(activity, StartScreenActivity.class);
        ActivityCompat.startActivity(activity, intent, transitionOptions.toBundle());
    }
}
