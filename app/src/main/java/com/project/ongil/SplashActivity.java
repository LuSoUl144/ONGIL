package com.project.ongil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/** Short brand splash shown before the role-selection screen. */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1100L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openLogin = () -> {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_ONGIL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(openLogin, SPLASH_DURATION_MS);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(openLogin);
        super.onDestroy();
    }
}
