package org.jquizmobile.app;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.jquizmobile.app.question.Answer;
import org.jquizmobile.app.question.PrettifyHighlighter;
import org.jquizmobile.app.question.Question;
import org.jquizmobile.app.question.QuestionsParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import prettify.PrettifyParser;
import syntaxhighlight.Parser;


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
    private View mQuestionTransitionLayout;

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
        mQuestionTransitionLayout = findViewById(R.id.questionTransitionLayout);

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

        loadQuestions();
        drawQuestion();
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
            Animator questionAnimator = getNextQuestionCloseCurrentAnimator();
            questionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    drawQuestion();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    getNextQuestionOpenNewAnimator().start();

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            questionAnimator.start();
        }
    }

    public static void launch(Activity activity, View sharedElement, String transitionName) {
        List<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
        View statusBar = activity.findViewById(android.R.id.statusBarBackground);
        pairs.add(Pair.create(sharedElement, transitionName));
        pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));

        ActivityOptionsCompat transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.get(0), pairs.get(1));
        Intent intent = new Intent(activity, QuestionsActivity.class);
        ActivityCompat.startActivity(activity, intent, transitionOptions.toBundle());
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

        Parser parser = new PrettifyParser();
        Pattern pattern = Pattern.compile("<pre><code>(.+)</code></pre>");
        Matcher codeMatcher = pattern.matcher(currentQuestion.getQuestionText());
        if (codeMatcher.find()) {
            String codeBlock = codeMatcher.group(1).replace("<br/>", "\n").replace("\\t", "\t");
            PrettifyHighlighter highlighter = new PrettifyHighlighter();
            String highlighted = highlighter.highlight("java", codeBlock);
            questionView.setText(Html.fromHtml(currentQuestion.getQuestionText().replace("<pre><code>" +
                    codeMatcher.group(1) + "</code></pre>", "") + highlighted));
        } else {
            questionView.setText(Html.fromHtml(currentQuestion.getQuestionText()));
        }
        questionsLayout.removeAllViews();
        if (currentQuestion.isMultiple()) {
            loadMultipleAnswersChoice(currentQuestion);
        } else {
            loadSingleAnswerChoice(currentQuestion);
        }
    }

    private AnimatorSet getNextQuestionCloseCurrentAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator flipAnimation = ObjectAnimator.ofFloat(mQuestionTransitionLayout, "rotationY", 0.0f, 360f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mQuestionTransitionLayout, "scaleX", 0.5f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mQuestionTransitionLayout, "scaleY", 0.5f);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.playTogether(flipAnimation, scaleDownX, scaleDownY);
        return animatorSet;
    }

    private AnimatorSet getNextQuestionOpenNewAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(mQuestionTransitionLayout, "scaleX", 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(mQuestionTransitionLayout, "scaleY", 1f);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.playTogether(scaleUpX, scaleUpY);
        return animatorSet;
    }

    private void loadSingleAnswerChoice(Question currentQuestion) {
        if (answersGroup != null) {
            answersGroup.removeAllViews();
            answersGroup = null;
        }
        answersGroup = new RadioGroup(this);
        answersGroup.setId(R.id.answers_group_id);
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
