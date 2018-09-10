package com.jharilela.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    int[] tracks = new int[11];
    int soundlist[];
    int currentTrack = 0;
    Button mButton;
    Button arr[];
    private MediaPlayer mediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        arr = new Button[10];
        arr[0] = (Button)findViewById(R.id.button_pin1);
        arr[1] = (Button)findViewById(R.id.button_pin2);
        arr[2] = (Button)findViewById(R.id.button_pin3);
        arr[3] = (Button)findViewById(R.id.button_pin4);
        arr[4] = (Button)findViewById(R.id.button_pin5);
        arr[5] = (Button)findViewById(R.id.button_pin6);
        arr[6] = (Button)findViewById(R.id.button_pin7);
        arr[7] = (Button)findViewById(R.id.button_pin8);
        arr[8] = (Button)findViewById(R.id.button_pin9);
        arr[9] = (Button)findViewById(R.id.button_pin10);
        for(int i=0;i<10;i++){
            arr[i].setVisibility(View.INVISIBLE);
        }
        mButton = (Button)findViewById(R.id.Play);

        tracks[0] = R.raw.pin;
        tracks[1] = R.raw.one;
        tracks[2] = R.raw.two;
        tracks[3] = R.raw.three;
        tracks[4] = R.raw.four;
        tracks[5] = R.raw.five;
        tracks[6] = R.raw.six;
        tracks[7] = R.raw.seven;
        tracks[8] = R.raw.eight;
        tracks[9] = R.raw.nine;
        tracks[10] = R.raw.ten;
        Intent intent = getIntent();
        final String pins = intent.getStringExtra("pins");
        System.out.println("pins "+ pins);
        for(int i=0;i<pins.length();i++){
            if(pins.charAt(i)=='1' && (i+1)<pins.length() && pins.charAt(i+1)=='0'){
                arr[9].setVisibility(View.VISIBLE);
                break;
            }
            arr[pins.charAt(i)-'0'-1].setVisibility(View.VISIBLE);
        }
        mButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                play(pins);
            }
        });

    }
    int length;
    public void play(String str){
        if(mediaPlayer == null){
            soundlist = new int[str.length()+1];
            length = str.length();
            soundlist[0]=tracks[0];
            for(int i = 0; i < length; i++)
            {
                if(str.charAt(i)=='1' && (i+1)<str.length() && str.charAt(i+1)=='0'){
                    soundlist[i+1] = tracks[10];
                    System.out.println((i+1)+" this is a test "+ 10);
                    length--;
                    break;
                }
                System.out.println((i+1)+" this is a test "+ (str.charAt(i)-'0'));
                soundlist[i+1] = tracks[str.charAt(i)-'0'];
            }

            currentTrack = 0;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundlist[currentTrack]);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        }
    }

    public void onCompletion(MediaPlayer arg0) {
        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (currentTrack < length) {
            currentTrack++;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundlist[currentTrack]);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        }
    }
}
