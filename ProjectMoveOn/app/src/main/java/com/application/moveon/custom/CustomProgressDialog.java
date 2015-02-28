package com.application.moveon.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.application.moveon.R;

/**
 * Created by Hugo on 28/02/2015.
 */
public class CustomProgressDialog extends ProgressDialog {

    AnimationDrawable animation;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressdialog_layout);

        ImageView la = (ImageView) findViewById(R.id.animation);
        la.setBackgroundResource(R.drawable.animation);
        animation = (AnimationDrawable) la.getBackground();

    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
        for (int i = 0; i < animation.getNumberOfFrames(); ++i){
            Drawable frame = animation.getFrame(i);
            if (frame instanceof BitmapDrawable) {
                ((BitmapDrawable)frame).getBitmap().recycle();
            }
            frame.setCallback(null);
        }
        animation.setCallback(null);
    }


}
