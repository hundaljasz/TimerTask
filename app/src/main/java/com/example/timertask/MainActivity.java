package com.example.timertask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timertask.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private CountDownTimer workoutTimer,restTimer;
    private long workoutTimeLeftInMillis, restTimeLeftInMillis;
    private int userWorkoutTime, restWorkoutTime;
    private String timeLeftString;
    private boolean restWorkoutFlag = false;
    private int min,sec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(String.valueOf(binding.workoutEditText.getText()).trim())) {
                    Toast.makeText(MainActivity.this, "Enter Workout Time..", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTimer();
            }
        });

        binding.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(String.valueOf(binding.workoutEditText.getText()).trim())) {
                    Toast.makeText(MainActivity.this, "Enter Workout Time..", Toast.LENGTH_SHORT).show();
                    return;
                }
                stopTimer();
            }
        });
    }

    public void startTimer(){
        userWorkoutTime = Integer.valueOf(String.valueOf(binding.workoutEditText.getText()));
        if(!TextUtils.isEmpty(String.valueOf(binding.restDurationEdittext.getText()).trim())) {
            restWorkoutFlag = true;
            restWorkoutTime = Integer.valueOf(String.valueOf(binding.restDurationEdittext.getText()));
        }
        Log.v("REST",String.valueOf(restWorkoutFlag));
        workoutTimeLeftInMillis = TimeUnit.SECONDS.toMillis(userWorkoutTime);
        workoutTimer = new CountDownTimer(workoutTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                workoutTimeLeftInMillis = l;
                updateUI(l,false);
            }

            @Override
            public void onFinish() {
                sound();
                if(restWorkoutFlag) {
                    restTimerStart();
                }
                Toast.makeText(MainActivity.this, "Workout Time is Completed..", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    public void restTimerStart() {
        restTimeLeftInMillis = TimeUnit.SECONDS.toMillis(restWorkoutTime);
        restTimer = new CountDownTimer(restTimeLeftInMillis,1000) {
            @Override
            public void onTick(long R) {
                restTimeLeftInMillis = R;
                updateUI(R,true);
            }

            @Override
            public void onFinish() {
                sound();
                startTimer();
                Toast.makeText(MainActivity.this, "Rest Time is Completed..", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    public void stopTimer () {
        workoutTimer.cancel();
        if(restWorkoutFlag) {
            restTimer.cancel();
        }
    }

    public void updateUI(long time, boolean restFlag){
        int min = (int) time / 60000;
        int sec = (int) time % 60000 / 1000;
        binding.progressBar.setProgress(sec);
        timeLeftString = "" + min + ":";
        if (sec<10) timeLeftString += "0";
        timeLeftString += sec;
        if(restFlag) {
            binding.restTimerTextview.setText("Rest Time Remaining: "+timeLeftString);
        } else {
            binding.workoutTimerTextview.setText("Workout Time Remaining: "+timeLeftString);
        }
    }

    public void notifyUser() {
        NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.timer)
                .setContentTitle("Workout Notification")
                .setContentText("Your timer has been completed....")
                .setPriority(Notification.PRIORITY_MAX).build();

        NM.notify(0,notification);
    }

    public void sound() {
        notifyUser();
        // Create a MediaPlayer instance and load the sound file
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alert);

        // Set a completion listener to release the MediaPlayer resources when the sound finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        // Start the sound playback
        mediaPlayer.start();
    }
}
