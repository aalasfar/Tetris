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
import android.widget.Toast;

public class Tetris extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private final GestureDetector gestureDetector = new GestureDetector(this);
    TetrisView view;
    static final int sizeX = 10;
    static final int sizeY = 16;
    Bitmap yellow, blue, red, green, lblue, purple, orange;
    float gameboard[][] = new float[2][];
    int gamevalue[][] = new int[sizeX][sizeY];
    int x = 0;
    int y = 0;
    final int gridW = 108;
    final int gridH = 99;
    public static final int SWIPE = 200;
    public static final int SWIPE_VELOCITY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        gameboard[0] = new float[10];
        gameboard[1] = new float[16];
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
        boolean moveRight = false;
        boolean moveLeft = false;

        public TetrisView(Context context) {
            super(context);
            holder = getHolder();
        }

        @Override
        public void run() {
            /*for(int j = 2; j < 9; j++) {
                Tetrominos(1, j, 14);
            }
            Tetrominos(1,2, 12);*/
            gamevalue[0][15] = 6;
            gamevalue[1][15] = 6;

            while (running == true) {
                //draw shapes
                try {
                    Thread.sleep(50);
                    moving = true;
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
                holder.unlockCanvasAndPost(grid);
                n = false;
            }
        }
int t = 2;
        public void move() {
                //gamevalue[0][0]=6;
            //start at center
            x=4;
               Tetrominos(t,x,y);
                /**deleting previous block**/

                for (int i = 0; i <sizeY ; i++) {
                    if (i == 0){
                        //gamevalue[0][i]= 6;
                        Tetrominos(t,x,i+y);
                    }
                    else {
                        //gamevalue[0][i - 1] = 0;
                        deleteblock(t,x,y+i-1);
                        //gamevalue[0][i] = 6;
                        Tetrominos(t,x,i);
                    }
                    if (moveRight){
                        deleteblock(t,x,i+y);
                        x++;
                        //if(x >= 9){
                        //x=8;}
                        x = CheckXRight(t, x, y+i);
                        Tetrominos(t,x,y+i );
                        moveRight = false;

                    }
                    if (moveLeft){
                        deleteblock(t,x,y+i);
                        x--;
                        //if (x <= 0){
                        //x=0;}
                        x = CheckXLeft(t, x, y+i);
                        Tetrominos(t,x,y+i);
                        moveLeft = false;
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    draw();
                    int check = CheckY(t, x, y+i);
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
        public void Tetrominos(int t, int coorX, int coorY){
            switch(t) {
                //O block
                case 1:
                    for(int i=0; i < 2; i++){
                        for(int j=0; j < 2; j++){
                            gamevalue[i+coorX][j+coorY] = t;
                        }
                    }break;
                case 2: // I block
                    for(int i=0; i<4; i++){
                        gamevalue[i+coorX][coorY] = t;
                    }break;
            }
        }
        /****************************************/
        public void deleteblock(int t, int coorX, int coorY){

            switch(t) {
                //O block
                case 1:
                    for(int i=0; i < 2; i++){
                        for(int j=0; j < 2; j++){
                            gamevalue[i+coorX][j+coorY] = 0;
                        }
                    }break;
                case 2:     //I block
                    for(int i=0; i<4; i++){
                        gamevalue[i+coorX][coorY]=0;
                    }
            }
        }
        /******************************************/
        public int CheckY(int t, int i, int j){
            switch (t) {
                case 1:
                    //if(j == 16 ){
                      //  return 1;
                    //}
                    if (moving){
                        if(j+1<=14){
                            if(gamevalue[i][j+2] > 1 || gamevalue[i+1][j+2] > 1){
                                return 0;
                            }
                            moving = false;
                            int p = CheckY(t,i,j);
                            if (p == 1){
                                moving = true;
                            }
                            else if (p == 0 ) {
                                return 0;
                            }
                            else {return 1;}
                        }
                    }
                    if(j+2 <= 15) {
                        if (gamevalue[i][j+2] > 0 || gamevalue[i+1][j+2] > 0){
                        return 0;
                        }
                    else{ return 1; }
            }
                case 2:
                    if(j+1 <= 15){
                        if(gamevalue[i][j+1]>0 || gamevalue[i+1][j+1]>0 || gamevalue[i+2][j+1]>0 || gamevalue[i+3][j+1]>0){
                            return 0; }
                        else{   return 1;   }
                    }
        }
        return 1;
    }
    /***************************************************/
        public int CheckXRight(int t, int i, int j) {
            switch (t) {
                case 1:
                    //check right is empty
                    if (i + 2 <= sizeX - 2) {
                        if (gamevalue[i + 2][j] > 0 || gamevalue[i + 2][j + 1] > 0 || gamevalue[i+2][j+3] > 0) {
                            return i-1;
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
            }return i;
        }

            public int CheckXLeft(int t, int i, int j){
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
                }return i;
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
            CheckRow();
        }

        public void MoveRight(){
            moveRight = true;

        }
        public void MoveLeft(){
            moveLeft = true;
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
    public void onLongPress(MotionEvent e) {

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
        Toast.makeText(this, "Swipe Right", Toast.LENGTH_LONG).show();
        view.MoveRight();
    }
    private void onSwipeLeft() {
        Toast.makeText(this, "Swipe Left", Toast.LENGTH_LONG).show();
        view.MoveLeft();
    }
    private void onSwipeBottom() {
        Toast.makeText(this, "Swipe Bottom", Toast.LENGTH_LONG).show();
    }
    private void onSwipeTop() {
        Toast.makeText(this, "Swipe Top", Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}

