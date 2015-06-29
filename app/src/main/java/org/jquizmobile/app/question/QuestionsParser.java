package org.jquizmobile.app.question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsParser {

    public static final Logger logger = LoggerFactory.getLogger(QuestionsParser.class);

    private static final int MAX_QUESTIONS_NUMBER = 5;

    private static final String QUESTIONS = "questions";

    private static final String QUESTION = "question";

    private static final String DIFFICULTY = "difficulty";

    private static final String MULTIPLE = "multiple";

    private static final String TOPICS = "topics";

    private static final String ANSWERS = "answers";

    private static final String ANSWER = "answer";

    private static final String CORRECT = "correct";

    private static final String DESCRIPTION = "description";

    private JSONObject questionsObject;

    public QuestionsParser(JSONObject questionsObject) {
        this.questionsObject = questionsObject;
    }

    public List<Question> getRandomQuestions() {
        List<Question> randomQuestions = new ArrayList<Question>();
        List<Question> allQuestions = parse();
        if (allQuestions != null && !allQuestions.isEmpty()) {
            Collections.shuffle(allQuestions);
            randomQuestions.addAll(allQuestions.subList(0, MAX_QUESTIONS_NUMBER));
        }
        return randomQuestions;
    }

    private List<Question> parse() {
        List<Question> questions = new ArrayList<Question>();
        try {
            JSONArray questionsJsonArray = questionsObject.getJSONArray(QUESTIONS);
            for (int i = 0; i < questionsJsonArray.length(); i++) {
                Question question = getFilledQuestion(questionsJsonArray.getJSONObject(i));
                questions.add(question);
            }
        } catch (JSONException e) {
            logger.error("Error while trying to parse JSON.\n" + e.getMessage());
        }
        return questions;
    }

    private Question getFilledQuestion(JSONObject questionJson) throws JSONException {
        Question question = new Question();
        question.setQuestionText(questionJson.getString(QUESTION));
        question.setDifficulty(questionJson.getInt(DIFFICULTY));
        question.setIsMultiple(questionJson.getBoolean(MULTIPLE));
        JSONArray topicsArray = questionJson.getJSONArray(TOPICS);
        List<String> topics = new ArrayList<String>();
        for (int i = 0; i < topicsArray.length(); i++) {
            topics.add(topicsArray.getString(i));
        }
        question.setTopics(topics);
        question.setAnswers(getFilledAnswers(questionJson));
        return question;
    }

    private List<Answer> getFilledAnswers(JSONObject questionJson) throws JSONException {
        List<Answer> answers = new ArrayList<Answer>();
        JSONArray answersArray = questionJson.getJSONArray(ANSWERS);
        for (int i = 0; i < answersArray.length(); i++) {
            JSONObject answerJson = answersArray.getJSONObject(i);
            answers.add(getFilledAnswer(answerJson));
        }
        return answers;
    }

    private Answer getFilledAnswer(JSONObject answerJson) throws JSONException {
        Answer answer = new Answer();
        answer.setAnswerText(answerJson.getString(ANSWER));
        answer.setIsCorrect(answerJson.getBoolean(CORRECT));
        answer.setDescription(answerJson.getString(DESCRIPTION));
        return answer;
    }
}
