package com.example.languagequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String PREFERENCES = "Preferences";
    private final Map<Integer, String> languageMap = Map.of(
            R.id.norwegian, "Norwegian",
            R.id.icelandic, "Icelandic",
            R.id.welsh, "Welsh",
            R.id.russian, "Russian"
    );
    private Menu menuRef;
    Map<String, String> dictionary;
    ArrayList<String> english;
    ArrayList<String> target;
    TextView textView;
    EditText editText;
    int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit = findViewById(R.id.btn_submit);
        Button reload = findViewById(R.id.btn_reload);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean("isFirstRun", true);
        if(isFirstRun){
            String[] languageChoices = languageMap.values().toArray(new String[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a language");
            builder.setSingleChoiceItems(languageChoices, -1, (dialog, which) ->{
                preferences.edit().putString("userChoice", languageChoices[which]).apply();
                preferences.edit().putBoolean("isFirstRun", false).apply();
                dialog.dismiss();
            });
            builder.setCancelable(false);
            builder.show();
        }

        String choice = preferences.getString("userChoice", "Norwegian");
        dictionary = readFile(choice + ".json");
        english = new ArrayList<>(dictionary.keySet());
        target = new ArrayList<>(dictionary.values());
        newQuestion();


        submit.setOnClickListener(view -> {
            submit.setEnabled(false);
            checkAnswer();
        });
        reload.setOnClickListener(view -> {
            submit.setEnabled(true);
            newQuestion();
        });
    }
    public void checkAnswer(){
        String x = editText.getText().toString();

        if(x.equals(english.get(key))){
            //correct
            editText.setBackgroundColor(Color.parseColor("#A5D6A7"));
        }else{
            //incorrect
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            editText.startAnimation(shake);
            editText.setBackgroundColor(Color.parseColor("#EF9A9A"));
            editText.setText(english.get(key));
        }
    }
    public void newQuestion(){
        key = new Random().nextInt(english.size());
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setText("");
        textView.setText(target.get(key));
    }
    public Map<String, String> readFile(String fileName){
        try {
            InputStream is = getAssets().open(fileName);
            String jsonText = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            JSONArray jsonArray = new JSONArray(jsonText);
            Map<String, String> dictionary = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                dictionary.put(obj.getString("eng"), obj.getString("target"));
            }
            return dictionary;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuRef = menu;
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String choice = preferences.getString("userChoice", "Norwegian");

        for (Map.Entry<Integer, String> entry : languageMap.entrySet()) {
            if (entry.getValue().equals(choice)) {
                menu.findItem(entry.getKey()).setChecked(true);
                break;
            }
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        for (int i = 0; i < menuRef.size(); i++) {
            MenuItem uItem = menuRef.getItem(i);
            uItem.setChecked(false);
        }
        String choice = languageMap.get(item.getItemId());
        item.setChecked(true);
        Toast.makeText(this, choice, Toast.LENGTH_SHORT).show();

        getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit().putString("userChoice", choice).apply();
        dictionary = readFile(choice + ".json");
        english = new ArrayList<>(dictionary.keySet());
        target = new ArrayList<>(dictionary.values());
        newQuestion();
        return true;
    }
}