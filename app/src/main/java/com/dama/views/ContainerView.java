package com.dama.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dama.customkeyboardbarstv.R;


public class ContainerView extends FrameLayout {
    private KeyView cursor;
    private KeyView highLight;
    private float currX, currY;

    public ContainerView(@NonNull Context context) {
        super(context);
    }

    public ContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initContainerView(KeyView focusedView){
        //create cursor
        Drawable cs = ContextCompat.getDrawable(getContext(), R.drawable.cursor);
        cursor = new KeyView(getContext(), cs, null, "#FBFBFB");
        //set initial position cursor
        initCursorPosition(focusedView);
        //create highLight
        Drawable hl = ContextCompat.getDrawable(getContext(), R.drawable.highlight);
        highLight = new KeyView(getContext(), hl, null, "#FBFBFB");
        //currX = cursor.getX();
        //currY = cursor.getY();
    }

    public Rect rectCoordinate(KeyView keyView){
        Rect offsetViewBounds = new Rect();
        keyView.getDrawingRect(offsetViewBounds);
        offsetDescendantRectToMyCoords(keyView, offsetViewBounds);
        return offsetViewBounds;
    }

    public void setCursorXY(float x, float y){
        currX = x;
        currY = y;
    }

    public void initCursorPosition(KeyView keyView){
        keyView.post(() -> {
            //coordinate keyView
            Rect keyViewCoordinates = rectCoordinate(keyView);
            int x = keyViewCoordinates.left;
            int y = keyViewCoordinates.top;

            //remove cursor
            removeView(cursor);

            //add cursor
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            layoutParams.leftMargin = x; //set x
            layoutParams.topMargin = y; //set y
            cursor.setLayoutParams(layoutParams);
            cursor.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);
            addView(cursor);

            currX = cursor.getX();
            currY = cursor.getY();
        });
    }

    public void setHighLightPosition(KeyView keyView){
        keyView.post(() -> {
            //coordinate keyView
            Rect keyViewCoordinates = rectCoordinate(keyView);
            int x = keyViewCoordinates.left;
            int y = keyViewCoordinates.top;

            //remove cursor
            removeView(highLight);

            //add cursor
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            layoutParams.leftMargin = x; //set x
            layoutParams.topMargin = y; //set y
            highLight.setLayoutParams(layoutParams);
            highLight.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);
            addView(highLight);
        });
    }

    public void removeHighlight(){
        removeView(highLight);
    }

    public void moveCursor(KeyView keyView){
        keyView.post(() -> {
            //keyView coordinate
            Rect keyViewCoordinates = rectCoordinate(keyView);
            int x = keyViewCoordinates.left;
            int y = keyViewCoordinates.top;

            //distances
            float newX = x - cursor.getX();
            float newY = y - cursor.getY();


            //create animation
            TranslateAnimation animation = new TranslateAnimation(currX,newX,currY,newY);
            animation.setFillAfter(true);
            animation.setFillBefore(true);
            animation.setDuration(100);
            //animation.setInterpolator(new AccelerateInterpolator());

            //start animation on view
            cursor.startAnimation(animation);
            cursor.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);

            //update
            this.currX = newX;
            this.currY = newY;
        });
    }

    public synchronized void prova(KeyView keyView){
        keyView.post(() -> {
            //keyView coordinate
            Rect keyViewCoordinates = rectCoordinate(keyView);
            int x = keyViewCoordinates.left;
            int y = keyViewCoordinates.top;

            //distances
            float newX = x - cursor.getX();
            float newY = y - cursor.getY();


            //create animation
            TranslateAnimation animation = new TranslateAnimation(newX,newX,newY,newY);
            animation.setFillAfter(true);
            animation.setFillBefore(true);
            animation.setDuration(100);

            //start animation on view
            cursor.startAnimation(animation);
            cursor.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);

            //update
            this.currX = newX;
            this.currY = newY;
        });
    }
}
