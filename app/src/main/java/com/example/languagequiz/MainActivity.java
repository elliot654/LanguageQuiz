package com.example.languagequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LinearLayout layout;
    Switch aSwitch;
    TextView textView;
    EditText editText;
    Button submit, reload;
    int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        aSwitch = findViewById(R.id.difficulty);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        submit = findViewById(R.id.btn_submit);
        reload = findViewById(R.id.btn_reload);

        ArrayList<String> english = null;
        ArrayList<String> norsk = null;
        try {
            english = ReadFile("english.txt");
            norsk = ReadFile("norsk.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();

        ArrayList<String> finalEnglish = english;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x = editText.getText().toString();
                if(x.equals(finalEnglish.get(key))){
                    layout.setBackgroundColor(Color.parseColor("#13bd19"));
                }else{
                    layout.setBackgroundColor(Color.parseColor("#de0909"));
                    textView.setText(finalEnglish.get(key));
                }
            }
        });
        ArrayList<String> finalNorsk = norsk;
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aSwitch.isChecked()){
                    key = random.nextInt(1000);
                }else{
                    key = random.nextInt(100);
                }
                layout.setBackgroundColor(Color.WHITE);
                editText.setText("");
                textView.setText(finalNorsk.get(key));
            }
        });
    }

    public ArrayList<String> ReadFile(String fileName) throws IOException {
        InputStream is = this.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ArrayList<String> list = new ArrayList<String>();
        String str;
            while((str = reader.readLine()) != null){
                list.add(str);
            }
        return list;
    }
}