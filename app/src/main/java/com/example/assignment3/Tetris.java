package com.example.assignment3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Tetris extends AppCompatActivity implements View.OnTouchListener {
    TetrisView view;
    Bitmap yellow, blue, red, green, lblue, purple, orange;
    float gameboard[][] = new float[2][];
    int gamevalue[][] = new int[10][16];
    float x = 0;
    float y = 0;
    final int gridW = 108;
    final int gridH = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        gameboard[0] = new float[10];
        gameboard[1] = new float[16];
        view = new TetrisView(this);
        view.setOnTouchListener(this);
        yellow = BitmapFactory.decodeResource(getResources(), R.drawable.yellowblock);
        blue = BitmapFactory.decodeResource(getResources(), R.drawable.blueblock);
        red = BitmapFactory.decodeResource(getResources(), R.drawable.redblock);
        green = BitmapFactory.decodeResource(getResources(), R.drawable.greenblock);
        lblue = BitmapFactory.decodeResource(getResources(), R.drawable.lightblueblock);
        orange = BitmapFactory.decodeResource(getResources(), R.drawable.orangeblock);
        purple = BitmapFactory.decodeResource(getResources(), R.drawable.purpleblock);
        for (int k = 0; k < 10; k++) {
            gameboard[0][k] = gridW * k;

        }
        for (int k = 0; k < 16; k++) {
            gameboard[1][k] = gridH * k;
        }
       // gamevalue[5][5] = 2;
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }


    public class TetrisView extends SurfaceView implements Runnable {
        Thread game = null;
        int m = 0;
        SurfaceHolder holder;
        boolean running = false;
        boolean moving = false;

        public TetrisView(Context context) {
            super(context);
            holder = getHolder();
        }

        @Override
        public void run() {
            while (running == true) {
                //draw shapes
                try {
                    Thread.sleep(1000);
                    move();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                draw();
            }
            //game.start();
        }

        public void draw() {
            boolean n = true;
            while (n) {
                if (!holder.getSurface().isValid()) {
                    continue;
                }
                Canvas grid = holder.lockCanvas();
                grid.drawARGB(255, 150, 150, 150);
                for (int i = 0; i < 10; i++) {
                    for (int q = 0; q < 16; q++) {
                        switch (gamevalue[i][q]) {
                            case 1:
                                grid.drawBitmap(yellow, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 2:
                                grid.drawBitmap(blue, gameboard[0][i], y+ gameboard[1][q], null);
                                break;
                            case 3:
                                grid.drawBitmap(red, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 4:
                                grid.drawBitmap(lblue, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 5:
                                grid.drawBitmap(green, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 6:
                                grid.drawBitmap(purple, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 7:
                                grid.drawBitmap(orange, gameboard[0][i], gameboard[1][q], null);
                                break;

                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                holder.unlockCanvasAndPost(grid);
                n = false;
            }
        }

        public void move() {
            if (m == 0){
             gamevalue[5][5]=2;
             m++;
            }
            else if(m == 1){
                for (int i = 5; i <15 ; i++) {
                    gamevalue[5][i - 1] = 0;
                    gamevalue[5][i] = 2;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    draw();
                }
            }
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    game.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            game = null;
        }

        public void resume() {
            running = true;
            game = new Thread(this);
            game.start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /*try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                break;
        }


        return true;
    }

}

