package com.example.trivia.data;

import com.example.trivia.model.Question;

import java.util.ArrayList;

public interface QuestionListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
