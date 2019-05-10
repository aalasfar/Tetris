package com.example.assignment3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    BoardView b;
    FrameLayout frm;    ///
    ImageView tetrisBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = new BoardView(this);

        setContentView(R.layout.activity_main);
        //frm = (FrameLayout) findViewById(R.id.frameLayout);///
        //frm.addView(b);
        tetrisBackground = (ImageView) findViewById(R.id.tetrisBackground);

        Button StartGamebtn = (Button)findViewById(R.id.StartGamebtn);
        StartGamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),Tetris.class);
                //how to pass info to second screen
                startActivity(startIntent);
            }
        });
    }
}
