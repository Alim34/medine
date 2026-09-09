package com.meditation.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Prefs prefs = new Prefs(this);

        View btnBack = findViewById(R.id.btnBack);
        TextInputEditText etName = findViewById(R.id.etName);
        SwitchMaterial switchSound = findViewById(R.id.switchSound);
        SwitchMaterial switchVibro = findViewById(R.id.switchVibro);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnReset = findViewById(R.id.btnReset);

        etName.setText(prefs.getName());
        switchSound.setChecked(prefs.isSoundEnabled());
        switchVibro.setChecked(prefs.isVibroEnabled());

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.onb_error), Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.setName(name);
            prefs.setSoundEnabled(switchSound.isChecked());
            prefs.setVibroEnabled(switchVibro.isChecked());
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnReset.setOnClickListener(v -> {
            prefs.resetStats();
            Toast.makeText(this, getString(R.string.settings_reset_done), Toast.LENGTH_SHORT).show();
        });
    }
}
