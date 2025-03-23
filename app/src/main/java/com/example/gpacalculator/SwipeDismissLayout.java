package com.example.gpacalculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

// Create a new class in your project
public class SwipeDismissLayout extends FrameLayout {
    private float startX;
    boolean isSwiping;
    private float dismissThreshold;
    private View contentView;
    private Runnable dismissCallback;
    private ValueAnimator currentAnimator;
    private static final long ANIMATION_DURATION = 200;

    public SwipeDismissLayout(Context context) {
        super(context);
        init();
    }

    public SwipeDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dismissThreshold = getResources().getDisplayMetrics().widthPixels * 0.3f;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    public void setDismissCallback(Runnable callback) {
        this.dismissCallback = callback;
    }

    // Add this crucial method to intercept touch events
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                return false; // Don't intercept yet

            case MotionEvent.ACTION_MOVE:
                // Start intercepting if horizontal movement is significant
                if (Math.abs(event.getX() - startX) > 10) {
                    isSwiping = true;
                    return true; // Intercept the touch event
                }
                return false;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSwiping = false;
                return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - startX;
                contentView.setTranslationX(dx);
                // Adjust background alpha for visual feedback
                float alpha = Math.max(0, 1 - (Math.abs(dx) / dismissThreshold) * 0.3f);
                contentView.setAlpha(alpha);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currentTranslation = contentView.getTranslationX();
                if (Math.abs(currentTranslation) > dismissThreshold) {
                    animateAndDismiss(currentTranslation < 0 ? -getWidth() : getWidth());
                } else {
                    animateBack();
                }
                isSwiping = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    // Update animateBack method to reset alpha
    private void animateBack() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        currentAnimator = ValueAnimator.ofFloat(contentView.getTranslationX(), 0);
        currentAnimator.addUpdateListener(animation -> {
            contentView.setTranslationX((float) animation.getAnimatedValue());
            // Restore alpha gradually
            float progress = 1 - Math.abs((float) animation.getAnimatedValue()) / Math.abs(contentView.getTranslationX());
            contentView.setAlpha(0.7f + 0.3f * progress);
        });
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setAlpha(1.0f);
            }
        });
        currentAnimator.setDuration(ANIMATION_DURATION);
        currentAnimator.start();
    }

    private void animateAndDismiss(float translationX) {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        currentAnimator = ValueAnimator.ofFloat(contentView.getTranslationX(), translationX);
        currentAnimator.addUpdateListener(animation -> contentView.setTranslationX((float) animation.getAnimatedValue()));
        currentAnimator.setDuration(ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (dismissCallback != null) {
                    dismissCallback.run();
                }
            }
        });
        currentAnimator.start();
    }


}