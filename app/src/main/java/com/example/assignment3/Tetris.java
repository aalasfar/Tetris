package com.example.assignment3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Tetris extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private final GestureDetector gestureDetector = new GestureDetector(this);
    TetrisView view;
    static final int sizeX = 10; //size in X
    static final int sizeY = 16; //size in Y
    Bitmap yellow, blue, red, green, lblue, purple, orange;
    float gameboard[][] = new float[2][];
    int gamevalue[][] = new int[sizeX][sizeY]; //array that stores the values for each bitmap
    int x = 0;
    int y = 0;
    final int gridW = 108; //width of each grid
    final int gridH = 99; // height of each grid
    public static final int SWIPE = 100;
    public static final int SWIPE_VELOCITY = 100;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        gameboard[0] = new float[10];   // for converting the pixels to blocks
        gameboard[1] = new float[16];  // for converting the pixels to blocks
        view = new TetrisView(this);
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
        setContentView(view);
        //TextView TheScore = (TextView) findViewById(R.id.ScoreNum);
       //TheScore.setText("score");
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
        int gamespeed = 1000;
        int rot = 0;
        SurfaceHolder holder;
        boolean falling = false;
        boolean rotating = false;
        boolean running = false;
        boolean moving = false;
        boolean moveRight = false;
        boolean moveLeft = false;
        int rotate = 0;

        public TetrisView(Context context) {
            super(context);
            holder = getHolder();
        }

        @Override
        public void run() {
            while (running == true) {
                //draw shapes
                try {
                    Thread.sleep(50);
                    Random rand = new Random();
                    int type = rand.nextInt((7-1)+1) +1 ;
                    moving = true;
                    move(type);
                    score = score + 10;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                draw();
                running = GameOver();

            }
        }

        /** Method for drawing the bitmaps**/
        //There are 7 bitmaps that are used for drawing the Tetrominos
        public void draw() {
            boolean n = true;
            while (n) {
                if (!holder.getSurface().isValid()) {
                    continue;
                }
                Canvas grid = holder.lockCanvas();
                grid.drawARGB(255, 0, 0, 0);
                for (int i = 0; i < 10; i++) {
                    for (int q = 0; q < 16; q++) {
                        switch (gamevalue[i][q]) {
                            case 1: // O Block
                                grid.drawBitmap(yellow, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 2: // I Block
                                grid.drawBitmap(lblue, gameboard[0][i], y+ gameboard[1][q], null);
                                break;
                            case 3: // S block
                                grid.drawBitmap(green, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 4: // Z block
                                grid.drawBitmap(red, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 5: // L block
                                grid.drawBitmap(blue, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 6: // Mirror L block
                                grid.drawBitmap(orange, gameboard[0][i], gameboard[1][q], null);
                                break;
                            case 7: // T Block
                                grid.drawBitmap(purple, gameboard[0][i], gameboard[1][q], null);
                                break;

                        }
                    }
                }
                holder.unlockCanvasAndPost(grid);
                n = false;
            }
        }
        /************** Method for calling everything *************/
        // in move, most methods are called. Here we draw the Tetrominos, move it, delete, and check for horizontal line of full blocks
        public void move(int type) {
            //start at center
            x=4;
               Tetrominos(type,x,y,rotate);
                /**deleting previous block**/

                for (int i = 0; i <sizeY ; i++) {
                    if (i == 0){
                        Tetrominos(type,x,i+y, rotate);
                    }
                    else {
                        deleteblock(type,x,y+i-1,rotate);
                        Tetrominos(type,x,i, rotate);
                    }
                    if (falling){
                        for ( int q = i ; q <sizeY; q++){
                            if (q == 0){
                                Tetrominos(type,x,q+y, rotate);
                            }
                            else {
                                deleteblock(type,x,y+q-1,rotate);
                                Tetrominos(type,x,q, rotate);
                            }
                            falling = false;
                            int check = CheckY(type, x, y+q,rotate);
                            if(check == 0){ break; }
                        }
                        draw();
                        break;
                    }
                    if (rotating){
                        while(rot < 3) {
                            if (type == 1) {
                                rotating = false;
                            } else {
                                deleteblock(type, x, i + y, rotate);
                                if (rotate == 3) {
                                    rotate = 0;
                                } else {
                                    rotate++;
                                }
                                rotate = CheckRotate(type, x, y + i, rotate);
                                Tetrominos(type, x, y + i, rotate);
                                rotating = false;
                            }
                            try {
                                Thread.sleep(50);
                                rot++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        rot =0 ;
                    }
                    if (moveRight){
                        deleteblock(type,x,i+y, rotate);
                        x++;
                        x = CheckXRight(type, x, y+i,rotate);
                        Tetrominos(type,x,y+i,rotate);
                        moveRight = false;

                    }
                    if (moveLeft){
                        deleteblock(type,x,y+i,rotate);
                        x--;
                        x = CheckXLeft(type, x, y+i, rotate);
                        Tetrominos(type,x,y+i,rotate);
                        moveLeft = false;
                    }
                    try {
                        Thread.sleep(gamespeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    draw();
                    int check = CheckY(type, x, y+i,rotate);
                    if(check == 0){ break; }
                }
            CheckRow();

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
        /*********************************************/
        /**Method for drawing Tetrominos**/
        public void Tetrominos(int t, int coorX, int coorY, int rotate){
            switch(t) {
                //O block
                case 1:
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            gamevalue[i + coorX][j + coorY] = t;
                        }
                    }
                    break;
                case 2: // I block
                    if (rotate == 0 || rotate == 2) {
                        for (int i = 0; i < 4; i++) {
                            gamevalue[i + coorX][coorY] = t;
                        }
                        break;
                    } else if (rotate == 1 || rotate == 3) {
                        for (int i = 0; i < 4; i++) {
                            gamevalue[coorX][i + coorY] = t;
                        }
                        break;
                    }
                case 3: // S block
                    if (rotate == 0 || rotate == 2) {
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 2][coorY] = t;
                        break;
                    } else if(rotate == 1 || rotate == 3){
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 2] = t;
                        break;
                    }
                case 4: // Z block
                    if (rotate == 0 || rotate == 2) {
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY + 1] = t;
                        break;
                    } else if(rotate == 1 || rotate == 3){
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX][coorY + 2] = t;
                        break;
                    }
                case 5: // L block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX][coorY + 2] = t;
                        gamevalue[coorX + 1][coorY + 2] = t;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 2][coorY] = t;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 2] = t;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY] = t;
                        break;
                    }
                case 6: // L mirror block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY+2] = t;
                        gamevalue[coorX+1][coorY] = t;
                        gamevalue[coorX+1][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY + 2] = t;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX + 1][coorY+1] = t;
                        gamevalue[coorX + 2][coorY+1] = t;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX+1][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX][coorY + 2] = t;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX + 1][coorY] = t;
                        gamevalue[coorX + 2][coorY] = t;
                        gamevalue[coorX + 2][coorY+1] = t;
                        break;
                    }
                case 7: // T block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX+1][coorY] = t;
                        gamevalue[coorX+1][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY] = t;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX+1][coorY] = t;
                        gamevalue[coorX+1][coorY + 1] = t;
                        gamevalue[coorX][coorY+1] = t;
                        gamevalue[coorX + 1][coorY+2] = t;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX+1][coorY] = t;
                        gamevalue[coorX][coorY+1] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY + 1] = t;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY] = t;
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX][coorY + 2] = t;
                        gamevalue[coorX + 1][coorY+1] = t;
                        break;
                    }
            }
        }
        /****************************************/
        /** Method for deleting blocks after moving **/
        public void deleteblock(int t, int coorX, int coorY, int rotate){

            switch(t) {
                //O block
                case 1:
                    for(int i=0; i < 2; i++){
                        for(int j=0; j < 2; j++){
                            gamevalue[i+coorX][j+coorY] = 0;
                        }
                    }break;
                case 2:     //I block
                    if ( rotate == 0 || rotate == 2) {
                        for (int i = 0; i < 4; i++) {
                            gamevalue[i + coorX][coorY] = 0;
                        }
                    }
                    else if ( rotate == 1 || rotate == 3) {
                        for (int i = 0; i < 4; i++) {
                            gamevalue[coorX][i + coorY] = 0;
                        }
                    }
                    break;
                case 3:  // S block
                    if (rotate == 0 || rotate == 2) {
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 2][coorY] = 0;
                        break;
                    } else if(rotate == 1 || rotate == 3){
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 2] = 0;
                        break;
                    }
                case 4: // Z block
                    if (rotate == 0 || rotate == 2) {
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY + 1] = 0;
                        break;
                    } else if(rotate == 1 || rotate == 3){
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 2] = 0;
                        break;
                    }
                case 5: // L block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 2] = 0;
                        gamevalue[coorX + 1][coorY + 2] = 0;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 2][coorY] = 0;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 2] = 0;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY] = 0;
                        break;
                    }
                case 6: // L mirror block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY+2] = 0;
                        gamevalue[coorX+1][coorY] = 0;
                        gamevalue[coorX+1][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY + 2] = 0;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX + 1][coorY+1] = 0;
                        gamevalue[coorX + 2][coorY+1] = 0;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX+1][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 2] = 0;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX + 1][coorY] = 0;
                        gamevalue[coorX + 2][coorY] = 0;
                        gamevalue[coorX + 2][coorY+1] = 0;
                        break;
                    }
                case 7: // T block
                    if (rotate == 0) {
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX+1][coorY] = 0;
                        gamevalue[coorX+1][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY] = 0;
                        break;
                    } else if(rotate == 1){
                        gamevalue[coorX+1][coorY] = 0;
                        gamevalue[coorX+1][coorY + 1] = 0;
                        gamevalue[coorX][coorY+1] = 0;
                        gamevalue[coorX + 1][coorY+2] = 0;
                        break;
                    } else if (rotate == 2) {
                        gamevalue[coorX+1][coorY] = 0;
                        gamevalue[coorX][coorY+1] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY + 1] = 0;
                        break;
                    } else if(rotate == 3){
                        gamevalue[coorX][coorY] = 0;
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 2] = 0;
                        gamevalue[coorX + 1][coorY+1] = 0;
                        break;
                    }
            }
        }
        /******************************************/
        /** Method for checking Vertically for stacking**/
        public int CheckY(int t, int i, int j, int rotate){
            switch (t) {
                case 1:
                    if(j == sizeY-2 ){
                        return 0;
                    }
                    if (moving){
                        if(j+1<=14){
                            if(gamevalue[i][j+2] == t || gamevalue[i+1][j+2] == t){
                                return 0;
                            }
                            moving = false;
                            int p = CheckY(t,i,j,rotate);
                            if (p == 1){
                                moving = true;
                            }
                            else if (p == 0 ) {
                                return 0;
                            }
                            else {return 1;}
                        }
                    }
                    if(j+2 <= sizeY-1) {
                        if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+2] > 0){
                        return 0;
                        }
                    else{ return 1; }
            }
                case 2:
                    if ( rotate == 0 || rotate == 2) {
                        if (j + 1 <= sizeY-1) {
                            if (gamevalue[i][j + 1] > 0 || gamevalue[i + 1][j + 1] > 0 || gamevalue[i + 2][j + 1] > 0 || gamevalue[i + 3][j + 1] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                    else if ( rotate == 1 || rotate == 3) {
                        if ( j == sizeY - 4){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+4] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j + 4] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }break;
                case 3:
                    if ( rotate == 0 || rotate == 2) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=14){
                                if(gamevalue[i+1][j+2] == t ||gamevalue[i][j+2]==t || gamevalue[i+2][j+1]==t ){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i+1][j+2] > 0|| gamevalue[i][j+2]>0 || gamevalue[i+2][j+1]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if ( rotate == 1 || rotate == 3) {
                    if ( j == sizeY - 3){
                        return 0;
                    }
                    if (moving){
                        if(j+3<=12){
                            if(gamevalue[i][j+2] == t || gamevalue[i+1][j+3] == t){
                                return 0;
                            }
                            moving = false;
                            int p = CheckY(t,i,j,rotate);
                            if (p == 1){
                                moving = true;
                            }
                            else if (p == 0 ) {
                                return 0;
                            }
                            else {return 1;}
                        }
                    }
                    if (j <= 15) {
                        if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+3] > 0) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }break;
                case 4:
                    if ( rotate == 0 || rotate == 2) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=14){
                                if(gamevalue[i][j+1] == t ||gamevalue[i+1][j+2]==t || gamevalue[i+2][j+2]==t ){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+1] > 0 ||gamevalue[i+1][j+2]>0 || gamevalue[i+2][j+2]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if ( rotate == 1 || rotate == 3) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+3] == t || gamevalue[i+1][j+2] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+3] > 0 || gamevalue[i+1][j+2] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }break;
                case 5:
                    if ( rotate == 0) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+3] == t ||gamevalue[i+1][j+3]==t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+3] > 0 ||gamevalue[i+1][j+3]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if ( rotate == 1) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+2] == t || gamevalue[i+1][j+1] == t|| gamevalue[i+2][j+1] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+1] > 0 || gamevalue[i+2][j+1]>1) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                    if (rotate == 2) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+1] == t ||gamevalue[i+1][j+3]==t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+1] > 0 ||gamevalue[i+1][j+3]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if (rotate == 3) {
                        if ( j == sizeY - 2 || i == sizeX - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+2] == t || gamevalue[i+1][j+2] == t|| gamevalue[i+2][j+2] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+2] > 0|| gamevalue[i+2][j+2] >0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }break;
                case 6:
                    if ( rotate == 0) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+3] == t ||gamevalue[i+1][j+3]==t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+3] > 0 ||gamevalue[i+1][j+3]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if ( rotate == 1) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+2] == t || gamevalue[i+1][j+2] == t|| gamevalue[i+2][j+2] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+2] > 0 || gamevalue[i+2][j+2]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                    if (rotate == 2) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i+1][j+1] == t ||gamevalue[i][j+3]==t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i+1][j+1] > 0 ||gamevalue[i][j+3]>0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if (rotate == 3) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+1] == t || gamevalue[i+1][j+1] == t|| gamevalue[i+2][j+2] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+1] > 0 || gamevalue[i+1][j+1] > 0|| gamevalue[i+2][j+2] >0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }break;
                case 7:
                    if ( rotate == 0) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+1] == t ||gamevalue[i+1][j+2] == t || gamevalue[i+2][j+1] == t ){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+1] > 0 ||gamevalue[i+1][j+2]>0 || gamevalue[i+2][j+1] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if ( rotate == 1) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+2] == t || gamevalue[i+1][j+3] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+3] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                    if (rotate == 2) {
                        if ( j == sizeY - 2){
                            return 0;
                        }
                        if (moving){
                            if(j+2<=13){
                                if(gamevalue[i][j+2] == t ||gamevalue[i+1][j+2]==t || gamevalue[i+2][j+2] == t ){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+2] > 0 ||gamevalue[i+1][j+2]>0 || gamevalue[i+2][j+2] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                    }
                    if (rotate == 3) {
                        if ( j == sizeY - 3){
                            return 0;
                        }
                        if (moving){
                            if(j+3<=12){
                                if(gamevalue[i][j+3] == t || gamevalue[i+1][j+2] == t){
                                    return 0;
                                }
                                moving = false;
                                int p = CheckY(t,i,j,rotate);
                                if (p == 1){
                                    moving = true;
                                }
                                else if (p == 0 ) {
                                    return 0;
                                }
                                else {return 1;}
                            }
                        }
                        if (j <= 15) {
                            if (gamevalue[i][j+3] > 0 || gamevalue[i+1][j+2] > 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }break;
            }

                        return 1;
    }
    /***************************************************/
    /** Method for checking right**/
    public int CheckXRight(int t, int i, int j, int rotate) {
        switch (t) {
            case 1:
                //check right is empty
                if (i + 2 <= sizeX - 2) {
                    if (gamevalue[i + 2][j] > 0 || gamevalue[i + 2][j + 1] > 0 || gamevalue[i + 2][j + 3] > 0) {
                        return i - 1;
                    } else {
                        return i;
                    }
                }//check if within right boundary
                else {
                    if (i >= sizeX - 1) {
                        return sizeX - 2;
                    } else {
                        return i;
                    }
                }
            case 2:
                if (rotate == 0 || rotate == 2) {
                    if (i + 4 <= sizeX - 4) {
                        if (gamevalue[i + 4][j] > 0) {
                            return i - 1;
                        } else {
                            return i;
                        }
                    } else {
                        if (i >= sizeX - 3) {
                            return sizeX - 4;
                        } else { return i; }
                    }
                }else if (rotate == 1 || rotate == 3) {
                    if (i <= sizeX-1) {
                        if (gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i][j+2]>0 || gamevalue[i][j+3]>0 || gamevalue[i][j+4]>0) {
                            return i - 1;
                        } else {
                            return i;
                        }
                    } else {
                        if (i >= sizeX) {
                            return sizeX - 1;
                        } else { return i; }
                    }
                }break;
            case 3:
                if(rotate == 0 || rotate == 2){
                    if(i + 3 <= sizeX -3){
                        if(gamevalue[i+3][j] > 0 || gamevalue[i+2][j+1] > 0){
                            return i - 1;
                        }else{ return i; }
                    }else{
                        if(i >= sizeX - 2){
                            return sizeX - 3;
                        }else{  return i;}
                    }

                }else if(rotate == 1 || rotate == 3) {
                    if (i + 2 <= sizeX - 2) {
                        if (gamevalue[i+2][j+1]>0 || gamevalue[i+2][j+2]>0 || gamevalue[i+2][j+2]>0 || gamevalue[i+3][j+1]>0) {
                            return i - 1;
                        } else {
                            return i;
                        }
                    } else {
                        if (i >= sizeX - 1) {
                            return sizeX - 2;
                        } else { return i; }
                    }
                }
            case 4:
                if(rotate == 0 || rotate == 2){
                    if(i + 3 <= sizeX -3){
                        if(gamevalue[i+2][j] > 0 || gamevalue[i+3][j+1] > 0){
                            return i - 1;
                        }else{ return i; }
                    }else{
                        if(i >= sizeX - 2){
                            return sizeX - 3;
                        }else{  return i;}
                    }

                }else if(rotate == 1 || rotate == 3) {
                    if (i + 2 <= sizeX - 2) {
                        if (gamevalue[i+2][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+2][j+2]>0 || gamevalue[i+1][j+2]>0) {
                            return i - 1;
                        } else {
                            return i;
                        }
                    } else {
                        if (i >= sizeX - 1) {
                            return sizeX - 2;
                        } else { return i; }
                    }
                }
            case 5:
                if(rotate == 0){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i+1][j+2]>0 || gamevalue[i+2][j+3] > 0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }
                }else if(rotate == 1){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+2][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i+1][j+2]>0 || gamevalue[i+2][j+1] > 0 || gamevalue[i+1][j+1]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }

                }else if(rotate == 2){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i+1][j]>0 || gamevalue[i+1][j+1]>0 || gamevalue[i+1][j+2]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }

                }else if(rotate == 3){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+2][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+3][j+1]>0 || gamevalue[i+2][j+1] > 0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }
                }
            case 6:
                if(rotate == 0){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i+2][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+2][j+2]>0 || gamevalue[i+3][j+2] > 0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }
                }else if(rotate == 1){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+1][j]>0 || gamevalue[i+3][j+2]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }

                }else if(rotate == 2){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i+2][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+1][j+1]>0 || gamevalue[i+1][j+1]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }

                }else if(rotate == 3){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+3][j]>0 || gamevalue[i+3][j+1]>0 || gamevalue[i+3][j+2]>0 || gamevalue[i+3][j+2] > 0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }
                }
            case 7:
                if(rotate == 0){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+3][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+3][j+1]>0 || gamevalue[i+2][j+2] > 0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }
                }else if(rotate == 1){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i+2][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+2][j+2]>0 || gamevalue[i+2][j+3]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }
                }else if(rotate == 2){
                    if(i + 3 <= sizeX - 3){
                        if(gamevalue[i+3][j+1]>0 || gamevalue[i+2][j]>0 || gamevalue[i+3][j+2]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 2){ return sizeX - 3; }
                        else{   return i; }
                    }

                }else if(rotate == 3){
                    if(i + 2 <= sizeX - 2){
                        if(gamevalue[i+1][j]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+1][j+3]>0){
                            return i - 1;
                        }else{  return i;}
                    }else{
                        if(i >= sizeX - 1){ return sizeX - 2; }
                        else{   return i; }
                    }
                }
        }
        return i;
    }
/**************************Method for checking left*************************/
        public int CheckXLeft(int t, int i, int j, int rotate){
            switch (t){
                case 1:
                    //check right is empty
                    if (i >= 0) {
                        if (gamevalue[i][j] > 0 || gamevalue[i][j + 1] > 0 || gamevalue[i][j+3] > 0) {
                            return i+1;
                        } else {
                            return i;
                        }
                    }//check if within right boundary
                    else {
                        if (i < 0) {
                            return 0;
                        } else {
                            return i;
                        }
                    }
                case 2:
                    if (rotate == 0 || rotate == 2) {
                        if (i >= 0) {
                            if (gamevalue[i][j] > 0) {
                                return i + 1;
                            } else {
                                return i;
                            }
                        } else {
                            if (i < 0) {
                                return 0;
                            } else { return i; }
                        }
                    }else if (rotate == 1 || rotate == 3) {
                        if (i > 0) {
                            if (gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i][j+2]>0 || gamevalue[i][j+3]>0 || gamevalue[i][j+4] > 0) {
                                return i + 1;
                            } else {
                                return i;
                            }
                        } else {
                            if (i <= 0) {
                                return 0;
                            } else { return i; }
                        }
                    }break;
                case 3:
                    if(rotate == 0 || rotate == 2){
                        if(i > 0){
                            if(gamevalue[i][j] > 0 || gamevalue[i-1][j+1] > 0 || gamevalue[i-1][j+2]>0){
                                return i + 1;
                            }else{ return i; }
                        }else{
                            if(i <= 0){
                                return 0;
                            }else{  return i;}
                        }

                    }else if(rotate == 1 || rotate == 3) {
                        if(i > 0){
                            if(gamevalue[i][j] > 0 || gamevalue[i][j+1] > 0 || gamevalue[i][j+2]>0 || gamevalue[i-1][j+2]>0){
                                return i + 1;
                            }else{ return i; }
                        }else{
                            if(i <= 0){
                                return 0;
                            }else{  return i;}
                        }
                    }
                case 4:
                    if(rotate == 0 || rotate == 2){
                        if(i > 0){
                            if(gamevalue[i][j] > 0 || gamevalue[i+1][j+1] > 0 || gamevalue[i-1][j+1]>0){
                                return i + 1;
                            }else{ return i; }
                        }else{
                            if(i <= 0){
                                return 0;
                            }else{  return i;}
                        }

                    }else if(rotate == 1 || rotate == 3) {
                        if(i > 0){
                            if(gamevalue[i][j+1] > 0 || gamevalue[i][j+2] > 0 || gamevalue[i-1][j+3] > 0 || gamevalue[i-1][j+2]>0){
                                return i + 1;
                            }else{ return i; }
                        }else{
                            if(i <= 0){
                                return 0;
                            }else{  return i;}
                        }
                    }
                case 5:
                    if(rotate == 0){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i][j+2]>0 || gamevalue[i-1][j+2] > 0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 1){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i-1][j+2] > 0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }

                    }else if(rotate == 2){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i+1][j+1]>0 || gamevalue[i+1][j+2]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i][j+2]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 3){
                        if(i > 0){
                            if(gamevalue[i][j+1]>0 || gamevalue[i-1][j+1] > 0 || gamevalue[i][j]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }
                case 6:
                    if(rotate == 0){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i][j+1]>0 || gamevalue[i-1][j+2]>0 || gamevalue[i-1][j+3] > 0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 1){
                        if(i > 0){
                            if(gamevalue[i-1][j]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i-1][j+2] > 0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }

                    }else if(rotate == 2){
                        if(i > 0){
                            if(gamevalue[i-1][j]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i-1][j+2]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 3){
                        if(i > 0){
                            if(gamevalue[i-1][j]>0 || gamevalue[i-1][j+1] > 0 || gamevalue[i+1][j+2]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }
                case 7:
                    if(rotate == 0){
                        if(i > 0){
                            if(gamevalue[i-1][j]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i][j+2]>0 || gamevalue[i][j+3] > 0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 1){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i-1][j+2]>0 || gamevalue[i][j+3]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }

                    }else if(rotate == 2){
                        if(i > 0){
                            if(gamevalue[i][j]>0 || gamevalue[i-1][j+1]>0 || gamevalue[i-1][j+2]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }else if(rotate == 3){
                        if(i > 0){
                            if(gamevalue[i-1][j]>0 || gamevalue[i-1][j+1] > 0 || gamevalue[i-1][j+2]>0 || gamevalue[i-1][j+3]>0){
                                return i + 1;
                            }else{  return i;}
                        }else{
                            if(i <= 0){ return 0; }
                            else{   return i; }
                        }
                    }
            }
            return i;
        }
/********************Method for rotating *****************/
        public int CheckRotate(int t, int i, int j, int rotate ){
            int left;
            int right;
            int down;
            right = CheckXLeft(t,i-1,j,rotate);
            left = CheckXRight(t,i+1,j,rotate);
            down = CheckY(t,i,j,rotate);
            if (right == i || left == i || down == 0){
                if (rotate == 0 ){
                    rotate = 3;
                }
                else {
                    rotate--;
                }
            }

            return rotate;
        }

        public void CheckRow(){
            boolean noGap;
            for(int j=15; j > 0; j--) {
                noGap = true;
                //for (int i = 0; i < 10; i++) {
                    if (gamevalue[0][j]!=0 && gamevalue[1][j]!=0 &&gamevalue[2][j]!=0 &&gamevalue[3][j]!=0&&gamevalue[4][j]!=0
                    &&gamevalue[5][j]!=0 &&gamevalue[6][j]!=0 &&gamevalue[7][j]!=0 &&gamevalue[8][j]!=0 && gamevalue[9][j]!=0) {
                  //      noGap = false;
                        DeleteRow(j);
                    }
                    //if (!noGap) {
                    //}
                //}
            }
        }

        public void DeleteRow(int indexY){
            for(int j=indexY; j>0; j--) {
                for (int i = 9; i >=0; i--) {
                    gamevalue[i][j] = gamevalue[i][j-1];
                }
            }
            if( gamespeed >= 100 ) {
                gamespeed = gamespeed - 10;
            }
            CheckRow();
            score = score + 100;
        }

        public void MoveRight(){
            moveRight = true;

        }
        public void MoveLeft(){
            moveLeft = true;
        }
        public void Rotate(){
            rotating = true;
        }
        public boolean GameOver(){
            boolean block = true;
            for (int i = 0; i < 10 ; i++){
                if(gamevalue[i][0] > 0){
                    block = false;
                }
                else {
                    block = true;
                }
            }
            return block;
        }
        public void Down(){
            falling = true;
        }
}
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {
    }
    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();
        // which is greater? X or Y
        if (Math.abs(diffX) > Math.abs(diffY)) {
            //right or left swipe
            if (Math.abs(diffX) > SWIPE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        } else {
            if (Math.abs(diffY) > SWIPE && Math.abs(velocityY) > SWIPE_VELOCITY) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeRight() {
        view.MoveRight();
    }
    private void onSwipeLeft() {
        view.MoveLeft();
    }
    private void onSwipeBottom() {
        view.Down();
    }
    private void onSwipeTop() {
        view.Rotate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}

