package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.QuestionListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        questions = new Repository().getQuestions(new QuestionListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextview.setText((CharSequence) questionArrayList.get(currentQuestionIndex).getQuestion().toString());
                updateCounter(questionArrayList);
            }
        });

        binding.nextButton.setOnClickListener(view -> {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
            updateQuestion();
        });
        binding.trueButton.setOnClickListener(view -> {
            checkCorrect(true);
        });
        binding.falseButton.setOnClickListener(view -> {
            checkCorrect(false);
        });
    }

    private void checkCorrect(boolean answerInput) {
        boolean answer = questions.get(currentQuestionIndex).isAnswer();
        int snackMessageId = 0;
        if(answer == answerInput) {
            snackMessageId = R.string.correct_answer;
        } else {
            snackMessageId = R.string.inccorect_answer;
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
}