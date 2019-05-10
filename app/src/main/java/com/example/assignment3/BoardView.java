import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.example.assignment3.R;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    Bitmap mybitmap= BitmapFactory.decodeResource (getResources ( ) , R.drawable . green_block) ;
    float x;
    float y;


    //  Canvas c;
    public BoardView(Context context) {
        super(context) ;
        x=0;
        y=0;

        getHolder().addCallback(this) ;
        //  holder = getHolder();
        setFocusable(true) ; // Very i m p o r t a n t
        setWillNotDraw(false);
        //  c = getHolder().lockCanvas();

    }

    public BoardView(Context context, Bitmap mybitmap, float x, float y) {
        super(context);
        this.mybitmap = mybitmap;
        this.x = x;
        this.y = y;
    }

    public BoardView(Context context, AttributeSet attrs, Bitmap mybitmap, float x, float y) {
        super(context, attrs);
        this.mybitmap = mybitmap;
        this.x = x;
        this.y = y;
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, Bitmap mybitmap, float x, float y) {
        super(context, attrs, defStyleAttr);
        this.mybitmap = mybitmap;
        this.x = x;
        this.y = y;
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Bitmap mybitmap, float x, float y) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mybitmap = mybitmap;
        this.x = x;
        this.y = y;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //   Canvas c = holder.lockCanvas();
        //    this.onDraw(c);
        //    holder.unlockCanvasAndPost(c);
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //  getHolder().unlockCanvasAndPost(c);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        x = e.getX();
        y = e.getY();
        invalidate();
        //   Canvas c = getHolder().lockCanvas();
        //Paint paint = new Paint();
        // paint.setColor(Color.BLUE);
        //  c.drawRect(x,y,x+100,y+100,paint);
        //    this.onDraw(c);
        //  getHolder().unlockCanvasAndPost(c);
        return true;
    }
    @Override
    protected void onDraw (Canvas c ){
        super.onDraw(c);
        c.drawColor(Color.RED);
        Rect dst =new Rect ( ) ;
        dst.set(500,1500,1000,2000);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        //    c.drawRect(10,10,110,110,paint);
        c.drawRect(x,y,x+100,y+100,paint);
        c.drawBitmap(mybitmap,null,dst,null);
    }
}