package com.tqb.m_expense.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LoadingScreen<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
    public interface OnTransitionListener<Param, Progress, Result> {
        Result transition(Param...params);

        default void onTransitionUpdate(Progress... progress) {
        }
    }
    public interface OnTransitionCompleteListener<Param, Progress, Result> {
        void onTransitionComplete(Result result);
    }
    private final ProgressBar progressBar;
    private OnTransitionListener<Param, Progress, Result> listener;
    private OnTransitionCompleteListener<Param, Progress, Result> completeListener;
    private final ViewGroup layout;
    public static <Param, Progress, Result> LoadingScreen<Param, Progress, Result>
            beginTransition(Context context) {
        return new LoadingScreen<>(context);
    }
    public LoadingScreen(Context context) {
        this.progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        this.layout = ((AppCompatActivity) context).findViewById(android.R.id.content);
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.MAGENTA));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params = ViewUtils.setToCenter(layout, params);
        layout.addView(progressBar, params);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
        layout.removeView(progressBar);
    }

    public LoadingScreen<Param, Progress, Result> doTask(OnTransitionListener<Param, Progress, Result> listener) {
        this.listener = listener;
        return this;
    }
    public LoadingScreen<Param, Progress, Result> done(OnTransitionCompleteListener<Param, Progress, Result> listener) {
        this.completeListener = listener;
        return this;
    }
    @Override
    protected void onProgressUpdate(Progress... progress) {
        listener.onTransitionUpdate(progress);
    }
    @Override
    protected void onPreExecute() {
        showProgressBar();
        super.onPreExecute();
    }
    @Override
    protected Result doInBackground(Param...params) {
        return listener.transition(params);
    }
    @Override
    protected void onPostExecute(Result result) {
        hideProgressBar();
        completeListener.onTransitionComplete(result);
        super.onPostExecute(result);
    }
}
