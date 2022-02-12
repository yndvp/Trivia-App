package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.trivia.data.QuestionListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String MESSAGE_ID = "score";
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);

        binding.scoreTextView.setText(String.format("Current Score: %d", score.getScore()));
        binding.highestScoreTextView.setText(String.format("Highest Score: %d", prefs.getHighestScore()));

        questions = new Repository().getQuestions(new QuestionListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextview.setText((CharSequence) questionArrayList.get(currentQuestionIndex).getQuestion().toString());
                updateCounter(questionArrayList);
            }
        });

        binding.nextButton.setOnClickListener(view -> {
            getNextQuestion();
        });
        binding.trueButton.setOnClickListener(view -> {
            checkCorrect(true);
        });
        binding.falseButton.setOnClickListener(view -> {
            checkCorrect(false);
        });
    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
        updateQuestion();
    }

    private void checkCorrect(boolean answerInput) {
        boolean answer = questions.get(currentQuestionIndex).isAnswer();
        int snackMessageId = 0;
        if(answer == answerInput) {
            snackMessageId = R.string.correct_answer;
            fadeAnimtation();
            addPoints();
        } else {
            snackMessageId = R.string.inccorect_answer;
            shakeAnimation();
            deductPoints();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void updateCounter(ArrayList<Question> questions) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questions.size()));
    }

    private void updateQuestion() {
        String question = questions.get(currentQuestionIndex).getQuestion();
        binding.questionTextview.setText(question);
        updateCounter((ArrayList<Question>) questions);
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimtation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.scoreTextView.setText(String.format("Current Score: %d", score.getScore()));
    }

    private void deductPoints(){
        if(scoreCounter > 0) {
            scoreCounter -= 100;
            score.setScore(scoreCounter);
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
        binding.scoreTextView.setText(String.format("Current Score: %d", score.getScore()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighestScore(score.getScore());
    }
}