package org.jquizmobile.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.apache.commons.io.IOUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
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
}
