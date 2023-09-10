package com.malarska.englishfromtextfile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import java.util.List;

public class SetDictionary extends AppCompatActivity {

    private final static String PATH = "/dictfiles/";
    private final static String FILENAME = "dict.txt";
    private ListView fileListView;
    private ArrayAdapter<String> adapter;
    private List<String> fileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dictionary);

        // String stringPathDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH;
        String stringPathDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + PATH;

        fileListView = findViewById(R.id.fileListView);
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        fileListView.setAdapter(adapter);
        searchFiles(new File(stringPathDirectory));

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

    private boolean createDirectoryAndFile(File directory) {
        boolean ifCreate = directory.mkdir();
        if (ifCreate) {
            System.out.println(this.getClass().getSimpleName() + " " + directory + " - The directory has been successfully created.");
        } else {
            System.out.println(this.getClass().getSimpleName() + "-- Failed to create directory or directory already exists, may already exist.");
        }
        File newFileDict = new File(directory, FILENAME);
        int fileResourceId = R.raw.dict;
        InputStream is = this.getResources().openRawResource(fileResourceId);

        try (InputStream fis = this.getResources().openRawResource(fileResourceId);
             FileOutputStream fos = new FileOutputStream(newFileDict)) {
            byte[] bufor = new byte[1024];
            int ilePrzeczytano;
            while ((ilePrzeczytano = fis.read(bufor)) != -1) {
                fos.write(bufor, 0, ilePrzeczytano);
            }
            System.out.println(" --------- The contents of the default file have been copied ------");
            fis.close();
            fos.close();
            return true;
        } catch (IOException e) {
            System.out.println("File not created: " + this.getClass().getSimpleName() + " --- " + e.getMessage());
            return false;
        }
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
            if (fileList.isEmpty()) {
                if (createDirectoryAndFile(directory))
                    searchFiles(directory);
            }
        } else {
            if (createDirectoryAndFile(directory)) {
                searchFiles(directory);
            } else {
                fileList.add("Back to Main");
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "check if you have directory \"/dictfiles/\" with files *.txt", Toast.LENGTH_LONG).show();
            }
        }
    }
}