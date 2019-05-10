package com.example.assignment3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Tetris extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        Shape shape = new Shape(this);


    }
}
