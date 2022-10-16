package com.tqb.m_expense.Utils;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class ViewUtils {
    public static ViewGroup.LayoutParams setToCenter(ViewGroup layout, ViewGroup.LayoutParams targetParams) {
        if (layout instanceof ConstraintLayout) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(targetParams);
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            return params;
        } else if (layout instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(targetParams);
            params.gravity = Gravity.CENTER;
            return params;
        } else if (layout instanceof RelativeLayout) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(targetParams);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            return params;
        } else {
            throw new RuntimeException("Layout not supported");
        }
    }
}
