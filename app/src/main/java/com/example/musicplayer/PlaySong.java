package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "back clicked", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView,currentTime,fullTime;
    ImageView play, previous, next,forward,replay,loop;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        forward = findViewById(R.id.forward);
        replay = findViewById(R.id.replay);
        currentTime = findViewById(R.id.currentTime);
        fullTime = findViewById(R.id.wholeTime);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

//        Toast.makeText(getApplicationContext(), "duration is "+mediaPlayer.getDuration(), Toast.LENGTH_SHORT).show();
        fullTime.setText(getfulltime(mediaPlayer.getDuration()));


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mediaPlayer.isPlaying())
                {
                    next.performClick();
                }
                else
                {
                    next.performClick();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenttime = getfulltime(mediaPlayer.getCurrentPosition());
                currentTime.setText(currenttime);
                handler.postDelayed(this,delay);
            }
        },delay);

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
//                        currentTime.setText(getfulltime(currentPosition));
//                        Log.e("time",getfulltime(currentPosition));
                        sleep(800);
                    }
                    if(currentPosition == mediaPlayer.getDuration())
                    {

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        play.setImageResource(R.drawable.play);
                        mediaPlayer.pause();
                    } else {
                        play.setImageResource(R.drawable.pause);
                        mediaPlayer.start();



                    }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }
                seekBar.setProgress(0000);
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);

                fullTime.setText(getfulltime(mediaPlayer.getDuration()));
                currentTime.setText("00 : 00");


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }
                seekBar.setProgress(0000);
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                fullTime.setText(getfulltime(mediaPlayer.getDuration()));
                currentTime.setText("00 : 00");
            }
        });


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentpositon = mediaPlayer.getCurrentPosition();
                int seektime = currentpositon + 10000;
                if(seektime >= mediaPlayer.getDuration())
                {
                    next.performClick();
                    currentTime.setText("00 : 00");
                }
                else
                {
                    seekBar.setProgress(seektime);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    String time = getfulltime(seektime);
                    currentTime.setText(time);
//                Toast.makeText(getApplicationContext(), "forward "+seektime, Toast.LENGTH_SHORT).show();
                }
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "replay clicked", Toast.LENGTH_SHORT).show();
                int currentposition = mediaPlayer.getCurrentPosition();
                if(currentposition > 10000)
                {
                    int seektime = currentposition - 10000;
                    seekBar.setProgress(seektime);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    String time = getfulltime(seektime);
                    currentTime.setText(time);
                }
                else
                {
                    seekBar.setProgress(0000);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    currentTime.setText("00 : 00");
                }
            }
        });

    }

    public String getfulltime(int milliseconds)
    {
        int minutes = (milliseconds / 1000) / 60;
        String m = "",s = "";
        int seconds = (milliseconds / 1000) % 60;
        if(minutes < 10)
        {
             m = "0"+minutes;
        }
        else
        {
            m = ""+minutes;
        }
        if(seconds < 10)
        {
            s = "0"+seconds;
        }
        else
        {
            s = ""+seconds;
        }
        String time = m + " : "+s;

        return time;
    }



}