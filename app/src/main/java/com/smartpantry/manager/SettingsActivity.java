package com.smartpantry.manager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.smartpantry.manager.database.SettingsRepository;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchExpiryAlerts;
    private SettingsRepository settingsRepository;

    private boolean loadingPreference = true;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        granted -> {
                            if (granted) {
                                Toast.makeText(
                                        this,
                                        "Notifications permitted",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                // Save the disabled state and cancel reminders.
                                settingsRepository.setExpiryAlertsEnabled(false);
                                ExpiryReminderScheduler.cancel(this);

                                loadingPreference = true;
                                switchExpiryAlerts.setChecked(false);
                                loadingPreference = false;

                                Toast.makeText(
                                        this,
                                        "Notification permission denied. Expiry alerts have been turned off.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

        settingsRepository = new SettingsRepository(
                getApplicationContext()
        );

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        Button btnBackToHome = findViewById(R.id.btnBackToHome);

        boolean alertsEnabled =
                settingsRepository.areExpiryAlertsEnabled();

        switchExpiryAlerts.setChecked(alertsEnabled);
        loadingPreference = false;

        // Synchronise scheduled work with the saved setting.
        if (alertsEnabled) {
            ExpiryReminderScheduler.schedule(this);
            requestNotificationPermissionIfNeeded();
        } else {
            ExpiryReminderScheduler.cancel(this);
        }

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (loadingPreference) {
                        return;
                    }

                    if (isChecked) {
                        // Ask permission before enabling reminders.
                        if (Build.VERSION.SDK_INT >= 33
                                && ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED) {

                            notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                            );
                            return;
                        }

                        settingsRepository.setExpiryAlertsEnabled(true);
                        ExpiryReminderScheduler.schedule(this);

                        Toast.makeText(
                                this,
                                "Expiry alerts enabled",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {
                        settingsRepository.setExpiryAlertsEnabled(false);
                        ExpiryReminderScheduler.cancel(this);

                        Toast.makeText(
                                this,
                                "Expiry alerts disabled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(
                    SettingsActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (settingsRepository != null) {
            settingsRepository.close();
        }

        super.onDestroy();
    }
}