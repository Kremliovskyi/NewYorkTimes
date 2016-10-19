package com.example.akremlov.nytimes.content;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.SyncStateContract;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.example.akremlov.nytimes.utils.Constants;

public class FilterView extends CheckBox {

    Paint mPaint = new Paint();
    RectF mRectF = new RectF(Constants.RECTF_LEFT_X, Constants.RECTF_TOP_Y, Constants.RECTF_RIGHT_X, Constants.RECTF_BOTTOM_Y);
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
        canvas.drawText(mText, Constants.FILTER_VIEW_TEXT_X, Constants.FILTER_VIEW_TEXT_Y, mPaint);
        canvas.drawArc(mRectF, Constants.FILTER_VIEW_ARC_START_ANGLE, Constants.FILTER_VIEW_ARC_SWEEP_ANGLE, true, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(Constants.FILTER_VIEW_TEXT_SIZE);
        int width = (int) mPaint.measureText(mText) + Constants.FILTER_VIEW_CHECKBOX_WIDTH;
        setMeasuredDimension(width, Constants.FILTER_VIEW_HEIGHT);
    }


}