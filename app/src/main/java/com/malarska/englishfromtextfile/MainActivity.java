package com.malarska.englishfromtextfile;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final static String STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final static String PATH = "/dictfiles/";
    private final static String FILENAME = "dict.txt";

    private String fileName = STORAGE + PATH + FILENAME;

    TextView question, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer, otherMeanings;
    TextView correct, inCorrect;
    Button btnNext, btnReset, btnClose, btnSetDictionary, btnRTFM;

    TextView totalWords, leftWords;

    private Map<String, List<String>> dictionaryAsMap;
    private List<String> keyList;

    private final List<TextView> textViewAnswers = new LinkedList<>();

    private String answer;
    private boolean giveAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissionToReadExternalStorage();

        if (getIntent().hasExtra("selectedFilePath")) {
            fileName = getIntent().getStringExtra("selectedFilePath");
        }

        question = findViewById(R.id.question);

        btnNext = findViewById(R.id.btnNext);
        btnReset = findViewById(R.id.btnReset);
        btnClose = findViewById(R.id.btnClose);
        btnSetDictionary = findViewById(R.id.btnDictionary);
        btnRTFM = findViewById(R.id.btnRTFM);

        firstAnswer = findViewById(R.id.firstAnswer);
        secondAnswer = findViewById(R.id.secondAnswer);
        thirdAnswer = findViewById(R.id.thirdAnswer);
        fourthAnswer = findViewById(R.id.fourthAnswer);
        otherMeanings = findViewById(R.id.otherMeanings);
        textViewAnswers.add(firstAnswer);
        textViewAnswers.add(secondAnswer);
        textViewAnswers.add(thirdAnswer);
        textViewAnswers.add(fourthAnswer);

        correct = findViewById(R.id.correct);
        inCorrect = findViewById(R.id.inCorrect);

        totalWords = findViewById(R.id.totalWords);
        leftWords = findViewById(R.id.leftWords);

        loadDataFromFile(fileName);
        setListenerToTextViewAnswers();
        generateRandomQuery();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandomQuery();
                leftWords.setText(String.valueOf(dictionaryAsMap.size()));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromFile(fileName);
                correct.setText("0");
                inCorrect.setText("0");
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "See you soon \n Have a nice day", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        btnSetDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetDictionary.class));
                finish();
            }
        });

        btnRTFM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Manual.class));
                finish();
            }
        });

        otherMeanings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherMeanings.setText(dictionaryAsMap.get(question.getText())
                        .toString().replace("[", "").replace("]", ""));
            }
        });
    }

    private void checkPermissionToReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void setListenerToTextViewAnswers() {
        for (TextView t : textViewAnswers) {
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!giveAnswer) {
                        if (answer.equals(t.getText())) {
                            t.setBackgroundColor(Color.GREEN);
                            incrementCorrect();
                            otherMeanings.setText(dictionaryAsMap.get(question.getText()).toString().replace("[", "").replace("]", ""));
                            dictionaryAsMap.remove(question.getText());
                        } else {
                            t.setBackgroundColor(Color.RED);
                            incrementInCorrect();
                            for (TextView t : textViewAnswers) {
                                if (answer.equals(t.getText()))
                                    t.setBackgroundColor(Color.GREEN);
                            }
                        }
                    }
                    giveAnswer = true;
                }
            });
        }
    }

    private void loadDataFromFile(String fileName) {
        try {
            dictionaryAsMap = getMapFromFile(fileName);
            totalWords.setText(String.valueOf(dictionaryAsMap.size()));
            leftWords.setText(String.valueOf(dictionaryAsMap.size()));
        } catch (IOException e) {
            System.out.println("Message from loadDataFromFile");
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Koniec błędu");
        }
    }

    private void generateRandomQuery() {
        if (dictionaryAsMap.isEmpty()) {
            loadDataFromFile(fileName);
        }
        otherMeanings.setText("- - - - - - - - - - - - - -");

        for (TextView t : textViewAnswers) {
            t.setBackgroundColor(Color.GRAY);
        }
        giveAnswer = false;
        keyList = getKeyListFromMap(dictionaryAsMap);
        List<String> shuffleKeyList = new ArrayList<>(keyList);
        Collections.shuffle(shuffleKeyList);
        question.setText(shuffleKeyList.get(0));
        answer = dictionaryAsMap.get(shuffleKeyList.get(0)).get(0);
        List<Integer> randNum = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(randNum);
        int i = 0;
        for (TextView t : textViewAnswers) {
            if (randNum.get(i) >= shuffleKeyList.size())
                randNum.set(i, shuffleKeyList.size() - 1);
            t.setText(dictionaryAsMap.get(shuffleKeyList.get(randNum.get(i))).get(0));
            i++;
        }

//        firstAnswer.setText(dictionaryAsMap.get(shuffleKeyList.get(randNum.get(0))).get(0));
//        secondAnswer.setText(dictionaryAsMap.get(shuffleKeyList.get(randNum.get(1))).get(0));
//        thirdAnswer.setText(dictionaryAsMap.get(shuffleKeyList.get(randNum.get(2))).get(0));
//        fourthAnswer.setText(dictionaryAsMap.get(shuffleKeyList.get(randNum.get(3))).get(0));

    }

    private Map<String, List<String>> getMapFromFile(String fileName) throws IOException {
        System.out.println("Laduje slownik: " + fileName);
        Map<String, List<String>> dictionary = new TreeMap<>();

        try {
            String line;
            //String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            //String pathDir = baseDir + PATH;
            File file = new File(fileName);
            //FileInputStream fis = null;
            InputStream is = null;
            BufferedReader br;
            if (file.exists()) {
                is = new FileInputStream(file);
                br = new BufferedReader(new InputStreamReader(is));
                System.out.println("Plik isnieje");
            } else {
                System.out.println("Plik nieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee       isnieje");
                int fileResourceId = R.raw.dict;
                is = this.getResources().openRawResource(fileResourceId);
                br = new BufferedReader(new InputStreamReader(is));
            }

            while ((line = br.readLine()) != null) {
                String[] tab = line.split("[-,]");
                LinkedList wordTranslete = new LinkedList();
                for (int i = 1; i < tab.length; i++) {
                    wordTranslete.add(tab[i].trim());
                }
                dictionary.put(tab[0].replaceAll("[^\\x00-\\x7F]", "").trim(), wordTranslete);
            }
//            if (fis != null)
//                fis.close();
            if (is != null)
                is.close();

        } catch (FileNotFoundException e) {
            System.out.println("Błąd z getMapFromFile(): " + e.getMessage());
            e.printStackTrace();
        }
        return sortBubbleByMe(dictionary);
    }

    private List<String> getKeyListFromMap(Map<String, List<String>> dictionary) {
        return new LinkedList<>(dictionary.keySet());
    }

    private Map<String, List<String>> sortBubbleByMe(Map<String, List<String>> map) {
        List<Map.Entry<String, List<String>>> listOfMap = new LinkedList<>(map.entrySet());
        Map.Entry<String, List<String>> temp1;
        Map<String, List<String>> orderMap = new LinkedHashMap<>();
        for (int j = 0; j < listOfMap.size(); j++) {
            for (int i = j + 1; i < listOfMap.size(); i++) {
                if (listOfMap.get(i).getKey().compareTo(listOfMap.get(j).getKey()) < 0) {
                    temp1 = listOfMap.get(j);
                    listOfMap.set(j, listOfMap.get(i));
                    listOfMap.set(i, temp1);
                }
            }
        }
        for (Map.Entry<String, List<String>> e : listOfMap) {
            orderMap.put(e.getKey(), e.getValue());
        }
        return orderMap;
    }

    private void incrementCorrect() {
        try {
            correct.setText("" + (Integer.parseInt(correct.getText().toString()) + 1));
        } catch (NumberFormatException e) {
            System.out.println("Error in increment correct: " + e.getMessage());
        }
    }

    private void incrementInCorrect() {
        try {
            inCorrect.setText("" + (Integer.parseInt(inCorrect.getText().toString()) + 1));
        } catch (NumberFormatException e) {
            System.out.println("Error in increment correct: " + e.getMessage());
        }
    }

    public void printMap() {
        for (Map.Entry<String, List<String>> entry : dictionaryAsMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}