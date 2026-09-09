package com.meditation.app;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SessionActivity extends AppCompatActivity {

    private static final int[] DURATIONS = {2, 5, 10, 20};

    private TextView tvTime, tvPhase, tvSessionTitle;
    private ImageView breathCircle;
    private Button btnPause, btnFinish;
    private TextView chip2, chip5, chip10, chip20;

    private CountDownTimer timer;
    private ObjectAnimator breathIn, breathOut;
    private boolean running = false;
    private boolean paused = false;
    private int selectedMinutes = 10;
    private long remainingMs = 0;
    private String category = "Расслабление";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        category = getIntent().getStringExtra("category");
        if (category == null) category = "Расслабление";
        selectedMinutes = getIntent().getIntExtra("minutes", 10);

        tvTime = findViewById(R.id.tvTime);
        tvPhase = findViewById(R.id.tvPhase);
        tvSessionTitle = findViewById(R.id.tvSessionTitle);
        breathCircle = findViewById(R.id.breathCircle);
        btnPause = findViewById(R.id.btnPause);
        btnFinish = findViewById(R.id.btnFinish);

        chip2 = findViewById(R.id.chip2);
        chip5 = findViewById(R.id.chip5);
        chip10 = findViewById(R.id.chip10);
        chip20 = findViewById(R.id.chip20);

        tvSessionTitle.setText(category);
        updateChipSelection();
        updateTimeDisplay(selectedMinutes * 60L * 1000);

        // chip clicks
        chip2.setOnClickListener(v -> pickDuration(2));
        chip5.setOnClickListener(v -> pickDuration(5));
        chip10.setOnClickListener(v -> pickDuration(10));
        chip20.setOnClickListener(v -> pickDuration(20));

        // back
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            stopEverything();
            finish();
        });

        btnPause.setOnClickListener(v -> togglePause());
        btnFinish.setOnClickListener(v -> finishSession());

        startSession();
    }

    private void pickDuration(int minutes) {
        if (running) return;
        selectedMinutes = minutes;
        updateChipSelection();
        updateTimeDisplay(selectedMinutes * 60L * 1000);
    }

    private void updateChipSelection() {
        TextView[] chips = {chip2, chip5, chip10, chip20};
        for (int i = 0; i < DURATIONS.length; i++) {
            chips[i].setBackground(ContextCompat.getDrawable(this,
                    DURATIONS[i] == selectedMinutes
                            ? R.drawable.bg_btn_primary
                            : R.drawable.bg_btn_ghost));
        }
    }

    private void startSession() {
        running = true;
        paused = false;
        remainingMs = selectedMinutes * 60L * 1000;
        btnPause.setText(getString(R.string.session_pause));
        startBreath();
        scheduleTimer(remainingMs);
    }

    private void togglePause() {
        if (!running) return;
        if (!paused) {
            paused = true;
            if (timer != null) timer.cancel();
            stopBreath();
            btnPause.setText(getString(R.string.session_resume));
            tvPhase.setText("Пауза");
        } else {
            paused = false;
            btnPause.setText(getString(R.string.session_pause));
            startBreath();
            scheduleTimer(remainingMs);
        }
    }

    private void scheduleTimer(long ms) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(ms, 500) {
            @Override public void onTick(long left) {
                remainingMs = left;
                updateTimeDisplay(left);
            }
            @Override public void onFinish() {
                remainingMs = 0;
                updateTimeDisplay(0);
                finishSession();
            }
        }.start();
    }

    private void finishSession() {
        stopEverything();
        new Prefs(this).addSession(category, selectedMinutes);
        vibrate();
        tvPhase.setText(getString(R.string.session_ready) + " 🌟");
        btnPause.setEnabled(false);
        btnFinish.setText("На главную");
        btnFinish.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    private void vibrate() {
        if (!new Prefs(this).isVibroEnabled()) return;
        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vib != null && vib.hasVibrator()) {
            vib.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void updateTimeDisplay(long ms) {
        long totalSec = ms / 1000;
        long m = totalSec / 60;
        long s = totalSec % 60;
        tvTime.setText(String.format("%02d:%02d", m, s));
    }

    /* ── breathing animation ──────────────────────────────── */

    private void startBreath() {
        stopBreath();
        breathIn = ObjectAnimator.ofFloat(breathCircle, "scaleX", 0.75f, 1.15f);
        ObjectAnimator breathInY = ObjectAnimator.ofFloat(breathCircle, "scaleY", 0.75f, 1.15f);
        breathOut = ObjectAnimator.ofFloat(breathCircle, "scaleX", 1.15f, 0.75f);
        ObjectAnimator breathOutY = ObjectAnimator.ofFloat(breathCircle, "scaleY", 1.15f, 0.75f);

        breathIn.setDuration(4000);
        breathInY.setDuration(4000);
        breathOut.setDuration(6000);
        breathOutY.setDuration(6000);

        breathIn.setRepeatCount(ObjectAnimator.INFINITE);
        breathInY.setRepeatCount(ObjectAnimator.INFINITE);
        breathOut.setRepeatCount(ObjectAnimator.INFINITE);
        breathOutY.setRepeatCount(ObjectAnimator.INFINITE);

        breathIn.addUpdateListener(a -> {
            float frac = a.getAnimatedFraction();
            if (frac < 0.5f) {
                tvPhase.setText(getString(R.string.session_inhale));
            } else {
                tvPhase.setText(getString(R.string.session_exhale));
            }
        });

        breathIn.start();
        breathInY.start();
        breathOut.start();
        breathOutY.start();
    }

    private void stopBreath() {
        if (breathIn != null) breathIn.cancel();
        if (breathOut != null) breathOut.cancel();
    }

    private void stopEverything() {
        running = false;
        if (timer != null) timer.cancel();
        stopBreath();
    }

    @Override
    protected void onDestroy() {
        stopEverything();
        super.onDestroy();
    }
}
