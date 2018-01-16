package com.maginawin.customprogressbardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomProgressBar extends View {

    private final float MAX_VALUE = 100;
    private int w, h;
    private Bitmap bgBitmap;
    private PorterDuffXfermode xfermode;

    private Paint bgPaint, frontPaint;
    private Path bgPath, frontPath;
    private Region barRegion = new Region();

    private int cornerRadius;
    private int barWidth;
    private int barHeight;
    private int barWidth_2;
    private int barHeight_2;
    private float preY;

    private boolean isTouchInside = false;

    private float currentValue = 0;

    private CustomProgressBarListener listener;

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeWidth(1);

        frontPaint = new Paint();
        frontPaint.setColor(Color.WHITE);
        frontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        frontPaint.setAntiAlias(true);
        frontPaint.setStrokeWidth(1);

        bgPath = new Path();
        frontPath = new Path();
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cct_bar_2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.w = w;
        this.h = h;

        barWidth = (int) (w * 0.2f);
        barHeight = (int) (h * 0.6f);
        barWidth_2 = barWidth / 2;
        barHeight_2 = barHeight / 2;
        cornerRadius = barWidth / 3;

        int bgWidth = bgBitmap.getWidth();
        int bgHeight = bgBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(barWidth / (float) bgWidth, barHeight / (float) bgHeight);
        bgBitmap = Bitmap.createBitmap(bgBitmap, 0, 0, bgWidth, bgHeight, matrix, true);

        bgPath.reset();
        bgPath.addRoundRect(-barWidth_2, -barHeight_2, barWidth_2, barHeight_2,
                cornerRadius, cornerRadius, Path.Direction.CW);

        barRegion.setPath(bgPath, new Region(-w, -h, w, h));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setBackgroundColor(Color.LTGRAY);

        canvas.translate(w / 2, h / 2);

        canvas.drawPath(bgPath, bgPaint);

        frontPath.reset();

        float frontTop = barHeight_2 - currentValue * barHeight / MAX_VALUE;
        frontPath.addRect(-barWidth_2, frontTop, barWidth_2, barHeight_2,
                Path.Direction.CW);
        frontPath.op(bgPath, Path.Op.INTERSECT);

        int sc = canvas.saveLayer(-barWidth_2, -barHeight_2, barWidth_2, barHeight_2, frontPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(frontPath, frontPaint);
        frontPaint.setXfermode(xfermode);
        canvas.drawBitmap(bgBitmap, -barWidth_2, -barHeight_2, frontPaint);

        frontPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                preY = event.getY();

                isTouchInside = barRegion.contains((int) (event.getX() - w / 2),
                        (int) (event.getY() - h / 2));

                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouchInside) {
                    break;
                }

                float diffY = preY - event.getY();

                currentValue += diffY * MAX_VALUE / barHeight;
                if (currentValue < 0) {
                    currentValue = 0;
                } else if (currentValue > MAX_VALUE) {
                    currentValue = MAX_VALUE;
                }

                if (listener != null) {
                    listener.didCustomProgressBarValueChanged(this, currentValue);
                }

                preY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        postInvalidate();

        return true;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setListener(CustomProgressBarListener listener) {
        this.listener = listener;
    }
}
