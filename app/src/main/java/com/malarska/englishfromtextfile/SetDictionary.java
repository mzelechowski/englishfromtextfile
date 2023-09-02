package com.malarska.englishfromtextfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

public class SetDictionary extends AppCompatActivity {

    private final static String PATH = "/dictfiles/";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ListView fileListView;
    private ArrayAdapter<String> adapter;
    private List<String> fileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dictionary);
        fileListView = findViewById(R.id.fileListView);
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        fileListView.setAdapter(adapter);

        searchFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH));

        fileListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFilePath = fileList.get(position);
            Intent intent = new Intent(SetDictionary.this, MainActivity.class);
            intent.putExtra("selectedFilePath", selectedFilePath);
            if (!selectedFilePath.equals("Back to Main")) {
                Toast.makeText(getApplicationContext(), "The dictionary has been set to: \n" + selectedFilePath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "The dictionary has not been changed", Toast.LENGTH_LONG).show();
            }
            startActivity(intent);
            finish();
        });
    }

    private void searchFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchFiles(file); // Rekurencyjnie przeszukaj podkatalogi
                } else {
                    fileList.add(file.getAbsolutePath());
                }
            }
            adapter.notifyDataSetChanged(); // Zaktualizuj ListView
        } else {
            fileList.add("Back to Main");
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "check if you have directory \"/dictfiles/\" with files *.txt", Toast.LENGTH_LONG).show();
        }
    }


}