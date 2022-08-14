package com.example.movies.activity.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;


import com.example.movies.R;
import com.example.movies.activity.loginorsignup.LoginOrSignUpActivity;
import com.example.movies.activity.main.MainActivity;
import com.example.movies.databinding.ActivitySplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        //binding.motionLayout.startLayoutAnimation();
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        binding.motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                binding.imageViewMain.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.imageViewMain.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_pop_corn_to_top_left_layout));
                    }
                });
                intent = new Intent(SplashScreen.this, LoginOrSignUpActivity.class);
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.brown_light));
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_layout_main_in, R.anim.anim_splash_screen_out);
                    }
                });
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }
}