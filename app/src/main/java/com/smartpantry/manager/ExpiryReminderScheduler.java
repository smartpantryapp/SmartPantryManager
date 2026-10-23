package com.smartpantry.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class ExpiryReminderScheduler {

    private static final String UNIQUE_WORK_NAME =
            "daily_expiry_reminder";

    private ExpiryReminderScheduler() {
        // Utility class; do not instantiate.
    }

    public static void schedule(@NonNull Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(
                        ExpiryReminderWorker.class,
                        24,
                        TimeUnit.HOURS
                )
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        UNIQUE_WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        reminderRequest
                );
    }

    public static void cancel(@NonNull Context context) {
        WorkManager.getInstance(context.getApplicationContext())
                .cancelUniqueWork(UNIQUE_WORK_NAME);
    }
}