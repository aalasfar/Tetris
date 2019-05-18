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
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
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
                    int type = 2;
                    move(type);
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
        public void move(int type) {
                //gamevalue[0][0]=6;
            //start at center
            x=4;
            int rotate = 3;
               Tetrominos(type,x,y,rotate);
                /**deleting previous block**/

                for (int i = 0; i <sizeY ; i++) {
                    if (i == 0){
                        //gamevalue[0][i]= 6;
                        Tetrominos(type,x,i+y, rotate);
                    }
                    else {
                        //gamevalue[0][i - 1] = 0;
                        deleteblock(type,x,y+i-1,rotate);
                        //gamevalue[0][i] = 6;
                        Tetrominos(type,x,i, rotate);
                    }
                    if (moveRight){
                        deleteblock(type,x,i+y, rotate);
                        x++;
                        //if(x >= 9){
                        //x=8;}
                        x = CheckXRight(type, x, y+i,rotate);
                        Tetrominos(type,x,y+i,rotate);
                        moveRight = false;

                    }
                    if (moveLeft){
                        deleteblock(type,x,y+i,rotate);
                        x--;
                        //if (x <= 0){
                        //x=0;}
                        x = CheckXLeft(type, x, y+i, rotate);
                        Tetrominos(type,x,y+i,rotate);
                        moveLeft = false;
                    }
                    try {
                        Thread.sleep(400);
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
                        gamevalue[coorX][coorY + 1] = t;
                        gamevalue[coorX][coorY + 2] = t;
                        gamevalue[coorX + 1][coorY + 1] = t;
                        gamevalue[coorX + 2][coorY + 1] = t;
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
            }
        }
        /****************************************/
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
                case 3:
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
                        gamevalue[coorX][coorY + 1] = 0;
                        gamevalue[coorX][coorY + 2] = 0;
                        gamevalue[coorX + 1][coorY + 1] = 0;
                        gamevalue[coorX + 2][coorY + 1] = 0;
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
            }
        }
        /******************************************/
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
                    }
        }
        return 1;
    }
    /***************************************************/
        public int CheckXRight(int t, int i, int j, int rotate) {
            switch (t) {
                case 1:
                    //check right is empty
                    if (i + 2 <= sizeX - 2) {
                        if (gamevalue[i + 2][j] > 0 || gamevalue[i + 2][j + 1] > 0 || gamevalue[i+2][j+3] > 0) {
                            return i-1;
                        } else { return i; }
                    }//check if within right boundary
                    else {
                        if (i >= sizeX - 1) {
                            return sizeX - 2;
                        } else { return i; }
                    }
                case 2:

            }return i;

        }

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

