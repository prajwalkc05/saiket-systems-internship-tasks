package com.example.simplequizapp;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuizRepository {

    public List<Question> loadQuestions(Context context) {
        List<Question> questionList = new ArrayList<>();
        String json;
        try {
            InputStream is = context.getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.getInt("id");
                String text = obj.getString("text");
                String correctAnswer = obj.getString("correctAnswer");
                
                JSONArray optionsArray = obj.getJSONArray("options");
                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }

                questionList.add(new Question(id, text, options, correctAnswer));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }
}
