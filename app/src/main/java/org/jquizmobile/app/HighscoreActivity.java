package org.jquizmobile.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.jquizmobile.app.highscore.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class HighscoreActivity extends AppCompatActivity {

    public static final Logger logger = LoggerFactory.getLogger(HighscoreActivity.class);

    private static final String PARAM_ATTEMPTS = "attempts";

    private static final String PARAM_AVATAR = "avatar";

    private static final String PARAM_HIGHEST_SCORE = "highest_score";

    private static final int MAX_PROFILES = 10;

    private List<Profile> profiles = new ArrayList<Profile>();

    private TableLayout highscoreTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        highscoreTableLayout = (TableLayout) findViewById(R.id.highscore_table_layout);

        // Postpone the transition until the window's decor view has
        // finished its layout.
        final Activity thisActivity = this;
        ActivityCompat.postponeEnterTransition(thisActivity);
        final View decor = getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(thisActivity);
                return true;
            }
        });
        Firebase.setAndroidContext(this);
        fillHighscoreTableIfConnected();
    }

    public static void launch(Activity activity, View sharedElement, String transitionName) {
        List<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
        View statusBar = activity.findViewById(android.R.id.statusBarBackground);
        pairs.add(Pair.create(sharedElement, transitionName));
        pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));

        ActivityOptionsCompat transitionOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.get(0), pairs.get(1));
        Intent intent = new Intent(activity, HighscoreActivity.class);
        ActivityCompat.startActivity(activity, intent, transitionOptions.toBundle());
    }

    private void fillHighscoreTable() {
        Firebase firebaseProfiles = new Firebase("https://incandescent-fire-9197.firebaseio.com/profiles");
        firebaseProfiles.orderByChild(PARAM_HIGHEST_SCORE).limitToLast(MAX_PROFILES)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, HashMap> profilesMap = (HashMap<String, HashMap>) dataSnapshot.getValue();
                        fillProfiles(profilesMap);
                        showHighscoreTableResults();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        logger.error("Read is failed: " + firebaseError.getMessage());
                    }
                });
    }

    private void fillHighscoreTableIfConnected() {
        Firebase connectedRef = new Firebase("https://incandescent-fire-9197.firebaseio.com/.info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    fillHighscoreTable();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                logger.error("Listener was cancelled" + error.getMessage());
            }
        });
    }

    private void fillProfiles(Map<String, HashMap> profilesMap) {
        for (String profileName : profilesMap.keySet()) {
            Profile newProfile = new Profile();
            newProfile.setName(profileName);
            HashMap<String, Object> profileParams =
                    (HashMap<String, Object>) profilesMap.get(profileName);
            newProfile.setAttempts((Long) profileParams.get(PARAM_ATTEMPTS));
            newProfile.setAvatar((String) profileParams.get(PARAM_AVATAR));
            newProfile.setHighestScore((Long) profileParams.get(PARAM_HIGHEST_SCORE));
            profiles.add(newProfile);
        }
        Collections.sort(profiles, new Comparator<Profile>() {
            @Override
            public int compare(Profile profile, Profile t1) {
                return t1.getHighestScore().compareTo(profile.getHighestScore());
            }
        });
    }

    private void showHighscoreTableResults() {
        TableRow noHighscoresMessage = (TableRow) findViewById(R.id.no_highscores_message_id);
        if (profiles != null && !profiles.isEmpty()) {
            if (noHighscoresMessage != null) {
                highscoreTableLayout.removeView(noHighscoresMessage);
            }
            for (Profile profile : profiles) {
                TableRow tableRow = new TableRow(this);
                tableRow.addView(getHighscoreTableCell(profile.getName()));
                tableRow.addView(getHighscoreTableCell(String.valueOf(profile.getHighestScore())));
                highscoreTableLayout.addView(tableRow);
            }
        } else if (noHighscoresMessage == null) {
            TableRow tableRow = new TableRow(this);
            tableRow.setId(R.id.no_highscores_message_id);
            tableRow.addView(getHighscoreTableCell("No records found."));
            highscoreTableLayout.addView(tableRow);
        }
    }

    private TextView getHighscoreTableCell(String content) {
        TextView textView = new TextView(this);
        textView.setText(content);
        textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        textView.setPadding(
                Math.round(getResources().getDimension(R.dimen.content_layout_padding)),
                0,
                Math.round(getResources().getDimension(R.dimen.content_layout_padding)),
                Math.round(getResources().getDimension(R.dimen.content_layout_padding))
        );
        return textView;
    }
}
