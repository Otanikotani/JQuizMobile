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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.jquizmobile.app.highscore.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class HighscoreActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(HighscoreActivity.class);

    private List<Profile> profiles = new ArrayList<Profile>();

    private Firebase jQuizDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        Firebase.setAndroidContext(this);
        jQuizDb = new Firebase("https://incandescent-fire-9197.firebaseio.com");
        jQuizDb.child("/profiles")
                .orderByChild("highest_score")
                .limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("Read is failed: " + firebaseError.getMessage());
                    }
                });
    }

    public static void launch(Activity activity, View sharedElement, String transitionName) {
        List<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
        View statusBar = activity.findViewById(android.R.id.statusBarBackground);
        pairs.add(Pair.create(sharedElement, transitionName));
        pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));

        ActivityOptionsCompat transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.get(0), pairs.get(1));
        Intent intent = new Intent(activity, HighscoreActivity.class);
        ActivityCompat.startActivity(activity, intent, transitionOptions.toBundle());
    }
}
