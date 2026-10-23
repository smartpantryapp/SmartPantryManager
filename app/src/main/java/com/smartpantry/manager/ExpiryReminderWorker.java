package com.smartpantry.manager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.smartpantry.manager.database.PantryRepository;
import com.smartpantry.manager.database.SettingsRepository;
import com.smartpantry.manager.models.Ingredient;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpiryReminderWorker extends Worker {

    private static final String PREFS_NAME =
            "expiry_reminder_prefs";

    private static final String LAST_NOTIFICATION_DATE =
            "last_notification_date";

    public ExpiryReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        SettingsRepository settingsRepository = null;
        PantryRepository pantryRepository = null;

        try {
            settingsRepository = new SettingsRepository(context);
            pantryRepository = new PantryRepository(context);

            // Respect the user's saved expiry-alert preference.
            if (!settingsRepository.areExpiryAlertsEnabled()) {
                return Result.success();
            }

            List<Ingredient> ingredients =
                    pantryRepository.getAllIngredients();

            List<String> expiringNames = new ArrayList<>();

            Calendar today = Calendar.getInstance();
            clearTime(today);

            // Include today and the following three days.
            Calendar lastDay = (Calendar) today.clone();
            lastDay.add(Calendar.DAY_OF_YEAR, 3);

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setLenient(false);

            for (Ingredient ingredient : ingredients) {
                String expiryDate = ingredient.getExpiryDate();

                if (expiryDate == null
                        || expiryDate.trim().isEmpty()) {
                    continue;
                }

                ParsePosition position = new ParsePosition(0);
                Date parsedDate =
                        dateFormat.parse(expiryDate, position);

                // Ignore missing, malformed, or incomplete dates.
                if (parsedDate == null
                        || position.getIndex() != expiryDate.length()) {
                    continue;
                }

                Calendar expiry = Calendar.getInstance();
                expiry.setTime(parsedDate);
                clearTime(expiry);

                if (!expiry.before(today)
                        && !expiry.after(lastDay)) {
                    expiringNames.add(ingredient.getName());
                }
            }

            // Nothing needs a reminder today.
            if (expiringNames.isEmpty()) {
                return Result.success();
            }

            String todayKey = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.US
            ).format(new Date());

            SharedPreferences preferences =
                    context.getSharedPreferences(
                            PREFS_NAME,
                            Context.MODE_PRIVATE
                    );

            // Prevent duplicate reminders on the same calendar day.
            String lastNotificationDate =
                    preferences.getString(
                            LAST_NOTIFICATION_DATE,
                            ""
                    );

            if (todayKey.equals(lastNotificationDate)) {
                return Result.success();
            }

            String message;

            if (expiringNames.size() == 1) {
                message = expiringNames.get(0)
                        + " is expiring today or within the next 3 days.";
            } else {
                message = expiringNames.size()
                        + " ingredients are expiring today or within "
                        + "the next 3 days: "
                        + joinNames(expiringNames);
            }

            // Record the date only if the notification was posted.
            boolean notificationPosted =
                    ExpiryNotificationHelper.showExpiryNotification(
                            context,
                            "Pantry expiry reminder",
                            message
                    );

            if (notificationPosted) {
                preferences.edit()
                        .putString(LAST_NOTIFICATION_DATE, todayKey)
                        .apply();
            }

            return Result.success();

        } catch (Exception exception) {
            return Result.retry();

        } finally {
            if (pantryRepository != null) {
                pantryRepository.close();
            }

            if (settingsRepository != null) {
                settingsRepository.close();
            }
        }
    }

    private static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static String joinNames(List<String> names) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < names.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }

            result.append(names.get(i));
        }

        return result.toString();
    }
}