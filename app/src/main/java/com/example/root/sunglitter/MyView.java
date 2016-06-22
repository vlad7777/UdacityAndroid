package com.example.root.sunglitter;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by vlad on 15.03.16.
 */
public class MyView extends View {

    public static final String LOG_TAG = "MyView";

    public MyView(Context context) {
        super(context);
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) context.getSystemService(
                        Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(
                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) context.getSystemService(
                        Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(
                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }


    public MyView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        Log.i(LOG_TAG, "onMeasure()");
        int hSpecMode = MeasureSpec.getMode(hMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(hMeasureSpec);
        int myHeight = hSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY)
            myHeight = hSpecSize;
        else if (hSpecMode == MeasureSpec.AT_MOST)
            myHeight = 300;

        int wSpecMode = MeasureSpec.getMode(wMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(wMeasureSpec);
        int myWidth = wSpecSize;

        if (wSpecMode == MeasureSpec.EXACTLY)
            myWidth = wSpecSize;
        else if (wSpecMode == MeasureSpec.AT_MOST)
            myWidth = 300;

        Log.i(LOG_TAG, "Set measurements to " + myHeight + " " + myWidth);
        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(LOG_TAG, "onDraw()");
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setTextSize(120);
        canvas.drawLine(0, 0, 100, 100, myPaint);
        canvas.drawText("It works!", 210, 210, myPaint);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent ev) {
        ev.getText().add("SW");
        return true;
    }

}
