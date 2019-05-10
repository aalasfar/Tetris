package com.example.assignment3;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

public class Shape extends View {
    int swidth = 102;
    int sheight= 97;
    public Shape(Context context) {
        super(context);
        init(null);
    }

    public Shape(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public Shape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public Shape(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    public void init (@Nullable AttributeSet set){

    }
    @Override
    protected void onDraw(Canvas c){
        Rect rect = new Rect();
        rect.left =0;
        rect.top=0;
        rect.right=rect.left +swidth;
        rect.bottom=rect.top+sheight;

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        c.drawRect(rect,paint);

    }

    public class Sqaure(){

    }

}