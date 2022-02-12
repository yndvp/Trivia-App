package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    public static final String HIGHEST_SCORE = "highest_score";
    private SharedPreferences sharedPreferences;

    public Prefs(Activity context){
        this.sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighestScore(int score) {
        int currentScore = score;
        int lastScore = sharedPreferences.getInt(HIGHEST_SCORE, 0);

        if(currentScore > lastScore) {
            sharedPreferences.edit().putInt(HIGHEST_SCORE, currentScore).apply();
        }
    }

    public int getHighestScore(){
        return sharedPreferences.getInt(HIGHEST_SCORE, 0);
    }

}
