package com.example.assignment3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Tetris extends AppCompatActivity implements View.OnTouchListener {
    TetrisView view;
    Bitmap square;
    float x = 0;
    float y = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        view = new TetrisView(this);
        view.setOnTouchListener(this);
        square = BitmapFactory.decodeResource(getResources(),R.drawable.yellowblock);
        setContentView(view);
    }

    @Override
    protected void onPause(){
        super.onPause();
        view.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        view.resume();
    }


    public class TetrisView extends SurfaceView implements Runnable{
        //float x = 0;
        //float y = 0;
        //static Bitmap square;
        Thread game = null;
        SurfaceHolder holder;
        boolean running = false;

        public TetrisView(Context context) {
            super(context);
            holder = getHolder();
        }


        @Override
        public void run() {
            while (running == true){
                //draw shapes
                if (!holder.getSurface().isValid()){
                    continue;
                }

                Canvas grid = holder.lockCanvas();
                System.out.println(grid.getHeight());
                System.out.println(grid.getWidth());
                grid.drawARGB(255,150,150,10);
                grid.drawBitmap(square,x,y,null);
                holder.unlockCanvasAndPost(grid);
            }
        }
        public void pause(){
            running = false;
            while(true){
                try{
                    game.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            game = null;
        }

        public void resume(){
            running = true;
            game = new Thread(this);
            game.start();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /*try{
            Thread.sleep(50);
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/
        switch (event.getAction()){
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
