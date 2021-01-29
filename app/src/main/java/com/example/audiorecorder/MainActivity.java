package com.example.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button replay;
    ImageButton play ,stop;
    String path;
    MediaRecorder media;
    Chronometer timer;
    public static final int RequestPermissionCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    play = (ImageButton) findViewById(R.id.play);
    replay = (Button) findViewById(R.id.replay);
    stop = (ImageButton) findViewById(R.id.stop);
    timer = (Chronometer) findViewById(R.id.timer);

    stop.setEnabled(false);
    replay.setEnabled(false);

    play.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()) {
                 path = Environment.getExternalStorageDirectory() + "/" + "voice.3gp";
                 //use the below path for deploying in phone. This is the document folder path which can be easily accessed
                //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "voice.3gp";
                File filename = new File(path);
                if(filename.exists()){
                    filename.delete();
                }
                media = new MediaRecorder();
                media.setAudioSource(MediaRecorder.AudioSource.MIC);
                media.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                media.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                media.setOutputFile(path);
                try {
                    media.prepare();
                    media.start();
                    timer.start();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                play.setEnabled(false);
                stop.setEnabled(true);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please give permission",Toast.LENGTH_LONG).show();
                 requestPermission();
            }

        }
    });

    stop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        media.stop();
        timer.stop();
        Toast.makeText(getApplicationContext(), "Recording stopped",Toast.LENGTH_LONG).show();
        replay.setEnabled(true);
        play.setEnabled(false);
        stop.setEnabled(false);
        }
    });

    replay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            Toast.makeText(MainActivity.this, "Recording Playing",
                    Toast.LENGTH_LONG).show();
            play.setEnabled(true);
            stop.setEnabled(true);

        }
    });
    }

    public void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[] {WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied. Sorry you need to have valid permissions",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        boolean val = result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
        return val;
    }
}
