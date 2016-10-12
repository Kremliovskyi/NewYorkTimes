package com.example.akremlov.nytimes.content;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class FilterView extends CheckBox {

    Paint mPaint = new Paint();
    RectF mRectF = new RectF(5, 5, 10, 10);
    String mText;

    public FilterView(Context context, String text) {
        super(context);
        this.mText = text;
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        canvas.drawText(mText, 100, 65, mPaint);
        canvas.drawArc(mRectF, 100, 200, true, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(50);
        int width = (int) mPaint.measureText(mText) + 150;
        setMeasuredDimension(width, 100);
    }


}