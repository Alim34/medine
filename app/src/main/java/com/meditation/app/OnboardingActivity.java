package com.meditation.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class OnboardingActivity extends AppCompatActivity {

    public static final String PREFS = "medi_prefs";
    public static final String KEY_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If name already set — skip onboarding
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (!prefs.getString(KEY_NAME, "").isEmpty()) {
            startMain();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        TextInputLayout nameInputLayout = findViewById(R.id.nameInputLayout);
        TextInputEditText nameInput = findViewById(R.id.nameInput);
        Button btnContinue = findViewById(R.id.btnContinue);

        nameInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveName(prefs, nameInput, nameInputLayout);
                return true;
            }
            return false;
        });

        btnContinue.setOnClickListener(v -> saveName(prefs, nameInput, nameInputLayout));
    }

    private void saveName(SharedPreferences prefs, TextInputEditText input, TextInputLayout layout) {
        String name = input.getText() != null ? input.getText().toString().trim() : "";
        if (name.isEmpty()) {
            layout.setError(getString(R.string.onb_error));
            return;
        }
        layout.setError(null);
        prefs.edit().putString(KEY_NAME, name).apply();
        startMain();
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
