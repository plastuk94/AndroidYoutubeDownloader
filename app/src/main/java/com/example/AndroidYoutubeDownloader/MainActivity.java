package com.example.AndroidYoutubeDownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void downloadVideo(android.view.View view) {

        String url = "";

        Button downloadButton        = (Button)      findViewById(R.id.button);
        EditText urlText             = (EditText)    findViewById(R.id.editTextTextPersonName);
        ProgressBar downloadProgress = (ProgressBar) findViewById(R.id.progressBar);
        RadioGroup radioGroup        = (RadioGroup)  findViewById(R.id.FormatGroup);

        downloadProgress.setVisibility(View.VISIBLE);

        int selectedButtonID = radioGroup.getCheckedRadioButtonId();

        RadioButton checkedButton    = (RadioButton) findViewById(selectedButtonID);
        String fileType              = checkedButton.getText().toString().toLowerCase();
        String urlTextContents       = urlText.getText().toString();

        if (!(urlText.getText().toString().equals(""))) {
            url = urlText.getText().toString();
        }

        try {
            YoutubeDL.getInstance().init(getApplication());
            FFmpeg.getInstance().init(getApplication());

        } catch (YoutubeDLException e) {
            Log.e(null,e.getMessage());
            Log.d(null, fileType);
        }
        File youtubeDLDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"youtubedl-android");

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");
        request.addOption("-f", "m4a");

        switch (fileType) {
            case "ogg":
                request.addOption("--audio-format", "vorbis");
                request.addOption("-x");
                break;
            case "mp3":
                request.addOption("--audio-format","mp3");
                request.addOption("-x");
                break;
        }

        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    YoutubeDL.getInstance().execute(request, (progress, etaInSeconds) -> {
                        System.out.println(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
                        downloadProgress.setProgress((int)progress);
                        if (progress == 100) {
                            Toast.makeText(getApplicationContext(), "Song downloaded", Toast.LENGTH_LONG);
                        }
                    });
                } catch (YoutubeDLException e) {
                    Log.e(null,e.getMessage());
                    Log.d(null, fileType);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    downloadButton.setText(e.getMessage());
                }
            }
        });

        downloadThread.start();
    }
}