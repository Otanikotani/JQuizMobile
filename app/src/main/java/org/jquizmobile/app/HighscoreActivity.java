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
import java.util.HashMap;
import java.util.List;


public class HighscoreActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(HighscoreActivity.class);

    private static final String PARAM_ATTEMPTS = "attempts";
    private static final String PARAM_AVATAR = "avatar";
    private static final String PARAM_HIGHEST_SCORE = "highest_score";

    private List<Profile> profiles = new ArrayList<Profile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        Firebase.setAndroidContext(this);
        Firebase firebaseProfiles = new Firebase("https://incandescent-fire-9197.firebaseio.com/profiles");
        firebaseProfiles.orderByChild(PARAM_HIGHEST_SCORE).limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap> profilesMap = (HashMap<String, HashMap>) dataSnapshot.getValue();
                for (String profileName : profilesMap.keySet()) {
                    Profile newProfile = new Profile();
                    newProfile.setName(profileName);
                    HashMap<String, Object> profileParams = (HashMap<String, Object>) profilesMap.get(profileName);
                    newProfile.setAttempts((Long) profileParams.get(PARAM_ATTEMPTS));
                    newProfile.setAvatar((String) profileParams.get(PARAM_AVATAR));
                    newProfile.setHighestScore((Long) profileParams.get(PARAM_HIGHEST_SCORE));
                    profiles.add(newProfile);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                logger.error("Read is failed: " + firebaseError.getMessage());
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
